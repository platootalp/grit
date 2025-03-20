import axios from 'axios';
import { marked } from 'marked';
import type { ChatSession, Message, ChatSettings } from '../types/chat';

// API基础设置
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '';
const wsUrl = import.meta.env.VITE_WS_URL || '';

// 增加一个环境变量判断，如果没有后端服务则使用模拟数据
const useMockData = false; // 改为false，连接到实际后端API

// 添加调试信息
console.log('API基础URL:', apiBaseUrl);
console.log('WebSocket URL:', wsUrl);

// 模拟响应生成函数
const generateMockResponse = async (message: string): Promise<string> => {
    return `这是对 "${message}" 的模拟响应。`;
};

export const createStreamingChatService = (
    onChunk: (content: string) => void,
    onComplete?: () => void
) => {
    let eventSource: EventSource | null = null;

    // 配置 marked
    marked.setOptions({
        breaks: true, // 将换行符转换为 <br>
        gfm: true,    // 启用 GitHub 风格的 Markdown
    });

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
                    if (typeof fullResponse === 'string') {
                        const htmlContent = marked.parse(fullResponse);
                        onChunk(htmlContent);
                    }

                    if (onComplete) {
                        onComplete();
                    }
                } catch (error) {
                    console.error('流式聊天失败:', error);
                    throw error;
                }
            },
            close: () => {
                console.log('关闭模拟流式连接');
            }
        };
    }

    return {
        sendMessage: async (
            message: string,
            conversationId?: string,
            options: { modelName?: string; temperature?: number } = {}
        ): Promise<void> => {
            try {
                // 关闭之前的连接
                if (eventSource) {
                    eventSource.close();
                }

                // 创建 SSE 连接
                const params = new URLSearchParams({
                    userMessage: message
                });
                const url = `${apiBaseUrl}/chat/stream?${params.toString()}`;
                eventSource = new EventSource(url);

                let accumulatedText = '';
                let updateTimeout: NodeJS.Timeout | null = null;

                const updateContent = () => {
                    try {
                        // 使用 DOMParser 解析 HTML 实体
                        const parser = new DOMParser();
                        const decodedText = parser.parseFromString(accumulatedText, 'text/html').body.textContent || '';

                        // 解析 Markdown 并确保返回字符串类型
                        const htmlContent = marked.parse(decodedText);
                        if (typeof htmlContent === 'string') {
                            onChunk(htmlContent);
                        } else {
                            console.error('Markdown 解析结果不是字符串类型');
                            onChunk(decodedText); // 降级处理：直接显示原文
                        }
                    } catch (error) {
                        console.error('解析内容失败:', error);
                        onChunk(accumulatedText); // 降级处理：直接显示原文
                    }
                };

                eventSource.onmessage = (event) => {
                    if (typeof event.data === 'string') {
                        accumulatedText += event.data;

                        // 清除之前的定时器
                        if (updateTimeout) {
                            clearTimeout(updateTimeout);
                        }

                        // 设置一个短暂的延迟后更新内容
                        updateTimeout = setTimeout(updateContent, 10);
                    }
                };

                eventSource.onerror = (error) => {
                    console.error('SSE 错误:', error);
                    eventSource?.close();
                    if (onComplete) {
                        onComplete();
                    }
                };
            } catch (error) {
                console.error('流式聊天失败:', error);
                throw error;
            }
        },
        close: () => {
            if (eventSource) {
                eventSource.close();
            }
        }
    };
}; 