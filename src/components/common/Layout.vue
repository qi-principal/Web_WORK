<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>

        <!-- 广告主菜单 -->
        <template v-if="userInfo && userInfo.userType === 1">
          <el-submenu index="/advertiser">
            <template slot="title">
              <i class="el-icon-s-marketing"></i>
              <span>广告管理</span>
            </template>
            <el-menu-item index="/advertiser/ad">
              <i class="el-icon-picture"></i>
              <span>广告管理</span>
            </el-menu-item>
            <el-menu-item index="/advertiser/material">
              <i class="el-icon-folder"></i>
              <span>素材管理</span>
            </el-menu-item>
            <el-menu-item index="/advertiser/delivery">
              <i class="el-icon-s-promotion"></i>
              <span>投放管理</span>
            </el-menu-item>
            <el-menu-item index="/advertiser/statistics">
              <i class="el-icon-data-line"></i>
              <span>数据统计</span>
            </el-menu-item>
          </el-submenu>
        </template>

        <!-- 网站主菜单 -->
        <template v-if="userInfo && userInfo.userType === 2">
          <el-submenu index="/publisher">
            <template slot="title">
              <i class="el-icon-s-platform"></i>
              <span>网站管理</span>
            </template>
            <el-menu-item index="/publisher/website">
              <i class="el-icon-monitor"></i>
              <span>网站管理</span>
            </el-menu-item>
            <el-menu-item index="/publisher/adspace">
              <i class="el-icon-picture-outline"></i>
              <span>广告位管理</span>
            </el-menu-item>
            <el-menu-item index="/publisher/statistics">
              <i class="el-icon-data-analysis"></i>
              <span>收益统计</span>
            </el-menu-item>
          </el-submenu>
        </template>

        <!-- 管理员菜单 -->
        <template v-if="userInfo && userInfo.userType === 3">
          <el-submenu index="/admin">
            <template slot="title">
              <i class="el-icon-s-tools"></i>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/user">
              <i class="el-icon-user"></i>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/ad/review">
              <i class="el-icon-s-check"></i>
              <span>广告审核</span>
            </el-menu-item>
            <el-menu-item index="/admin/website/review">
              <i class="el-icon-s-check"></i>
              <span>网站审核</span>
            </el-menu-item>
            <el-menu-item index="/admin/adspace/review">
              <i class="el-icon-s-check"></i>
              <span>广告位审核</span>
            </el-menu-item>
          </el-submenu>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-left">
          <span>广告投放平台</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userInfo ? userInfo.username : '未登录' }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'Layout',
  computed: {
    ...mapState('user', ['userInfo'])
  },
  methods: {
    ...mapActions('user', ['logout']),
    handleCommand(command) {
      if (command === 'logout') {
        this.logout().then(() => {
          this.$router.push('/login')
          this.$message.success('退出成功')
        })
      } else if (command === 'profile') {
        // 跳转到个人信息页面
        this.$message.info('功能开发中')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .el-aside {
    background-color: #304156;
    .el-menu {
      border-right: none;
    }
  }

  .el-header {
    background-color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    .header-left {
      font-size: 18px;
      font-weight: bold;
    }

    .header-right {
      .el-dropdown-link {
        cursor: pointer;
        color: #409EFF;
      }
    }
  }

  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style> 