<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '../stores/chat';
import ChatMessage from '../components/chat/ChatMessage.vue';
import ChatInput from '../components/chat/ChatInput.vue';
import ChatSettings from '../components/chat/ChatSettings.vue';
import ConfirmDialog from '../components/ui/ConfirmDialog.vue';
import { useQuery } from '@tanstack/vue-query';

// 使用路由和路由参数
const route = useRoute();
const router = useRouter();

// 使用聊天状态管理
const chatStore = useChatStore();

// 消息容器引用，用于滚动到底部
const messagesContainer = ref<HTMLElement | null>(null);

// 设置对话框可见性
const showSettings = ref(false);

// 模型下拉菜单状态
const showModelDropdown = ref(false);

// 确认对话框状态
const confirmDialog = ref({
    show: false,
    title: '',
    message: '',
    sessionId: '',
    action: '' // 添加动作类型标识
});

// 脚本部分添加批量删除相关的状态和方法
const isSelectionMode = ref(false);
const selectedSessions = ref<string[]>([]);

// 判断是否全部选中
const isAllSelected = computed(() => {
    return chatStore.sessions.length > 0 && selectedSessions.value.length === chatStore.sessions.length;
});

// 选择模型
const selectModel = (modelName: string) => {
    chatStore.updateSettings({
        ...chatStore.settings,
        modelName
    });
    showModelDropdown.value = false;
};

// 发送消息
const handleSend = async (content: string) => {
    // 如果没有活动会话，先创建一个
    if (!chatStore.activeSession) {
        const session = await chatStore.createSession();
        // 更新URL以反映新创建的会话
        router.push(`/chat/${session.id}`);
        // 确保会话已设置为活动会话
        await chatStore.setActiveSession(session.id);
    }

    // 发送消息
    await chatStore.sendMessage(content);

    // 滚动到底部
    scrollToBottom();
};

// 创建新会话
const createNewSession = async () => {
    const session = await chatStore.createSession();
    // 导航到新会话
    router.push(`/chat/${session.id}`);
};

// 创建新会话并开始对话
const createNewSessionWithMessage = async (content: string) => {
    const newSession = await chatStore.createSession();
    await chatStore.setActiveSession(newSession.id);
    router.push(`/chat/${newSession.id}`);

    // 等待路由跳转完成后再发送消息
    setTimeout(async () => {
        await chatStore.sendMessage(content);
        scrollToBottom();
    }, 100);
};

// 选择会话
const selectSession = (sessionId: string) => {
    chatStore.setActiveSession(sessionId);
    // 更新URL以反映选择的会话
    router.push(`/chat/${sessionId}`);
};

// 显示删除会话确认对话框
const showDeleteConfirm = (sessionId: string) => {
    confirmDialog.value = {
        show: true,
        title: '删除会话',
        message: '确定要删除这个会话吗？这个操作无法撤销。',
        sessionId: sessionId,
        action: 'delete'
    };
};

// 显示清空会话确认对话框
const showClearConfirm = (sessionId: string) => {
    confirmDialog.value = {
        show: true,
        title: '清空会话',
        message: '确定要清空这个会话中的所有消息吗？这个操作无法撤销。',
        sessionId: sessionId,
        action: 'clear'
    };
};

// 切换选择模式
const toggleSelectionMode = () => {
    isSelectionMode.value = !isSelectionMode.value;
    // 清空已选择的会话
    selectedSessions.value = [];
};

// 全选/取消全选会话
const toggleSelectAll = () => {
    if (isAllSelected.value) {
        // 如果已全选，则清空选择
        selectedSessions.value = [];
    } else {
        // 否则选择所有会话
        selectedSessions.value = chatStore.sessions.map(s => s.id);
    }
};

// 选择或取消选择会话
const toggleSessionSelection = (sessionId: string, event: Event) => {
    // 阻止事件冒泡，防止触发会话选择
    event.stopPropagation();

    const index = selectedSessions.value.indexOf(sessionId);
    if (index === -1) {
        // 如果不在选中列表中，则添加
        selectedSessions.value.push(sessionId);
    } else {
        // 如果已在选中列表中，则移除
        selectedSessions.value.splice(index, 1);
    }
};

// 删除选中的所有会话
const deleteSelectedSessions = () => {
    if (selectedSessions.value.length === 0) return;

    confirmDialog.value = {
        show: true,
        title: '批量删除会话',
        message: `确定要删除选中的${selectedSessions.value.length}个会话吗？这个操作无法撤销。`,
        sessionId: '', // 不需要单个会话ID
        action: 'batch-delete'
    };
};

