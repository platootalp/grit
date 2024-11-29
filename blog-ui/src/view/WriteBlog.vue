<template>
	<div>
	  <v-md-editor v-model="markdownContent" height="500px" placeholder="请输入内容"></v-md-editor>
	</div>
</template>
  
<script>
import { ref } from 'vue';
  
export default {
	setup() {
	  const markdownContent = ref('# Hello World\n这是一个 Markdown 示例！');
	  return {
		markdownContent,
	  };
	},
	data(){
		return{
			markdownContent
		}
	},
	methods:{

	}
  };
</script>