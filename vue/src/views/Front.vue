<template>
  <div>
    <!--头部-->
    <div class="front-header">
      <div class="front-header-left" @click="navTo('/front/home')">
        <img src="@/assets/imgs/touxiang.jpg" alt="">
        <div class="title">图书购物商城</div>
      </div>
      <div class="front-header-center" style="text-align: right">
        <el-input style="width: 200px" placeholder="请输入商品名称" v-model="name"></el-input>
        <el-button type="primary" style="margin-left: 5px" @click="search">搜索</el-button>
      </div>
      <div class="front-header-right">
        <div>
          <el-dropdown>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/cart')">我的购物车</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/collect')">我的收藏</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/address')">我的地址</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/orders')">我的订单</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="logout">退出</div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <!--主体-->
    <div class="main-body">
      <router-view ref="child" />
    </div>
  </div>

</template>

<script>

export default {
  name: "FrontLayout",

  data () {
    return {
      top: '',
      notice: [],
      name: null
    }
  },

  mounted() {
    this.loadNotice()
  },

  methods: {
    loadNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        this.notice = res.data
        let i = 0
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content
          setInterval(() => {
            this.top = this.notice[i].content
            i++
            if (i === this.notice.length) {
              i = 0
            }
          }, 2500)
        }
      })
    },

    navTo(url) {
      location.href = url
    },
    // // 退出登录
    // logout() {
    //   localStorage.removeItem("xm-user");
    //   this.$router.push("/login");
    // },
    search() {
      let name = this.name ? this.name : ''
      location.href = '/front/search?name=' + name
    }
  }

}
</script>

<style scoped>
  @import "@/assets/css/front.css";
</style>