<script setup lang="ts">
import { computed } from 'vue';
import { useChatStore } from '../../stores/chat';
import type { ChatSession } from '../../types/chat';

// 定义事件
const emit = defineEmits<{
  (e: 'create'): void;
  (e: 'select', id: string): void;
  (e: 'delete', id: string): void;
}>();

// 使用聊天状态管理
const chatStore = useChatStore();

// 按时间排序的会话列表
const sortedSessions = computed(() => {
  if (!chatStore.sessions || chatStore.sessions.length === 0) return [];
  return [...chatStore.sessions].sort((a, b) => {
    const dateA = new Date(a.updatedAt || a.createdAt);
    const dateB = new Date(b.updatedAt || b.createdAt);
    return dateB.getTime() - dateA.getTime();
  });
});

// 获取会话预览
const getSessionPreview = (session: ChatSession) => {
  const lastMessage = session.messages[session.messages.length - 1];
  if (!lastMessage) return '新对话';
  return lastMessage.content.slice(0, 50) + (lastMessage.content.length > 50 ? '...' : '');
};

// 关闭侧边栏（移动端）
const closeSidebar = () => {
  chatStore.sidebarVisible = false;
};
</script>

<template>
  <div class="sidebar-chatgpt">
    <div class="p-3 border-b border-white/20">
      <h1 class="text-lg font-bold text-white mb-2">ChatGPT</h1>
      <button @click="emit('create')" 
        class="w-full bg-[#10a37f] text-white rounded-md h-10 flex items-center justify-center gap-3 transition-colors font-medium hover:bg-[#0e926f]">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path fill-rule="evenodd" clip-rule="evenodd" d="M12 4a1 1 0 0 1 1 1v6h6a1 1 0 1 1 0 2h-6v6a1 1 0 1 1-2 0v-6H5a1 1 0 1 1 0-2h6V5a1 1 0 0 1 1-1z" fill="currentColor" />
        </svg>
        新对话
      </button>
      <button @click="closeSidebar" class="p-2 absolute top-2 right-2 rounded-full hover:bg-gray-600 text-white md:hidden"
        title="关闭侧边栏">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd"
            d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
            clip-rule="evenodd" />
        </svg>
      </button>
    </div>

    <div class="mt-2 px-3 text-xs text-gray-400 font-medium">最近对话</div>

    <div class="flex-1 overflow-y-auto px-2 py-2">
      <div v-for="session in sortedSessions" :key="session.id" @click="emit('select', session.id)" :class="[
        'session-item-chatgpt group',
        { 'bg-gray-800/50': session.id === chatStore.activeSessionId }
      ]">
        <div class="flex items-center gap-2">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="flex-shrink-0">
            <path fill-rule="evenodd" clip-rule="evenodd" d="M12 2.25A9.75 9.75 0 0 1 21.75 12c0 1.733-.452 3.36-1.246 4.771-.34.605-.036 1.372.459 1.866l.948.949a.75.75 0 0 1-.53 1.281H19.5a2.25 2.25 0 0 1-2.25-2.25v-.629c0-.293-.117-.575-.325-.783l-.929-.928a.75.75 0 0 1 0-1.06l.928-.929c.208-.208.325-.49.325-.783v-1.5A6 6 0 0 0 8.82 3.73a.75.75 0 1 1-.57-1.387A7.5 7.5 0 0 1 16.5 8.257V7.5a.75.75 0 0 1 .75-.75h2.25a.75.75 0 0 1 0 1.5h-1.5v.01c0 1.153-.458 2.257-1.27 3.07l-.926.926c-.009.01-.018.02-.027.03a.75.75 0 0 1-1.06 0 4.84 4.84 0 0 0-1.58-1.111c-1.154-.487-2.427-.23-3.317.661l-.641.643a2.25 2.25 0 0 0-.659 1.59v1.927c0 .618.247 1.213.687 1.654l2.778 2.778c.654.654 1.66.89 2.546.609l2.447-.779a4.542 4.542 0 0 1 1.343-.215H19.5a.75.75 0 0 1 0 1.5h-.053c-.595 0-1.194.079-1.78.236l-2.448.778a3.75 3.75 0 0 1-3.492-.651l-2.778-2.777a3.75 3.75 0 0 1-1.078-2.397 5.25 5.25 0 1 1 8.348-5.99.75.75 0 0 1 1.356-.638A7.5 7.5 0 1 0 4.5 12a.75.75 0 0 1-1.5 0A9.75 9.75 0 0 1 12 2.25z" fill="currentColor" />
          </svg>
          <div class="flex-1 min-w-0 text-sm">
            <div class="text-white truncate">{{ session.title }}</div>
          </div>
        </div>

        <button @click.stop="emit('delete', session.id)"
          class="ml-2 p-1 rounded-full text-gray-400 hover:text-red-500 hover:bg-gray-700 opacity-0 group-hover:opacity-100 transition-opacity">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd"
              d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
              clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <div v-if="!sortedSessions.length" class="p-4 text-center text-gray-400">
        没有聊天记录
      </div>
    </div>

    <div class="p-3 border-t border-white/20 text-xs text-gray-400 pt-2">
      <div class="flex justify-between">
        <span>ChatGPT 风格界面</span>
        <span>版本 1.0.0</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.session-item-chatgpt {
  @apply flex items-center p-3 cursor-pointer hover:bg-[#2A2B32] transition-colors duration-150 rounded-md text-white mb-1 relative;
}

.sidebar-chatgpt {
  @apply flex flex-col h-full;
}

@media (max-width: 768px) {
  .session-item-chatgpt:hover button {
    @apply opacity-100;
  }
}
</style>