// 修改确认操作方法，添加批量删除功能
const confirmAction = () => {
    if (confirmDialog.value.action === 'delete') {
        if (!confirmDialog.value.sessionId) return;

        // 如果删除的是当前活动会话，则导航到会话列表
        if (confirmDialog.value.sessionId === chatStore.activeSessionId) {
            router.push('/chat');
        }

        chatStore.deleteSession(confirmDialog.value.sessionId);
    } else if (confirmDialog.value.action === 'clear') {
        if (!confirmDialog.value.sessionId) return;
        chatStore.clearMessages(confirmDialog.value.sessionId);
    } else if (confirmDialog.value.action === 'batch-delete') {
        // 批量删除会话
        for (const sessionId of selectedSessions.value) {
            // 如果删除的包含当前活动会话，则导航到会话列表
            if (sessionId === chatStore.activeSessionId) {
                router.push('/chat');
            }
            chatStore.deleteSession(sessionId);
        }
        // 删除完成后退出选择模式
        isSelectionMode.value = false;
        selectedSessions.value = [];
    }

    confirmDialog.value.show = false;
};

// 取消操作
const cancelAction = () => {
    confirmDialog.value.show = false;
};

// 滚动到底部
const scrollToBottom = () => {
    setTimeout(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
    }, 50);
};

// 打开/关闭设置对话框
const toggleSettings = () => {
    showSettings.value = !showSettings.value;
};

// 切换侧边栏显示/隐藏
const toggleSidebar = () => {
    chatStore.toggleSidebar();
};

// 检查是否有API错误
const hasError = computed(() => !!chatStore.error);

// 检查是否API已配置
const hasApiConfigured = computed(() => !!chatStore.settings.apiKey);

// 监听消息变化，自动滚动到底部
watch(() => chatStore.activeSession?.messages.length, () => {
    scrollToBottom();
});

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
    if (newId && typeof newId === 'string') {
        // 当URL中有会话ID时，设置活动会话
        chatStore.setActiveSession(newId);
    } else if (chatStore.sessions.length > 0) {
        // 当URL中没有会话ID且有会话列表时，仅显示会话列表
        chatStore.setActiveSession(null);
    }
}, { immediate: true });

// 计算确认按钮文本
const confirmButtonText = computed(() => {
    if (confirmDialog.value.action === 'delete') return '删除';
    if (confirmDialog.value.action === 'clear') return '清空';
    if (confirmDialog.value.action === 'batch-delete') return '批量删除';
    return '确认';
});

// 添加一个可以展示新会话按钮的计算属性
const shouldShowNewChatButton = computed(() => {
    return chatStore.activeSession &&
        chatStore.activeSession.messages.length >= 2 &&
        !chatStore.loading;
});

// 初始化数据
onMounted(async () => {
    // 初始化Store
    await chatStore.initializeStore();

    // 检查URL中是否有会话ID
    if (route.params.id && typeof route.params.id === 'string') {
        chatStore.setActiveSession(route.params.id);
    } else if (route.path === '/chat') {
        // 如果是会话列表页面，不自动创建新会话
        chatStore.setActiveSession(null);
    }

    // 初始滚动到底部
    scrollToBottom();
});

// 返回主页
const goToHome = () => {
    router.push('/');
};
</script>

