<template>
	<div class="write-blog-container">
		<h1>写博客</h1>

		<!-- 使用 mavon-editor 作为 Markdown 编辑器 -->
		<mavon-editor v-model="markdownContent" height="500px" placeholder="在此处输入Markdown内容" />

		<!-- Markdown 预览 -->
		<div class="markdown-preview">
			<h3>预览</h3>
			<mavon-preview :source="markdownContent" />
		</div>

		<!-- 提交按钮 -->
		<div class="submit-section">
			<button @click="submitBlog">提交博客</button>
		</div>
	</div>
</template>

<script>
import { ref } from 'vue'
import { mavonEditor } from 'mavon-editor'

export default {
	components: {
		'mavon-editor': mavonEditor,
		'mavon-preview': mavonEditor.mavonPreview
	},
	setup() {
		// 初始化 Markdown 内容
		const markdownContent = ref('# 我的第一篇博客\n\n这是一个使用 Markdown 编写的博客示例！')

		// 提交博客的方法
		const submitBlog = () => {
			// 假设你通过 API 提交博客内容
			console.log('提交的博客内容：', markdownContent.value)
			// 在这里调用API进行提交
			// 例如:
			// api.submitBlog({ content: markdownContent.value })
		}

		return {
			markdownContent,
			submitBlog
		}
	}
}
</script>

<style scoped>
.write-blog-container {
	max-width: 800px;
	margin: 0 auto;
	padding: 20px;
}

h1 {
	text-align: center;
}

.markdown-preview {
	margin-top: 30px;
	padding: 20px;
	background-color: #f7f7f7;
	border-radius: 5px;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.submit-section {
	margin-top: 20px;
	text-align: center;
}

button {
	padding: 10px 20px;
	font-size: 16px;
	background-color: #42b983;
	color: white;
	border: none;
	border-radius: 5px;
	cursor: pointer;
}

button:hover {
	background-color: #368e70;
}
</style>
