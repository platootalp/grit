import { marked } from 'marked';
import 'highlight.js/styles/github-dark.css';

export function useMarkdown() {
    // 将Markdown文本转换为HTML
    const renderMarkdown = (text: string): string => {
        if (!text) return '';
        try {
            return marked.parse(text) as string;
        } catch (error) {
            console.error('Markdown parsing error:', error);
            return `<p>${text}</p>`;
        }
    };

    return {
        renderMarkdown
    };
} 