<template>
    <div class="chat-container-chatgpt">
        <div class="flex h-full">
            <!-- 侧边栏 - 会话列表 -->
            <Transition name="sidebar">
                <div v-if="chatStore.sidebarVisible"
                    class="sidebar-container w-64 border-r border-gray-100/50 dark:border-gray-800/50 overflow-y-auto flex flex-col z-30">
                    <div class="p-2 border-b border-gray-100/50 dark:border-gray-800/50">
                        <div class="flex justify-between items-center">
                            <!-- 新对话按钮 -->
                            <button @click="goToHome"
                                class="sidebar-new-chat-btn flex-grow flex items-center rounded-md p-2 gap-2 transition-colors text-sm hover:bg-gray-100 dark:hover:bg-gray-700">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20"
                                    fill="currentColor">
                                    <path fill-rule="evenodd"
                                        d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                                        clip-rule="evenodd" />
                                </svg>
                                <span>新对话</span>
                            </button>

                            <!-- 批量删除按钮 -->
                            <button @click="toggleSelectionMode"
                                class="sidebar-action-btn ml-2 p-2 rounded-md transition-colors text-sm hover:bg-gray-100 dark:hover:bg-gray-700"
                                :class="{ 'bg-gray-100 dark:bg-gray-700': isSelectionMode }"
                                :title="isSelectionMode ? '取消' : '批量选择'">
                                <svg v-if="!isSelectionMode" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4"
                                    viewBox="0 0 20 20" fill="currentColor">
                                    <path
                                        d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
                                    <path fill-rule="evenodd"
                                        d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                                        clip-rule="evenodd" />
                                </svg>
                                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20"
                                    fill="currentColor">
                                    <path fill-rule="evenodd"
                                        d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                                        clip-rule="evenodd" />
                                </svg>
                            </button>
                        </div>

                        <!-- 选择模式工具栏 -->
                        <div v-if="isSelectionMode" class="mt-2 flex justify-between items-center">
                            <div class="flex items-center">
                                <!-- 全选控制框 -->
                                <div @click="toggleSelectAll"
                                    class="mr-2 flex items-center cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 rounded px-1.5 py-0.5 transition-colors">
                                    <div class="w-4 h-4 border rounded mr-1.5 flex-shrink-0"
                                        :class="{ 'bg-indigo-600 border-indigo-600': isAllSelected, 'border-gray-300 dark:border-gray-600': !isAllSelected }">
                                        <svg v-if="isAllSelected" xmlns="http://www.w3.org/2000/svg"
                                            class="h-4 w-4 text-white" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd"
                                                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                                clip-rule="evenodd" />
                                        </svg>
                                    </div>
                                    <span class="text-xs text-gray-600 dark:text-gray-300 whitespace-nowrap">全选</span>
                                </div>
                                <span class="text-xs text-gray-500 dark:text-gray-400">
                                    已选择 {{ selectedSessions.length }} 项
                                </span>
                            </div>
                            <button @click="deleteSelectedSessions"
                                class="text-xs text-red-600 dark:text-red-400 hover:underline p-1"
                                :disabled="selectedSessions.length === 0"
                                :class="{ 'opacity-50 cursor-not-allowed': selectedSessions.length === 0 }">
                                删除
                            </button>
                        </div>
                    </div>

                    <!-- 会话列表 -->
                    <div class="overflow-y-auto flex-1 px-2 py-2">
                        <div v-for="session in chatStore.sessions" :key="session.id"
                            @click="isSelectionMode ? toggleSessionSelection(session.id, $event) : selectSession(session.id)"
                            class="flex items-center p-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 rounded-md group transition-colors text-sm mb-1 text-gray-700 dark:text-gray-200"
                            :class="{ 'bg-gray-100 dark:bg-gray-700': session.id === chatStore.activeSessionId || selectedSessions.includes(session.id) }">

                            <!-- 选择框 -->
                            <div v-if="isSelectionMode" class="mr-2 flex-shrink-0">
                                <div class="w-4 h-4 border rounded flex items-center justify-center"
                                    :class="{ 'bg-indigo-600 border-indigo-600': selectedSessions.includes(session.id), 'border-gray-300 dark:border-gray-600': !selectedSessions.includes(session.id) }">
                                    <svg v-if="selectedSessions.includes(session.id)" xmlns="http://www.w3.org/2000/svg"
                                        class="h-3 w-3 text-white" viewBox="0 0 20 20" fill="currentColor">
                                        <path fill-rule="evenodd"
                                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                            clip-rule="evenodd" />
                                    </svg>
                                </div>
                            </div>

                            <div class="flex-1 min-w-0">
                                <div class="font-medium truncate">{{ session.title }}</div>
                                <div class="text-xs text-gray-500 dark:text-gray-400">
                                    {{ new Date(session.updatedAt).toLocaleString() }}
                                </div>
                            </div>

                            <!-- 删除按钮，在选择模式下隐藏 -->
                            <button v-if="!isSelectionMode && (session.id === chatStore.activeSessionId || true)"
                                @click.stop="showDeleteConfirm(session.id)"
                                class="ml-2 p-1 rounded-full text-gray-500 hover:text-red-500 hover:bg-gray-200 dark:hover:bg-gray-600 opacity-0 group-hover:opacity-100 transition-opacity">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20"
                                    fill="currentColor">
                                    <path fill-rule="evenodd"
                                        d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                        clip-rule="evenodd" />
                                </svg>
                            </button>
                        </div>

                        <div v-if="!chatStore.sessions.length"
                            class="p-4 text-center text-gray-500 dark:text-gray-400 text-sm">
                            没有聊天记录
                        </div>
                    </div>

                    <!-- 侧边栏底部 - 只保留版本信息 -->
                    <div class="p-2 border-t border-gray-100/50 dark:border-gray-800/50 text-center">
                        <div class="text-xs text-gray-500 dark:text-gray-400">
                            ChatGPT风格界面 · v1.0.0
                        </div>
                    </div>
                </div>
            </Transition>

            <!-- 主要聊天区域 -->
            <div class="main-view-chatgpt flex flex-col h-full w-full" :class="{ 'ml-64': chatStore.sidebarVisible }">
                <!-- 顶部工具栏 -->
                <div class="border-b border-gray-100/50 dark:border-gray-800/50 p-3 flex justify-between items-center">
                    <div class="flex items-center">
                        <!-- 侧边栏切换按钮 -->
                        <button @click="toggleSidebar"
                            class="p-1.5 mr-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700" title="切换侧边栏">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20"
                                fill="currentColor">
                                <path fill-rule="evenodd"
                                    d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z"
                                    clip-rule="evenodd" />
                            </svg>
                        </button>
                        
                        <!-- 模型选择下拉菜单 -->
                        <div class="relative">
                            <button @click="showModelDropdown = !showModelDropdown" @dblclick="toggleSettings"
                                class="flex items-center gap-1 p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-lg font-medium"
                                title="点击切换模型，双击打开设置">
                                {{ chatStore.settings.modelName || 'GPT-3.5' }}
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" viewBox="0 0 20 20" fill="currentColor">
                                    <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                                </svg>
                            </button>
                            <!-- 模型下拉列表 -->
                            <div v-if="showModelDropdown" 
                                class="absolute left-0 mt-1 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg border border-gray-200 dark:border-gray-700 z-50">
                                <div class="py-1">
                                    <button @click="selectModel('GPT-3.5')"
                                        class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        :class="{'font-semibold bg-gray-50 dark:bg-gray-700': chatStore.settings.modelName === 'GPT-3.5'}">
                                        GPT-3.5
                                    </button>
                                    <button @click="selectModel('GPT-4')"
                                        class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        :class="{'font-semibold bg-gray-50 dark:bg-gray-700': chatStore.settings.modelName === 'GPT-4'}">
                                        GPT-4
                                    </button>
                                    <button @click="selectModel('Claude-3')"
                                        class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        :class="{'font-semibold bg-gray-50 dark:bg-gray-700': chatStore.settings.modelName === 'Claude-3'}">
                                        Claude-3
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 设置按钮 -->
                    <button @click="toggleSettings" class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700"
                        title="设置">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd"
                                d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z"
                                clip-rule="evenodd" />
                        </svg>
                    </button>
                </div>

                <!-- 聊天消息区域 -->
                <div ref="messagesContainer" class="flex-1 overflow-y-auto bg-white dark:bg-[#343541]">
                    <!-- 有活动会话且有消息时显示消息列表 -->
                    <div v-if="chatStore.activeSession && chatStore.activeSession.messages.length > 0">
                        <div class="max-w-2xl lg:max-w-3xl mx-auto px-4 sm:px-6 lg:px-0">
                            <ChatMessage v-for="message in chatStore.activeSession.messages" :key="message.id"
                                :role="message.role" :content="message.content" />

                            <!-- 加载中指示器 -->
                            <div v-if="chatStore.loading" class="py-8 border-b border-black/10 dark:border-gray-700/50 flex items-start bg-white dark:bg-[#444654]">
                                <div
                                    class="mr-4 flex-shrink-0 pt-0.5 w-8 h-8 rounded-sm bg-[#10a37f] flex items-center justify-center">
                                    <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
                                    </svg>
                                </div>

                                <div class="flex-1">
                                    <div class="text-xs text-gray-500 dark:text-gray-400 mb-2">{{ chatStore.settings.modelName || 'AI助手' }}</div>
                                    <div class="flex space-x-2 text-[15px] leading-relaxed">
                                        <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                                        <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce delay-100"></div>
                                        <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce delay-200"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 会话列表 - 当路径是 /chat 且没有选择活动会话时显示 -->
                    <div v-else-if="route.path === '/chat' && chatStore.sessions.length > 0" class="p-6">
                        <h2 class="text-xl font-bold mb-6 text-center">选择会话或开始新对话</h2>
                        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 max-w-4xl mx-auto">
                            <div v-for="session in chatStore.sessions" :key="session.id"
                                @click="selectSession(session.id)"
                                class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4 cursor-pointer hover:shadow transition-shadow">
                                <h3 class="font-bold text-lg mb-2 truncate">{{ session.title }}</h3>
                                <p class="text-sm text-gray-500 dark:text-gray-400">
                                    {{ new Date(session.updatedAt).toLocaleString() }}
                                </p>
                            </div>
                        </div>

                        <div class="mt-8 flex justify-center">
                            <button @click="goToHome" class="btn btn-primary px-6 py-2">
                                开始新会话
                            </button>
                        </div>
                    </div>

                    <!-- 欢迎界面 - 当没有任何会话时显示 -->
                    <div v-else class="h-full flex flex-col items-center justify-center p-8 text-center">
                        <div class="max-w-md">
                            <svg xmlns="http://www.w3.org/2000/svg"
                                class="h-16 w-16 mx-auto mb-6 text-indigo-600 dark:text-indigo-400" fill="none"
                                viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                            </svg>
                            <h1 class="text-2xl font-bold mb-3">开始对话</h1>
                            <p class="text-gray-600 dark:text-gray-300 mb-6">
                                在下方输入框中提问，或从左侧选择已有的对话
                            </p>

                            <div class="flex justify-center">
                                <button @click="goToHome" class="btn btn-primary px-6 py-2">
                                    新对话
                                </button>
                            </div>

                            <!-- API 提示 -->
                            <div v-if="!hasApiConfigured"
                                class="mt-6 text-left p-4 bg-yellow-50 dark:bg-yellow-900/30 rounded-lg border border-yellow-200 dark:border-yellow-700">
                                <div class="flex">
                                    <svg xmlns="http://www.w3.org/2000/svg"
                                        class="h-5 w-5 text-yellow-500 mr-2 flex-shrink-0 mt-0.5" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    <div>
                                        <h3 class="font-medium text-yellow-800 dark:text-yellow-200 text-sm">本地模式</h3>
                                        <p class="text-xs text-yellow-700 dark:text-yellow-300 mt-1">
                                            当前运行在本地模式，回复将由系统模拟生成。配置API密钥以获取真实AI回复。
                                        </p>
                                        <button @click="toggleSettings"
                                            class="mt-2 text-xs px-3 py-1 bg-yellow-200 dark:bg-yellow-800 text-yellow-800 dark:text-yellow-200 rounded hover:bg-yellow-300 dark:hover:bg-yellow-700">
                                            配置API
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 底部输入区域 - 始终显示 -->
                <div class="border-t border-gray-100/50 dark:border-gray-800/50 bg-white dark:bg-[#343541] py-4">
                    <div class="max-w-2xl lg:max-w-3xl mx-auto px-4 sm:px-6 lg:px-0">
                        <ChatInput @send="handleSend" />
                    </div>
                </div>
            </div>
        </div>

        <!-- 设置对话框 -->
        <ChatSettings :show="showSettings" @close="toggleSettings" />

        <!-- 删除确认对话框 -->
        <ConfirmDialog :show="confirmDialog.show" :title="confirmDialog.title" :message="confirmDialog.message"
            :confirmText="confirmButtonText" cancelText="取消" @confirm="confirmAction" @cancel="cancelAction" />
    </div>
