<template>
  <NavBar />
  <router-view></router-view>
</template>
  
<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

export default {
  components: {
    NavBar
  }
}


</script>

<style >
  body {  
    /* 设置背景图片 */  
    background-image: url('./assets/images/background.jpg');  
      
    /* 确保背景图片覆盖整个页面，且图片不会被拉伸或压缩 */  
    background-size: cover;  
      
    /* 背景图片不重复 */  
    background-repeat: no-repeat;  
      
    /* 背景图片固定在页面上，不随页面滚动 */  
    background-attachment: fixed;  
      
    /* 背景颜色，当图片未加载或不可见时显示 */  
    background-color: #f0f0f0; /* 浅灰色作为备用背景色 */  
      
    /* 确保整个body元素占据视口（viewport）的全部高度 */  
    min-height: 100vh;  
    margin: 0; /* 移除默认的margin */  
  }
</style>
