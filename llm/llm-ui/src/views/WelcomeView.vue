<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useChatStore } from '../stores/chat';
import ChatSettings from '../components/chat/ChatSettings.vue';
import ChatInput from '../components/chat/ChatInput.vue';
import { ref, computed, onMounted } from 'vue';
import ConfirmDialog from '../components/ui/ConfirmDialog.vue';

// 使用路由器
const router = useRouter();

// 使用聊天状态管理
const chatStore = useChatStore();

// 设置对话框可见性
const showSettings = ref(false);

// 打开/关闭设置对话框
const toggleSettings = () => {
    showSettings.value = !showSettings.value;
};

// 切换侧边栏显示/隐藏
const toggleSidebar = () => {
    chatStore.toggleSidebar();
};

// 创建新会话并导航到聊天界面
const createNewSession = async () => {
    const session = await chatStore.createSession();
    router.push(`/chat/${session.id}`);
};

// 导航到现有会话
const goToSession = (sessionId: string) => {
    router.push(`/chat/${sessionId}`);
};

// 处理消息发送
const handleSend = async (content: string) => {
    // 先创建新会话
    const session = await chatStore.createSession();

    // 设置为活动会话
    await chatStore.setActiveSession(session.id);

    // 导航到新会话页面
    router.push(`/chat/${session.id}`);

    // 在导航后发送消息，避免消息丢失
    setTimeout(async () => {
        await chatStore.sendMessage(content);
    }, 100);
};

// 检查是否有会话历史
const hasExistingSessions = computed(() => chatStore.sessions.length > 0);

// 检查是否API已配置
const hasApiConfigured = computed(() => !!chatStore.settings.apiKey);

// 会话按更新时间排序
const sortedSessions = computed(() => {
    return [...chatStore.sessions].sort((a, b) => b.updatedAt - a.updatedAt);
});

// 格式化日期 - 统一与ChatView.vue中的格式
const formatDate = (timestamp: number): string => {
    return new Date(timestamp).toLocaleString();
};

// 添加确认对话框状态
const confirmDialog = ref({
    show: false,
    title: '',
    message: '',
    sessionId: '',
    action: '' // 动作类型标识
});

// 批量删除相关的状态和方法
const isSelectionMode = ref(false);
const selectedSessions = ref<string[]>([]);

// 判断是否全部选中
const isAllSelected = computed(() => {
    return sortedSessions.value.length > 0 && selectedSessions.value.length === sortedSessions.value.length;
});

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
        selectedSessions.value = sortedSessions.value.map(s => s.id);
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