</template>

<style scoped>
/* 添加平滑滚动效果 */
.main-view-chatgpt {
    scroll-behavior: smooth;
}

/* 侧边栏容器样式 */
.sidebar-container {
    background-color: #fff;
    transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.3s ease;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
    height: 100vh;
    position: absolute;
    left: 0;
    top: 0;
}

/* 暗色模式下的侧边栏 */
.dark .sidebar-container {
    background-color: #343541;
}

/* 侧边栏新对话按钮样式 */
.sidebar-new-chat-btn {
    background-color: transparent;
    color: inherit;
    font-weight: 500;
    border: 1px solid rgba(0, 0, 0, 0.1);
}

.dark .sidebar-new-chat-btn {
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.sidebar-new-chat-btn:hover {
    background-color: rgba(0, 0, 0, 0.05);
}

.dark .sidebar-new-chat-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
}

/* 侧边栏批量编辑按钮样式 */
.sidebar-action-btn {
    background-color: transparent;
    color: inherit;
    border: 1px solid rgba(0, 0, 0, 0.1);
}

.dark .sidebar-action-btn {
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.sidebar-action-btn:hover {
    background-color: rgba(0, 0, 0, 0.05);
}

.dark .sidebar-action-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
}

/* 响应式布局调整 */
@media (max-width: 768px) {
    .sidebar-container {
        position: fixed;
        z-index: 50;
    }
}

/* 添加侧边栏切换时的平滑过渡 */
.sidebar-enter-active,
.sidebar-leave-active {
    transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.3s ease;
}

.sidebar-enter-from,
.sidebar-leave-to {
    transform: translateX(-100%);
    opacity: 0;
}

.chat-container-chatgpt {
    @apply h-screen overflow-hidden;
}
</style>