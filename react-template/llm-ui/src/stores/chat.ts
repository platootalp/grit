import { defineStore } from 'pinia';
import { ref, computed, reactive } from 'vue';
import type { Message, ChatSession, CreateMessageRequest, ChatSettings } from '../types/chat';
import { apiService, createStreamingChatService } from '../services/api';

/**
 * 生成唯一ID
 */
function generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substring(2);
}

/**
 * 聊天状态管理
 */
export const useChatStore = defineStore('chat', () => {
    // 聊天会话列表
    const sessions = ref<ChatSession[]>([]);

    // 当前活动的会话ID
    const activeSessionId = ref<string | null>(null);

    // 加载状态
    const loading = ref(false);

    // 是否使用流式响应
    const useStreaming = ref(true);

    // 当前正在流式传输的消息
    const streamingMessageId = ref<string | null>(null);

    // 流式传输的临时内容
    const streamingContent = ref('');

    // 侧边栏可见性状态
    const sidebarVisible = ref(true);

    // 聊天设置
    const settings = ref<ChatSettings>({
        apiKey: '',
        modelName: 'GPT-3.5',
        temperature: 0.7,
        maxTokens: 2048
    });

    // 错误信息
    const error = ref<string | null>(null);

    // 计算当前活动的会话
    const activeSession = computed(() => {
        if (!activeSessionId.value) return null;
        return sessions.value.find(session => session.id === activeSessionId.value) || null;
    });

    // 初始化 - 从后端加载会话
    const initializeStore = async () => {
        try {
            loading.value = true;
            error.value = null;

            // 如果启用了API，从后端获取会话列表
            if (settings.value.apiKey) {
                const conversations = await apiService.getConversations();
                if (conversations && conversations.length > 0) {
                    sessions.value = conversations;
                    if (!activeSessionId.value && conversations.length > 0) {
                        activeSessionId.value = conversations[0].id;
                    }
                }
            }
        } catch (err) {
            console.error('初始化会话失败:', err);
            error.value = '无法加载会话历史';
        } finally {
            loading.value = false;
        }
    };

    // 创建新的聊天会话
    const createSession = async (title = '新的对话') => {
        try {
            loading.value = true;

            let newSession: ChatSession;

            // 如果启用了API，使用后端创建会话
            if (settings.value.apiKey) {
                const result = await apiService.createConversation(title);
                newSession = {
                    id: result.id,
                    title: result.title,
                    messages: [],
                    createdAt: Date.now(),
                    updatedAt: Date.now()
                };
            } else {
                // 本地创建会话
                const id = generateId();
                newSession = {
                    id,
                    title,
                    messages: [],
                    createdAt: Date.now(),
                    updatedAt: Date.now()
                };
            }

            sessions.value.unshift(newSession);
            activeSessionId.value = newSession.id;
            return newSession;
        } catch (err) {
            console.error('创建会话失败:', err);
            error.value = '创建新会话失败';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    // 切换活动会话
    const setActiveSession = async (sessionId: string | null) => {
        try {
            activeSessionId.value = sessionId;

            if (!sessionId) return;

            const session = sessions.value.find(s => s.id === sessionId);

            // 如果会话存在但没有消息，并且启用了API，从后端获取消息
            if (session && session.messages.length === 0 && settings.value.apiKey) {
                loading.value = true;
                const messages = await apiService.getMessages(sessionId);
                if (messages && messages.length > 0) {
                    session.messages = messages;
                }
            }
        } catch (err) {
            console.error('切换会话失败:', err);
            error.value = '无法加载会话消息';
        } finally {
            loading.value = false;
        }
    };

    // 删除会话
    const deleteSession = async (sessionId: string) => {
        try {
            loading.value = true;

            // 如果启用了API，使用后端删除会话
            if (settings.value.apiKey) {
                await apiService.deleteConversation(sessionId);
            }

            const index = sessions.value.findIndex(session => session.id === sessionId);
            if (index !== -1) {
                sessions.value.splice(index, 1);

                // 如果删除的是当前活动会话，切换到其他会话
                if (activeSessionId.value === sessionId) {
                    activeSessionId.value = sessions.value.length > 0 ? sessions.value[0].id : null;
                }
            }
        } catch (err) {
            console.error('删除会话失败:', err);
            error.value = '删除会话失败';
        } finally {
            loading.value = false;
        }
    };

    // 添加消息到当前会话
    const addMessage = (request: CreateMessageRequest) => {
        if (!activeSession.value) {
            createSession();
        }

        const message: Message = {
            id: generateId(),
            role: request.role,
            content: request.content,
            timestamp: Date.now()
        };

        activeSession.value!.messages.push(message);
        activeSession.value!.updatedAt = Date.now();

        return message;
    };

    // 更新会话标题
    const updateSessionTitle = async (sessionId: string, title: string) => {
        try {
            const session = sessions.value.find(s => s.id === sessionId);
            if (session) {
                session.title = title;
                session.updatedAt = Date.now();

                // 如果启用了API，更新后端会话标题
                if (settings.value.apiKey) {
                    await apiService.updateConversationTitle(sessionId, title);
                }
            }
        } catch (err) {
            console.error('更新会话标题失败:', err);
            error.value = '无法更新会话标题';
        }
    };

    // 处理流式响应
    const handleStreamingResponse = (content: string) => {
        if (!streamingMessageId.value) {
            const message = addMessage({
                role: 'assistant',
                content: ''
            });
            streamingMessageId.value = message.id;
        }

        // 累积流式内容
        streamingContent.value += content;

        // 更新消息内容
        const message = activeSession.value!.messages.find(m => m.id === streamingMessageId.value);
        if (message) {
            message.content = streamingContent.value;
        }
    };

    // 完成流式响应
    const completeStreamingResponse = () => {
        streamingMessageId.value = null;
        streamingContent.value = '';
    };

    // 切换侧边栏可见性
    const toggleSidebar = () => {
        sidebarVisible.value = !sidebarVisible.value;
    };

    // 创建新的会话（别名）
    const createNewSession = (title = '新的对话') => {
        return createSession(title);
    };

    // 切换流式响应模式
    const toggleStreamingMode = () => {
        useStreaming.value = !useStreaming.value;
    };

    // 发送消息并获取回复
    const sendMessage = async (content: string) => {
        if (!content.trim()) return;

        try {
            // 重置错误状态
            error.value = null;

            // 添加用户消息
            addMessage({
                role: 'user',
                content
            });

            // 设置加载状态
            loading.value = true;

            if (settings.value.apiKey && useStreaming.value) {
                // 使用流式响应
                const streamingService = createStreamingChatService(
                    handleStreamingResponse,
                    completeStreamingResponse
                );
                streamingService.sendMessage(
                    content,
                    activeSessionId.value ?? undefined,
                    {
                        modelName: settings.value.modelName,
                        temperature: settings.value.temperature
                    }
                );
            } else if (settings.value.apiKey) {
                // 使用普通API响应
                const response = await apiService.sendMessage(
                    content,
                    activeSessionId.value ?? undefined,
                    {
                        modelName: settings.value.modelName,
                        temperature: settings.value.temperature
                    }
                );

                // 添加助手回复
                addMessage({
                    role: 'assistant',
                    content: response.content
                });
            } else {
                // 本地模拟响应
                // 模拟API调用延迟
                await new Promise(resolve => setTimeout(resolve, 1000));

                // 模拟助手回复
                addMessage({
                    role: 'assistant',
                    content: `这是一个模拟的回复，当前时间：${new Date().toLocaleTimeString()}\n\n您的消息是：${content}`
                });
            }
        } catch (err) {
            console.error('发送消息失败:', err);
            error.value = '发送消息失败，请重试';

            // 添加错误提示作为系统消息
            addMessage({
                role: 'system',
                content: '消息发送失败，请检查网络连接或API密钥设置'
            });
        } finally {
            loading.value = false;
        }
    };

    // 更新设置
    const updateSettings = async (newSettings: Partial<ChatSettings>) => {
        try {
            settings.value = { ...settings.value, ...newSettings };

            // 如果API密钥被修改，重新初始化
            if (newSettings.apiKey) {
                await initializeStore();
            }
        } catch (err) {
            console.error('更新设置失败:', err);
            error.value = '无法更新设置';
        }
    };

    // 清空会话消息
    const clearMessages = async (sessionId: string) => {
        try {
            const session = sessions.value.find(s => s.id === sessionId);
            if (session) {
                session.messages = [];
                session.updatedAt = Date.now();

                // 如果启用了API，清空后端会话消息
                if (settings.value.apiKey) {
                    await apiService.clearMessages(sessionId);
                }
            }
        } catch (err) {
            console.error('清空消息失败:', err);
            error.value = '无法清空会话消息';
        }
    };

    return {
        sessions,
        activeSessionId,
        activeSession,
        loading,
        settings,
        error,
        useStreaming,
        sidebarVisible,
        streamingMessageId,
        streamingContent,
        initializeStore,
        createSession,
        setActiveSession,
        deleteSession,
        addMessage,
        updateSessionTitle,
        sendMessage,
        updateSettings,
        clearMessages,
        toggleStreamingMode,
        toggleSidebar,
        createNewSession
    };
}, {
    persist: {
        key: 'chat-store',
        storage: localStorage,
    },
}); 