<script setup lang="ts">
import { computed } from 'vue'
import { useChatStore } from '../../stores/chat'
import { marked } from 'marked'
import hljs from 'highlight.js'
import type { MessageRole } from '../../types/chat'

const props = defineProps<{
  role: MessageRole
  content: string
  error?: boolean
}>()

const chatStore = useChatStore()

const formattedContent = computed(() => {
  if (!props.content) return ''
  
  const renderer = new marked.Renderer()
  
  // 通过设置renderer来处理代码高亮，而不是直接使用highlight选项
  renderer.code = (code, language) => {
    const validLanguage = language && hljs.getLanguage(language) ? language : 'plaintext'
    const highlightedCode = hljs.highlight(validLanguage, code).value
    return `<pre><code class="hljs ${validLanguage}">${highlightedCode}</code></pre>`
  }
  
  marked.setOptions({ renderer })
  
  return marked(props.content)
})

const messageClass = computed(() => {
  return {
    'bg-gray-50 dark:bg-[#343541]': props.role === 'user',
    'bg-white dark:bg-[#444654]': props.role === 'assistant',
    'bg-yellow-50 dark:bg-gray-800': props.role === 'system',
    'text-red-500': props.error
  }
})
</script>

<template>
  <div :class="messageClass" class="py-8 border-b border-black/10 dark:border-gray-700/50 group">
    <div class="flex items-start gap-4 w-full">
      <div class="flex-shrink-0 pt-0.5">
        <!-- 用户头像 -->
        <div v-if="role === 'user'" class="w-8 h-8 rounded-full bg-[#5436DA] flex items-center justify-center">
          <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
          </svg>
        </div>
        
        <!-- AI助手头像 -->
        <div v-else-if="role === 'assistant'" class="w-8 h-8 rounded-sm bg-[#10a37f] flex items-center justify-center">
          <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 00-2.456 2.456zM16.894 20.567L16.5 21.75l-.394-1.183a2.25 2.25 0 00-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 001.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 001.423 1.423l1.183.394-1.183.394a2.25 2.25 0 00-1.423 1.423z" />
          </svg>
        </div>
        
        <!-- 系统消息头像 -->
        <div v-else class="w-8 h-8 rounded-sm bg-yellow-500 flex items-center justify-center">
          <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
          </svg>
        </div>
      </div>
      <div class="flex-grow prose dark:prose-invert max-w-none overflow-hidden">
        <div class="text-xs text-gray-500 dark:text-gray-400 mb-2">
          {{ role === 'user' ? '你' : role === 'assistant' ? `${chatStore.settings.modelName || 'AI助手'}` : '系统' }}
        </div>
        <div v-html="formattedContent" class="leading-relaxed text-[15px]"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.hljs) {
  background: transparent;
  padding: 0;
}

:deep(pre) {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 0.5rem;
  padding: 1rem;
  margin: 1rem 0;
  overflow-x: auto;
}

:deep(code) {
  background: rgba(0, 0, 0, 0.05);
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

:deep(pre code) {
  background: transparent;
  padding: 0;
}

:deep(p) {
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

:deep(ul), :deep(ol) {
  padding-left: 1.5em;
  margin-top: 0.75em;
  margin-bottom: 0.75em;
}

.dark :deep(pre) {
  background: rgba(255, 255, 255, 0.05);
}

.dark :deep(code) {
  background: rgba(255, 255, 255, 0.05);
}

/* 调整块引用样式 */
:deep(blockquote) {
  border-left: 3px solid rgba(0, 0, 0, 0.1);
  padding-left: 1em;
  margin-left: 0;
  color: rgba(0, 0, 0, 0.6);
}

.dark :deep(blockquote) {
  border-left-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
}
</style> 