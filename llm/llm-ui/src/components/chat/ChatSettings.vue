<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useChatStore } from '../../stores/chat';
import type { ChatSettings } from '../../types/chat';

// 定义组件属性
const props = defineProps<{
    show: boolean;
}>();

// 定义事件
const emit = defineEmits<{
    (e: 'close'): void;
}>();

// 使用聊天状态管理
const chatStore = useChatStore();

// 设置表单值
const formValues = ref<ChatSettings>({
    apiKey: chatStore.settings.apiKey || '',
    modelName: chatStore.settings.modelName,
    temperature: chatStore.settings.temperature,
    maxTokens: chatStore.settings.maxTokens
});

// 是否启用API
const enableAPI = computed(() => !!formValues.value.apiKey?.trim());

// 可用模型列表
const availableModels = [
    { id: 'GPT-3.5', name: 'GPT-3.5' },
    { id: 'GPT-4', name: 'GPT-4' },
    { id: 'Claude-3', name: 'Claude-3' },
];

// 是否显示API密钥
const showApiKey = ref(false);

// 切换API密钥可见性
const toggleApiKeyVisibility = () => {
    showApiKey.value = !showApiKey.value;
};

// 保存设置
const saveSettings = async () => {
    await chatStore.updateSettings(formValues.value);
    emit('close');
};

// 重置设置
const resetSettings = () => {
    formValues.value = {
        apiKey: chatStore.settings.apiKey || '',
        modelName: chatStore.settings.modelName,
        temperature: chatStore.settings.temperature,
        maxTokens: chatStore.settings.maxTokens
    };
};

// 切换流式响应模式
const toggleStreamingMode = () => {
    chatStore.toggleStreamingMode();
};

// 监听外部设置变化
watch(() => chatStore.settings, (newSettings) => {
    formValues.value = { ...newSettings };
}, { deep: true });
</script>

<template>
    <div v-if="show" class="settings-overlay" @click.self="emit('close')">
        <div class="settings-panel">
            <div class="settings-header">
                <h2 class="text-lg font-bold">设置</h2>
                <button @click="emit('close')" class="text-gray-500 hover:text-gray-700">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                        stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            <div class="settings-body">
                <div class="settings-section">
                    <h3 class="text-md font-semibold mb-2">API 设置</h3>
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">API 密钥</label>
                        <div class="relative">
                            <input :type="showApiKey ? 'text' : 'password'" v-model="formValues.apiKey"
                                class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800"
                                placeholder="输入你的 API 密钥" />
                            <button @click="toggleApiKeyVisibility"
                                class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                    <path v-if="showApiKey"
                                        d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                                    <path v-if="showApiKey"
                                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                    <path v-else
                                        d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 00-2.79.588l.77.771A5.944 5.944 0 018 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0114.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z" />
                                    <path v-else
                                        d="M11.297 9.176a3.5 3.5 0 00-4.474-4.474l.823.823a2.5 2.5 0 012.829 2.829l.822.822zm-2.943 1.299l.822.822a3.5 3.5 0 01-4.474-4.474l.823.823a2.5 2.5 0 002.829 2.829z" />
                                    <path v-else
                                        d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 001.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 018 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709z" />
                                </svg>
                            </button>
                        </div>
                        <p class="text-xs text-gray-500 mt-1">请从 OpenAI 或 Anthropic 获取 API 密钥</p>
                    </div>
                </div>

                <div class="settings-section">
                    <h3 class="text-md font-semibold mb-2">模型设置</h3>
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">AI 模型</label>
                        <select v-model="formValues.modelName"
                            class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded-md bg-white dark:bg-gray-800"
                            :disabled="!enableAPI">
                            <option v-for="model in availableModels" :key="model.id" :value="model.id">
                                {{ model.name }}
                            </option>
                        </select>
                    </div>

                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                            温度值: {{ formValues.temperature.toFixed(1) }}
                        </label>
                        <input type="range" v-model.number="formValues.temperature" min="0" max="2" step="0.1"
                            class="w-full" :disabled="!enableAPI" />
                        <div class="flex justify-between text-xs text-gray-500">
                            <span>精确</span>
                            <span>平衡</span>
                            <span>创意</span>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                            最大令牌数: {{ formValues.maxTokens }}
                        </label>
                        <input type="range" v-model.number="formValues.maxTokens" min="256" max="4096" step="256"
                            class="w-full" :disabled="!enableAPI" />
                        <div class="flex justify-between text-xs text-gray-500">
                            <span>短</span>
                            <span>中</span>
                            <span>长</span>
                        </div>
                    </div>
                </div>

                <div class="settings-section">
                    <h3 class="text-md font-semibold mb-2">界面设置</h3>
                    <div class="mb-4">
                        <div class="flex items-center">
                            <input type="checkbox" id="streaming" :checked="chatStore.useStreaming"
                                @change="toggleStreamingMode" class="mr-2" :disabled="!enableAPI" />
                            <label for="streaming"
                                class="text-sm font-medium text-gray-700 dark:text-gray-300">流式响应</label>
                        </div>
                        <p class="text-xs text-gray-500 mt-1 ml-6">像ChatGPT一样实时显示回复</p>
                    </div>
                </div>
            </div>

            <div class="settings-footer">
                <button @click="resetSettings" class="btn-secondary mr-2">取消</button>
                <button @click="saveSettings" class="btn-primary">保存</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.settings-overlay {
    @apply fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4;
}

.settings-panel {
    @apply bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full max-h-[90vh] flex flex-col;
}

.settings-header {
    @apply p-4 border-b border-gray-200 dark:border-gray-700 flex justify-between items-center;
}

.settings-body {
    @apply p-4 flex-1 overflow-y-auto;
}

.settings-section {
    @apply mb-6 pb-6 border-b border-gray-200 dark:border-gray-700 last:border-0 last:mb-0 last:pb-0;
}

.settings-footer {
    @apply p-4 border-t border-gray-200 dark:border-gray-700 flex justify-end;
}

.btn-primary {
    @apply px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition-colors;
}

.btn-secondary {
    @apply px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors;
}
</style>