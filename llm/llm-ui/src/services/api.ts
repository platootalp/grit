import axios from 'axios';
import type { ChatSession, Message, ChatSettings } from '../types/chat';

// API基础设置
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000/api';
const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:3000/ws';

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
            // 在真实环境中，这里会调用API
            // const response = await api.get('/conversations');
            // return response.data;

            // 返回模拟数据
            return mockSessions;
        } catch (error) {
            console.error('获取会话列表失败:', error);
            throw error;
        }
    },

    // 获取单个会话的消息
    getMessages: async (conversationId: string): Promise<Message[]> => {
        try {
            // 在真实环境中，这里会调用API
            // const response = await api.get(`/conversations/${conversationId}`);
            // return response.data.messages;

            // 返回模拟数据
            const session = mockSessions.find(s => s.id === conversationId);
            return session?.messages || [];
        } catch (error) {
            console.error('获取消息失败:', error);
            throw error;
        }
    },

    // 创建新会话
    createConversation: async (title: string): Promise<{ id: string; title: string }> => {
        try {
            // 在真实环境中，这里会调用API
            // const response = await api.post('/conversations', { title });
            // return response.data;

            // 返回模拟数据
            return {
                id: 'mock-session-' + Date.now().toString(36),
                title
            };
        } catch (error) {
            console.error('创建会话失败:', error);
            throw error;
        }
    },

    // 发送消息
    sendMessage: async (
        message: string,
        conversationId?: string,
        options: { modelName?: string; temperature?: number; stream?: boolean } = {}
    ): Promise<Message> => {
        try {
            // 在真实环境中，这里会调用API
            // const response = await api.post('/chat', {
            //     message,
            //     conversation_id: conversationId,
            //     model: options.modelName,
            //     temperature: options.temperature,
            //     stream: options.stream
            // });
            // return response.data;

            // 返回模拟数据
            const response = await generateMockResponse(message);
            return {
                id: 'msg-' + Date.now().toString(36),
                role: 'assistant',
                content: response,
                timestamp: Date.now()
            };
        } catch (error) {
            console.error('发送消息失败:', error);
            throw error;
        }
    },

    // 删除会话
    deleteConversation: async (conversationId: string): Promise<void> => {
        try {
            // 在真实环境中，这里会调用API
            // await api.delete(`/conversations/${conversationId}`);

            // 模拟删除
            console.log(`已删除会话: ${conversationId}`);
        } catch (error) {
            console.error('删除会话失败:', error);
            throw error;
        }
    },

    // 更新会话标题
    updateConversationTitle: async (conversationId: string, title: string): Promise<void> => {
        try {
            // 在真实环境中，这里会调用API
            // await api.patch(`/conversations/${conversationId}`, { title });

            // 模拟更新
            console.log(`已更新会话标题: ${conversationId} => ${title}`);
        } catch (error) {
            console.error('更新会话标题失败:', error);
            throw error;
        }
    },

    // 清空会话消息
    clearMessages: async (conversationId: string): Promise<void> => {
        try {
            // 在真实环境中，这里会调用API
            // await api.delete(`/conversations/${conversationId}/messages`);

            // 模拟清空
            console.log(`已清空会话消息: ${conversationId}`);
        } catch (error) {
            console.error('清空消息失败:', error);
            throw error;
        }
    }
};

// 创建流式聊天服务
export const createStreamingChatService = (
    onChunk: (content: string) => void,
    onComplete?: () => void
) => {
    // 模拟流式响应
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
            console.log('关闭流式连接');
        }
    };
}; 