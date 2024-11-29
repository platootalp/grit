// main.js
import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入 Element Plus 的样式
import router from './router'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

// 创建 Vue 实例并挂载
const app = createApp(App);
app.use(router);
app.use(mavonEditor);
app.use(ElementPlus);
app.mount('#app');