// 确认操作
const confirmAction = () => {
    if (confirmDialog.value.action === 'delete') {
        if (!confirmDialog.value.sessionId) return;
        chatStore.deleteSession(confirmDialog.value.sessionId);
    } else if (confirmDialog.value.action === 'batch-delete') {
        // 批量删除会话
        for (const sessionId of selectedSessions.value) {
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

// 计算确认按钮文本
const confirmButtonText = computed(() => {
    if (confirmDialog.value.action === 'delete') return '删除';
    if (confirmDialog.value.action === 'batch-delete') return '批量删除';
    return '确认';
});

// 初始化Store
onMounted(async () => {
    await chatStore.initializeStore();
});
</script>

<template>
    <div class="welcome-container">
        <div class="flex h-full">
            <!-- 侧边栏 - 会话列表 (使用过渡动画) -->
            <Transition name="sidebar">
                <div v-if="chatStore.sidebarVisible"
                    class="sidebar-container w-64 border-r border-gray-100/50 dark:border-gray-800/50 overflow-y-auto flex flex-col z-30">
                    <div class="p-2 border-b border-gray-100/50 dark:border-gray-800/50">
                        <div class="flex justify-between items-center">
                            <button @click="router.push('/')"
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

                    <div class="overflow-y-auto flex-1 px-2 py-2">
                        <div v-for="session in sortedSessions" :key="session.id"
                            @click="isSelectionMode ? toggleSessionSelection(session.id, $event) : goToSession(session.id)"
                            class="flex items-center p-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 rounded-md group transition-colors text-sm mb-1 text-gray-700 dark:text-gray-200"
                            :class="{ 'bg-gray-100 dark:bg-gray-700': selectedSessions.includes(session.id) }">

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
                                    {{ formatDate(session.updatedAt) }}
                                </div>
                            </div>

                            <!-- 删除按钮，在选择模式下隐藏 -->
                            <button v-if="!isSelectionMode" @click.stop="showDeleteConfirm(session.id)"
                                class="ml-2 p-1 rounded-full text-gray-500 hover:text-red-500 hover:bg-gray-200 dark:hover:bg-gray-600 opacity-0 group-hover:opacity-100 transition-opacity">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20"
                                    fill="currentColor">
                                    <path fill-rule="evenodd"
                                        d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                        clip-rule="evenodd" />
                                </svg>
                            </button>
                        </div>

                        <div v-if="!sortedSessions.length"
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

            <!-- 右侧主界面 -->
            <div class="main-view-chatgpt flex-1 flex flex-col" :class="{ 'ml-64': chatStore.sidebarVisible }">
                <!-- 顶部工具栏 - 与ChatView统一风格 -->
                <div class="border-b border-gray-100/50 dark:border-gray-800/50 p-3 flex justify-between items-center">
                    <div class="flex items-center">
                        <!-- 侧边栏切换按钮 -->
                        <button @click="toggleSidebar"
                            class="p-2 mr-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700" title="切换侧边栏">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20"
                                fill="currentColor">
                                <path fill-rule="evenodd"
                                    d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z"
                                    clip-rule="evenodd" />
                            </svg>
                        </button>
                        <h1 class="text-lg font-medium truncate max-w-xs">AI聊天助手</h1>
                    </div>

                    <div class="flex items-center space-x-2">
                        <!-- 状态指示器 -->
                        <div class="flex items-center space-x-2">
                            <div v-if="hasApiConfigured"
                                class="text-xs px-2 py-1 rounded-full bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100">
                                API已连接
                            </div>
                            <div v-else
                                class="text-xs px-2 py-1 rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-800 dark:text-yellow-100">
                                本地模式
                            </div>
                        </div>

                        <!-- 设置按钮 -->
                        <button @click="toggleSettings" class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700"
                            title="设置">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20"
                                fill="currentColor">
                                <path fill-rule="evenodd"
                                    d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z"
                                    clip-rule="evenodd" />
                            </svg>
                        </button>
                    </div>
                </div>

                <!-- 主要内容区域 -->
                <div class="flex-1 flex flex-col overflow-y-auto">
                    <div class="flex-1 flex flex-col justify-center items-center px-4 py-4">
                        <div class="max-w-3xl w-full">
                            <!-- 欢迎标题部分 -->
                            <div class="text-center mb-6">
                                <div class="flex justify-center mb-4">
                                    <svg xmlns="http://www.w3.org/2000/svg"
                                        class="h-12 w-12 text-indigo-600 dark:text-indigo-400" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                                    </svg>
                                </div>
                                <h1 class="text-2xl font-bold mb-2">AI聊天助手</h1>
                                <p class="text-gray-600 dark:text-gray-300 mb-4">
                                    在下方提出问题，开始与AI助手对话
                                </p>
                            </div>

                            <!-- 示例问题 -->
                            <div class="mb-8">
                                <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                                    <button @click="handleSend('介绍一下你自己，你能做什么？')"
                                        class="text-left p-3 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 text-sm">
                                        介绍一下你自己，你能做什么？
                                    </button>
                                    <button @click="handleSend('帮我写一段简单的Python程序，计算斐波那契数列。')"
                                        class="text-left p-3 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 text-sm">
                                        帮我写一段简单的Python程序，计算斐波那契数列。
                                    </button>
                                    <button @click="handleSend('什么是人工智能？它的发展历程是怎样的？')"
                                        class="text-left p-3 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 text-sm">
                                        什么是人工智能？它的发展历程是怎样的？
                                    </button>
                                    <button @click="handleSend('请推荐几本经典科幻小说。')"
                                        class="text-left p-3 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 text-sm">
                                        请推荐几本经典科幻小说。
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 输入框区域 -->
                    <div class="mt-auto border-t border-gray-200 dark:border-gray-700 p-4 mx-auto w-full max-w-3xl">
                        <ChatInput @send="handleSend" />
                    </div>
                </div>

                <!-- 底部信息 -->
                <div
                    class="border-t border-gray-100/50 dark:border-gray-800/50 p-4 text-center text-xs text-gray-500 dark:text-gray-400">
                    ChatGPT风格界面 · 版本 1.0.0
                </div>
            </div>
        </div>

        <!-- 设置对话框 -->
        <ChatSettings :show="showSettings" @close="toggleSettings" />

        <!-- 确认对话框 -->
        <ConfirmDialog :show="confirmDialog.show" :title="confirmDialog.title" :message="confirmDialog.message"
            @confirm="confirmAction" @cancel="cancelAction" :confirmButtonText="confirmButtonText" />
    </div>
</template>

<style scoped>
.welcome-container {
    @apply h-screen bg-white dark:bg-[#343541] text-gray-900 dark:text-gray-100 overflow-hidden;
}

/* 添加与ChatView.vue相同的平滑滚动效果 */
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
</style>