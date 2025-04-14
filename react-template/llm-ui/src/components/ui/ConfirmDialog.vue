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

// 点击确认
const handleConfirm = () => {
    emit('confirm');
};

// 点击取消
const handleCancel = () => {
    emit('cancel');
};
</script>

<template>
    <Teleport to="body">
        <!-- 背景遮罩 -->
        <div v-if="show"
            class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 backdrop-blur-sm transition-opacity"
            @click="handleCancel">

            <!-- 对话框内容 -->
            <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl w-full max-w-md overflow-hidden transform transition-all"
                @click.stop>

                <!-- 标题栏 -->
                <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
                    <h3 class="text-lg font-medium text-gray-900 dark:text-white">{{ title }}</h3>
                </div>

                <!-- 消息内容 -->
                <div class="px-6 py-4">
                    <p class="text-gray-700 dark:text-gray-300">{{ message }}</p>
                </div>

                <!-- 按钮区域 -->
                <div class="px-6 py-3 bg-gray-50 dark:bg-gray-700/50 flex justify-end space-x-3">
                    <button
                        class="px-4 py-2 text-sm font-medium text-gray-700 bg-white dark:bg-gray-800 dark:text-gray-300 border border-gray-300 dark:border-gray-600 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 focus:outline-none"
                        @click="handleCancel">
                        {{ cancelText || '取消' }}
                    </button>
                    <button
                        class="px-4 py-2 text-sm font-medium text-white bg-red-600 border border-transparent rounded-md hover:bg-red-700 focus:outline-none"
                        @click="handleConfirm">
                        {{ confirmText || '确认' }}
                    </button>
                </div>
            </div>
        </div>
    </Teleport>
</template>