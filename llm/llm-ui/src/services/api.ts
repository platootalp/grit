import axios from 'axios';
import type { ChatSession, Message, ChatSettings } from '../types/chat';

// API基础设置
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws';

// 增加一个环境变量判断，如果没有后端服务则使用模拟数据
const useMockData = true; // 暂时强制使用模拟数据，直到后端API准备好

// Axios实例
const api = axios.create({
    baseURL: apiBaseUrl,
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json',
    }
});

// 模拟数据
const mockSessions: ChatSession[] = [
    {
        id: 'mock-session-1',
        title: '关于人工智能的讨论',
        messages: [
            {
                id: 'msg-1',
                role: 'user',
                content: '什么是人工智能?',
                timestamp: Date.now() - 3600000
            },
            {
                id: 'msg-2',
                role: 'assistant',
                content: '人工智能（AI）是计算机科学的一个分支，它关注创建能够执行通常需要人类智能的任务的系统。这包括视觉识别、语音识别、决策制定、语言翻译等。人工智能可以分为两大类：弱人工智能（专注于执行特定任务）和强人工智能（具有与人类相当或超越人类的认知能力）。',
                timestamp: Date.now() - 3500000
            }
        ],
        createdAt: Date.now() - 3600000,
        updatedAt: Date.now() - 3500000
    },
    {
        id: 'mock-session-2',
        title: '网站开发问题',
        messages: [
            {
                id: 'msg-3',
                role: 'user',
                content: '如何创建一个响应式网站?',
                timestamp: Date.now() - 86400000
            },
            {
                id: 'msg-4',
                role: 'assistant',
                content: '创建响应式网站有几种方法：\n\n1. 使用CSS媒体查询来适应不同屏幕尺寸\n2. 采用弹性布局（Flexbox）和网格布局（Grid）\n3. 使用Bootstrap或Tailwind等框架\n4. 实现移动优先设计原则\n5. 使用相对单位（如%、em、rem）而不是固定像素\n6. 测试不同设备上的显示效果',
                timestamp: Date.now() - 86300000
            }
        ],
        createdAt: Date.now() - 86400000,
        updatedAt: Date.now() - 86300000
    }
];

// 模拟AI响应
const generateMockResponse = (message: string): Promise<string> => {
    return new Promise((resolve) => {
        setTimeout(() => {
            if (message.includes('你好') || message.includes('嗨') || message.includes('问好')) {
                resolve('你好！我是AI助手，有什么我可以帮助你的吗？');
            } else if (message.includes('时间') || message.includes('日期') || message.includes('几点')) {
                const now = new Date();
                resolve(`现在的时间是 ${now.toLocaleTimeString()}，日期是 ${now.toLocaleDateString()}`);
            } else if (message.includes('天气')) {
                resolve('很抱歉，我目前无法获取实时天气信息，因为我是在本地模式下运行的。在真实环境中，我可以连接到天气API来提供准确的天气预报。');
            } else if (message.length < 10) {
                resolve('请提供更多的信息，这样我才能更好地帮助你。');
            } else {
                resolve(`我收到了你的消息："${message}"。这是一个模拟的回应，在本地模式下运行。要获取真实的AI回应，请在设置中配置API密钥。`);
            }
        }, 1000); // 模拟网络延迟
    });
};

