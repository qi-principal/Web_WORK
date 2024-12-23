<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>欢迎使用广告投放平台</span>
          </div>
          <div class="welcome-content">
            <h2>{{ welcomeMessage }}</h2>
            <p>这是一个专业的广告投放平台，为广告主和网站主提供全方位的服务。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>平台概况</span>
          </div>
          <div class="platform-stats">
            <div class="stat-item">
              <div class="label">广告主数量</div>
              <div class="value">{{ stats.advertiserCount }}</div>
            </div>
            <div class="stat-item">
              <div class="label">网站主数量</div>
              <div class="value">{{ stats.publisherCount }}</div>
            </div>
            <div class="stat-item">
              <div class="label">广告数量</div>
              <div class="value">{{ stats.adCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>系统公告</span>
          </div>
          <div class="announcement-list">
            <div v-for="(item, index) in announcements" :key="index" class="announcement-item">
              <div class="title">{{ item.title }}</div>
              <div class="content">{{ item.content }}</div>
              <div class="time">{{ item.time }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Home',
  data() {
    return {
      stats: {
        advertiserCount: 100,
        publisherCount: 50,
        adCount: 200
      },
      announcements: [
        {
          title: '系统升级通知',
          content: '系统将于2023年12月31日进行升级维护，届时部分功能可能暂时无法使用。',
          time: '2023-12-25'
        },
        {
          title: '新功能上线',
          content: '广告投放数据分析功能全新升级，提供更详细的数据报表。',
          time: '2023-12-20'
        }
      ]
    }
  },
  computed: {
    ...mapState('user', ['userInfo']),
    welcomeMessage() {
      console.log('userInfo:', this.userInfo)
      if (!this.userInfo) return '欢迎访问'
      const typeMap = {
        0: '尊敬的管理员',
        1: '尊敬的广告主',
        2: '尊敬的网站主'
      }
      const result = `${typeMap[this.userInfo.userType]}，${this.userInfo.username}`
      console.log('welcomeMessage:', result)
      return result
    }
  }
}
</script>

<style lang="scss" scoped>
.home-container {
  .mt-20 {
    margin-top: 20px;
  }

  .welcome-content {
    text-align: center;
    padding: 20px;
    h2 {
      margin-bottom: 10px;
    }
  }

  .platform-stats {
    .stat-item {
      text-align: center;
      margin-bottom: 20px;
      .label {
        color: #666;
        margin-bottom: 5px;
      }
      .value {
        font-size: 24px;
        color: #409EFF;
        font-weight: bold;
      }
    }
  }

  .announcement-list {
    .announcement-item {
      padding: 10px 0;
      border-bottom: 1px solid #eee;
      &:last-child {
        border-bottom: none;
      }
      .title {
        font-weight: bold;
        margin-bottom: 5px;
      }
      .content {
        color: #666;
        margin-bottom: 5px;
      }
      .time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style> 