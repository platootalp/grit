<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useChatStore } from '../../stores/chat';

// 定义事件
const emit = defineEmits<{
    (e: 'send', content: string): void;
}>();

const chatStore = useChatStore();

// 输入文本
const userInput = ref('');
const textareaRef = ref<HTMLTextAreaElement | null>(null);

// 自动调整文本框高度
const adjustTextareaHeight = () => {
    if (textareaRef.value) {
        textareaRef.value.style.height = 'auto';
        textareaRef.value.style.height = `${textareaRef.value.scrollHeight}px`;
    }
};

// 处理输入事件
const handleInput = () => {
    adjustTextareaHeight();
};

// 处理按键事件
const handleKeydown = (e: KeyboardEvent) => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
        e.preventDefault();
        sendMessage();
    }
};

// 发送消息
const sendMessage = () => {
    const content = userInput.value.trim();
    if (!content || chatStore.loading) return;

    emit('send', content);
    userInput.value = '';
    
    // 重置文本框高度
    if (textareaRef.value) {
        textareaRef.value.style.height = 'auto';
    }
};

onMounted(() => {
    // 初始化文本框高度
    adjustTextareaHeight();
});
</script>

<template>
    <div class="chat-input-container">
        <div class="relative w-full">
            <div class="relative flex flex-col w-full py-3 px-3 border border-black/10 bg-white dark:border-gray-900/50 dark:bg-[#40414F] dark:text-white rounded-xl shadow-[0_0_15px_rgba(0,0,0,0.10)]">
                <textarea ref="textareaRef" v-model="userInput" @keydown="handleKeydown" @input="handleInput"
                    placeholder="输入消息，按 Ctrl+Enter 发送..." rows="1"
                    class="chat-input-chatgpt m-0 w-full resize-none border-0 bg-transparent p-0 pr-10 focus:ring-0 focus-visible:ring-0 dark:bg-transparent pl-2 md:pl-0 max-h-[200px] text-base leading-6"
                    style="height: 24px; overflow-y: hidden;"></textarea>

                <button @click="sendMessage"
                    class="absolute p-1 rounded-md text-sm bottom-2.5 right-2.5 enabled:hover:bg-gray-100 dark:enabled:hover:text-gray-400 dark:enabled:hover:bg-gray-900 disabled:opacity-40 dark:disabled:opacity-30 text-gray-500 transition-colors"
                    :disabled="!userInput.trim() || chatStore.loading" aria-label="发送消息">
                    <svg stroke="currentColor" fill="none" stroke-width="2" viewBox="0 0 24 24" stroke-linecap="round" stroke-linejoin="round" class="h-4 w-4" height="1em" width="1em" xmlns="http://www.w3.org/2000/svg">
                        <line x1="22" y1="2" x2="11" y2="13"></line>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                </button>
            </div>
        </div>

        <div class="text-xs text-center text-gray-500 dark:text-gray-400 mt-2 px-3">
            <p>AI助手由 ChatGPT 提供支持。信息可能存在偏差或不准确。</p>
        </div>
    </div>
</template>

<style scoped>
.chat-input-container {
    @apply w-full;
}

.chat-input-chatgpt:focus {
    outline: none !important;
    box-shadow: none !important;
}

/* 添加响应式文本框高度过渡 */
textarea {
    transition: height 0.1s ease-out;
}
</style>