// API服务
export const apiService = {
    // 获取会话列表
    getConversations: async (): Promise<ChatSession[]> => {
        try {
            // 如果使用模拟数据，直接返回模拟会话列表
            if (useMockData) {
                return mockSessions;
            }

            // 调用真实API
            const response = await api.get('/conversations');
            return response.data;
        } catch (error) {
            console.error('获取会话列表失败:', error);
            // 出错时返回模拟数据
            return mockSessions;
        }
    },

    // 获取单个会话的消息
    getMessages: async (conversationId: string): Promise<Message[]> => {
        try {
            // 如果使用模拟数据，查找并返回模拟会话消息
            if (useMockData) {
                const session = mockSessions.find(s => s.id === conversationId);
                return session?.messages || [];
            }

            // 调用真实API
            const response = await api.get(`/conversations/${conversationId}/messages`);
            return response.data;
        } catch (error) {
            console.error('获取消息失败:', error);
            // 出错时查找并返回模拟数据
            const session = mockSessions.find(s => s.id === conversationId);
            return session?.messages || [];
        }
    },

    // 创建新会话
    createConversation: async (title: string): Promise<{ id: string; title: string }> => {
        try {
            // 如果使用模拟数据，创建一个模拟会话ID
            if (useMockData) {
                return {
                    id: 'mock-session-' + Date.now().toString(36),
                    title
                };
            }

            // 调用真实API
            const response = await api.post('/conversations', { title });
            return response.data;
        } catch (error) {
            console.error('创建会话失败:', error);
            // 出错时返回模拟数据
            return {
                id: 'mock-session-' + Date.now().toString(36),
                title
            };
        }
    },

    // 发送消息
    sendMessage: async (
        message: string,
        conversationId?: string,
        options: { modelName?: string; temperature?: number; stream?: boolean } = {}
    ): Promise<Message> => {
        try {
            // 如果使用模拟数据，生成模拟响应
            if (useMockData) {
                const response = await generateMockResponse(message);
                return {
                    id: 'msg-' + Date.now().toString(36),
                    role: 'assistant',
                    content: response,
                    timestamp: Date.now()
                };
            }

            // 调用真实API
            const response = await api.post('/chat', {
                message,
                conversation_id: conversationId,
                model: options.modelName,
                temperature: options.temperature,
                stream: options.stream
            });
            return response.data;
        } catch (error) {
            console.error('发送消息失败:', error);
            // 出错时生成模拟响应
            const response = await generateMockResponse(message);
            return {
                id: 'msg-' + Date.now().toString(36),
                role: 'assistant',
                content: response,
                timestamp: Date.now()
            };
        }
    },

    // 删除会话
    deleteConversation: async (conversationId: string): Promise<void> => {
        try {
            // 如果使用模拟数据，直接打印日志
            if (useMockData) {
                console.log(`已删除会话: ${conversationId}`);
                return;
            }

            // 调用真实API
            await api.delete(`/conversations/${conversationId}`);
        } catch (error) {
            console.error('删除会话失败:', error);
        }
    },

    // 更新会话标题
    updateConversationTitle: async (conversationId: string, title: string): Promise<void> => {
        try {
            // 如果使用模拟数据，直接打印日志
            if (useMockData) {
                console.log(`已更新会话标题: ${conversationId} => ${title}`);
                return;
            }

            // 调用真实API
            await api.patch(`/conversations/${conversationId}`, { title });
        } catch (error) {
            console.error('更新会话标题失败:', error);
        }
    },

    // 清空会话消息
    clearMessages: async (conversationId: string): Promise<void> => {
        try {
            // 如果使用模拟数据，直接打印日志
            if (useMockData) {
                console.log(`已清空会话消息: ${conversationId}`);
                return;
            }

            // 调用真实API
            await api.delete(`/conversations/${conversationId}/messages`);
        } catch (error) {
            console.error('清空消息失败:', error);
        }
    }
};

// 创建流式聊天服务
export const createStreamingChatService = (
    onChunk: (content: string) => void,
    onComplete?: () => void
) => {
    let socket: WebSocket | null = null;

    // 如果使用模拟数据，使用模拟的流式响应
    if (useMockData) {
        return {
            sendMessage: async (
                message: string,
                conversationId?: string,
                options: { modelName?: string; temperature?: number } = {}
            ): Promise<void> => {
                try {
                    const fullResponse = await generateMockResponse(message);

                    // 模拟逐字发送
                    const words = fullResponse.split(' ');
                    for (const word of words) {
                        await new Promise((resolve) => setTimeout(resolve, 100 + Math.random() * 200));
                        onChunk(word + ' ');
                    }

                    if (onComplete) {
                        onComplete();
                    }
                } catch (error) {
                    console.error('流式聊天失败:', error);
                    throw error;
                }
            },

            // 关闭连接
            close: () => {
                console.log('关闭模拟流式连接');
            }
        };
    }

    const connectWebSocket = (
        message: string,
        conversationId?: string,
        options: { modelName?: string; temperature?: number } = {}
    ) => {
        // 创建WebSocket连接
        const queryParams = new URLSearchParams();
        if (conversationId) queryParams.append('conversation_id', conversationId);
        if (options.modelName) queryParams.append('model', options.modelName);
        if (options.temperature) queryParams.append('temperature', options.temperature.toString());

        const wsEndpoint = `${wsUrl}/chat?${queryParams.toString()}`;
        socket = new WebSocket(wsEndpoint);

        socket.onopen = () => {
            // 连接建立后发送消息
            if (socket) {  // 修复socket可能为null的问题
                socket.send(JSON.stringify({ message }));
            }
        };

        socket.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);

                // 处理收到的数据块
                if (data.content) {
                    onChunk(data.content);
                }

                // 处理完成事件
                if (data.done && onComplete) {
                    onComplete();
                    if (socket) {
                        socket.close();
                    }
                }
            } catch (error) {
                console.error('解析WebSocket消息失败:', error);
            }
        };

        socket.onerror = (error) => {
            console.error('WebSocket错误:', error);
        };

        socket.onclose = () => {
            console.log('WebSocket连接已关闭');
        };
    };

    return {
        sendMessage: async (
            message: string,
            conversationId?: string,
            options: { modelName?: string; temperature?: number } = {}
        ): Promise<void> => {
            try {
                connectWebSocket(message, conversationId, options);
            } catch (error) {
                console.error('流式聊天失败:', error);
                throw error;
            }
        },

        // 关闭连接
        close: () => {
            if (socket && socket.readyState === WebSocket.OPEN) {
                socket.close();
            }
        }
    };
}; 