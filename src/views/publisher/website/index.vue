# 网站管理页面组件
<template>
  <div class="website-container">
    <!-- 网站信息展示卡片 -->
    <el-card v-if="website" class="website-info">
      <div slot="header" class="clearfix">
        <span>网站信息</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="handleEdit">
          修改信息
        </el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="网站名称">{{ website.name }}</el-descriptions-item>
        <el-descriptions-item label="网站域名">{{ website.url }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="website.status === 1 ? 'success' : website.status === 0 ? 'warning' : 'danger'">
            {{ website.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ website.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ website.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="网站简介" :span="2">{{ website.description }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审核状态为待审核时显示提交审核按钮 -->
      <div class="operation-buttons" v-if="website.status === 0">
        <el-button type="primary" @click="handleSubmit">提交审核</el-button>
      </div>
    </el-card>

    <!-- 没有网站信息时显示创建按钮 -->
    <el-empty v-else description="暂无网站信息">
      <el-button type="primary" @click="handleCreate">创建网站</el-button>
    </el-empty>

    <!-- 新建/编辑网站对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新建网站' : '编辑网站'" :visible.sync="dialogVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="网站名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="网站域名" prop="url">
          <el-input v-model="temp.url" />
        </el-form-item>
        <el-form-item label="网站简介" prop="description">
          <el-input type="textarea" v-model="temp.description" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { createWebsite, updateWebsite, getWebsiteByUserId, submitWebsiteReview } from '@/api/website'

export default {
  name: 'WebsiteManagement',
  data() {
    return {
      website: null, // 当前用户的网站信息
      dialogVisible: false,
      dialogStatus: 'create',
      temp: {
        name: '',
        url: '',
        description: ''
      },
      rules: {
        name: [{ required: true, message: '请输入网站名称', trigger: 'blur' }],
        url: [{ required: true, message: '请输入网站域名', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getWebsiteInfo()
  },
  methods: {
    // 获取当前用户的网站信息
    getWebsiteInfo() {
      const userInfo = this.$store.state.user.userInfo;
      if (!userInfo || !userInfo.id) {
        this.$store.dispatch('user/getUserInfo')
          .then(data => {
            this.fetchWebsiteInfo(data.id);
          })
          .catch(error => {
            console.error('获取用户信息失败：', error);
            this.$message.error('获取用户信息失败');
          });
      } else {
        console.log('website用户信息：', userInfo);
        this.fetchWebsiteInfo(userInfo.id);
      }
    },
    // 获取网站信息
    fetchWebsiteInfo(userId) {
      getWebsiteByUserId(userId)
        .then(response => {
          console.log('获取到的网站信息：', response.data);
          this.website = response.data;
        })
        .catch(error => {
          console.error('获取网站信息失败：', error);
          if (error.response && error.response.status === 404) {
            // 如果是404错误，说明用户还没有创建网站
            this.website = null;
          } else {
            this.$message.error('获取网站信息失败');
          }
        });
    },
    // 重置表单数据
    resetTemp() {
      this.temp = {
        name: '',
        url: '',
        description: ''
      }
    },
    // 处理创建网站
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 处理编辑网站
    handleEdit() {
      this.temp = Object.assign({}, this.website)
      this.dialogStatus = 'update'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 创建网站数据
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const userInfo = this.$store.state.user.userInfo;
          if (!userInfo || !userInfo.id) {
            this.$message.error('获取用户信息失败');
            return;
          }

          const data = Object.assign({}, this.temp, {
            userId: userInfo.id
          });
          
          console.log('创建网站的数据：', data);
          createWebsite(data).then(() => {
            this.dialogVisible = false
            this.$message({
              type: 'success',
              message: '创建成功'
            })
            this.getWebsiteInfo()
          }).catch(error => {
            console.error('创建网站失败：', error);
            this.$message.error('创建失败');
          })
        }
      })
    },
    // 更新网站数据
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const userInfo = this.$store.state.user.userInfo;
          if (!userInfo || !userInfo.id) {
            this.$message.error('获取用户信息失败');
            return;
          }

          const data = Object.assign({}, this.temp, {
            userId: userInfo.id
          });

          console.log('更新网站的数据：', data);
          updateWebsite(data.id, data).then(() => {
            this.dialogVisible = false
            this.$message({
              type: 'success',
              message: '更新成功'
            })
            this.getWebsiteInfo()
          }).catch(error => {
            console.error('更新网站失败：', error);
            this.$message.error('更新失败');
          })
        }
      })
    },
    // 提交审核
    handleSubmit() {
      this.$confirm('确认提交审核吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        submitWebsiteReview(this.website.id).then(() => {
          this.$message({
            type: 'success',
            message: '提交成功'
          })
          this.getWebsiteInfo()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.website-container {
  padding: 20px;

  .website-info {
    margin-bottom: 20px;

    .operation-buttons {
      margin-top: 20px;
      text-align: center;
    }
  }

  .el-empty {
    margin: 100px 0;
  }
}
</style> 