<script setup lang="ts">
import { computed } from 'vue';
import type { Message } from '../../types/chat';
import { useMarkdown } from '../../composables/useMarkdown';

const props = defineProps<{
    message: Message;
}>();

const { renderMarkdown } = useMarkdown();

// 消息类型样式
const messageClass = computed(() => {
    return props.message.role === 'user'
        ? 'message-bubble-user'
        : 'message-bubble-assistant';
});

// 角色图标
const roleIcon = computed(() => {
    if (props.message.role === 'user') {
        return `
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-700 dark:text-gray-300" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
        `;
    } else {
        return `
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-indigo-600 dark:text-indigo-400" viewBox="0 0 20 20" fill="currentColor">
                <path d="M2 10a8 8 0 018-8v8h8a8 8 0 11-16 0z" />
                <path d="M12 2.252A8.014 8.014 0 0117.748 8H12V2.252z" />
            </svg>
        `;
    }
});

// 格式化时间戳
const formattedTime = computed(() => {
    if (!props.message.timestamp) return '';
    const date = new Date(props.message.timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
});

// 渲染消息内容（支持 Markdown）
const renderedContent = computed(() => {
    return renderMarkdown(props.message.content);
});
</script>

<template>
    <div class="message-container">
        <div class="flex items-start space-x-4 px-4">
            <!-- 头像 -->
            <div class="flex-shrink-0" v-html="roleIcon"></div>

            <!-- 消息内容 -->
            <div class="flex-1 space-y-1">
                <div :class="messageClass" v-html="renderedContent"></div>
                <div class="text-xs text-gray-500 dark:text-gray-400 pl-2" v-if="formattedTime">
                    {{ formattedTime }}
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.message-container {
    @apply py-4;
}

:deep(.message-bubble-user),
:deep(.message-bubble-assistant) {
    @apply text-sm max-w-none;
}

:deep(.message-bubble-user) pre,
:deep(.message-bubble-assistant) pre {
    @apply bg-gray-800 dark:bg-gray-900 text-sm rounded-md p-4 my-2 overflow-x-auto;
}

:deep(.message-bubble-user) code,
:deep(.message-bubble-assistant) code {
    @apply bg-gray-100 dark:bg-gray-800 px-1 py-0.5 rounded text-sm;
}

:deep(.message-bubble-user) pre code,
:deep(.message-bubble-assistant) pre code {
    @apply bg-transparent p-0;
}

:deep(.message-bubble-user) table,
:deep(.message-bubble-assistant) table {
    @apply border-collapse my-4;
}

:deep(.message-bubble-user) th,
:deep(.message-bubble-assistant) th,
:deep(.message-bubble-user) td,
:deep(.message-bubble-assistant) td {
    @apply border border-gray-300 dark:border-gray-700 px-4 py-2;
}

:deep(.message-bubble-user) th,
:deep(.message-bubble-assistant) th {
    @apply bg-gray-100 dark:bg-gray-800;
}
</style> 