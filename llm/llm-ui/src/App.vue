<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from './stores/chat'

const chatStore = useChatStore()
const router = useRouter()

// 检测系统颜色模式并应用
onMounted(() => {
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    document.documentElement.classList.add('dark')
  }

  // 初始化聊天 store
  chatStore.initializeStore()
})
</script>

<template>
  <div class="welcome-container">
    <router-view></router-view>
  </div>
</template>

<style>
/* 基础样式 */
@import 'tailwindcss/base';
@import 'tailwindcss/components';
@import 'tailwindcss/utilities';

/* 自定义组件样式 */
@layer components {
  .btn {
    @apply rounded-md flex items-center px-4 py-2 font-medium focus:outline-none transition-colors;
  }

  .btn-primary {
    @apply bg-[#10a37f] text-white hover:bg-[#0e926f] dark:bg-[#10a37f] dark:hover:bg-[#0e926f];
  }

  .btn-secondary {
    @apply bg-gray-100 text-gray-800 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600;
  }
}

/* 暗色模式表单元素 */
.dark input,
.dark textarea,
.dark select {
  @apply bg-gray-700 border-gray-600 text-white;
}

/* 亮色模式滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  @apply bg-gray-100;
}

::-webkit-scrollbar-thumb {
  @apply bg-gray-300 rounded-full hover:bg-gray-400;
}

/* 暗色模式滚动条 */
.dark ::-webkit-scrollbar-track {
  @apply bg-gray-800;
}

.dark ::-webkit-scrollbar-thumb {
  @apply bg-gray-700 hover:bg-gray-600;
}

html,
body {
  @apply overflow-hidden;
  font-family: 'Söhne', ui-sans-serif, system-ui, -apple-system, 'Segoe UI', Roboto, Ubuntu, Cantarell, 'Noto Sans', sans-serif;
}

.welcome-container {
  @apply h-screen bg-white dark:bg-[#343541] text-gray-900 dark:text-gray-100 overflow-hidden;
}

/* 侧边栏样式 */
.sidebar-chatgpt {
  @apply fixed top-0 left-0 w-64 h-full bg-[#202123] border-r border-gray-200 dark:border-gray-700 flex flex-col overflow-hidden z-20;
}

/* 主要内容区域样式 */
.main-view-chatgpt {
  @apply transition-all duration-300 ease-in-out w-full;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .main-view-chatgpt {
    @apply ml-0 !important;
  }
}

/* 侧边栏过渡动画 */
.sidebar-enter-active,
.sidebar-leave-active {
  @apply transition-all duration-300 ease-in-out;
}

.sidebar-enter-from,
.sidebar-leave-to {
  @apply -translate-x-full;
}

.sidebar-enter-to,
.sidebar-leave-from {
  @apply translate-x-0;
}
</style>
