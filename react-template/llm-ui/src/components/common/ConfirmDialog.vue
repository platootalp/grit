<script setup lang="ts">
// 定义组件属性
const props = defineProps<{
  show: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}>();

// 定义事件
const emit = defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();
</script>

<template>
  <div v-if="show" class="confirm-dialog-overlay" @click.self="emit('cancel')">
    <div class="confirm-dialog-panel">
      <div class="confirm-dialog-header">
        <h3 class="text-lg font-medium">{{ title }}</h3>
        <button @click="emit('cancel')" class="text-gray-500 hover:text-gray-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="confirm-dialog-body">
        <p class="text-gray-600 dark:text-gray-300">{{ message }}</p>
      </div>

      <div class="confirm-dialog-footer">
        <button @click="emit('cancel')" class="btn-secondary">
          {{ cancelText || '取消' }}
        </button>
        <button @click="emit('confirm')" class="btn-primary">
          {{ confirmText || '确定' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.confirm-dialog-overlay {
  @apply fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4;
}

.confirm-dialog-panel {
  @apply bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full;
}

.confirm-dialog-header {
  @apply p-4 border-b border-gray-200 dark:border-gray-700 flex justify-between items-center;
}

.confirm-dialog-body {
  @apply p-4;
}

.confirm-dialog-footer {
  @apply p-4 border-t border-gray-200 dark:border-gray-700 flex justify-end space-x-2;
}

.btn-primary {
  @apply px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition-colors;
}

.btn-secondary {
  @apply px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors;
}
</style> 