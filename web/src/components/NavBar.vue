<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
          <a class="navbar-brand" href="/">King Of Bots</a>
          <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
              <li class="nav-item">
                <a  :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" aria-current="page" href="/pk/">对战</a>
              </li>
              <li class="nav-item">
                <a :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" href="/record/">对局列表</a>
              </li>
              <li class="nav-item">
                <a :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" href="/ranklist/">排行榜</a>
              </li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                      lx
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                      <li><a class="dropdown-item" href="/user/bot">我的Bot</a></li>
                      <li><hr class="dropdown-divider"></li>
                      <li><a class="dropdown-item" href="#">退出</a></li>
                    </ul>
                  </li>
            </ul>
          </div>
        </div>
      </nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed } from 'vue';

// 点击哪个导航哪个导航就亮着
export default {
  setup() {
    const route = useRoute();
    let route_name = computed(() => route.name);
    return {
      route_name,
    }
  }
}
</script>

<style scoped>

</style>