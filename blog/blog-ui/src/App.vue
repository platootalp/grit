<template>
    <div id="app">
        <!-- <img alt="Vue logo" src="./assets/logo.png">
        <HelloWorld msg="Welcome to Your Vue.js App" /> -->
        <WriteBlog/>
    </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import WriteBlog from './view/WriteBlog.vue' 

export default {
    name: 'App',
    components: {
        HelloWorld,
        WriteBlog // 添加 WriteBlog 组件
    }
}
</script>

<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
}
</style>
