<template>
  <div class="material-container">
    <div class="filter-container">
      <el-upload
        class="upload-demo"
        action="#"
        :http-request="handleUpload"
        :show-file-list="false"
        :before-upload="beforeUpload">
        <el-button type="primary">上传素材</el-button>
      </el-upload>
    </div>

    <el-row :gutter="20">
      <el-col :span="8" v-for="item in materialList" :key="item.id">
        <el-card :body-style="{ padding: '0px' }" class="material-card">
          <img :src="item.url" class="material-image">
          <div class="material-info">
            <div class="info-item">
              <span class="label">类型：</span>
              <span>{{ item.typeName }}</span>
            </div>
            <div class="info-item">
              <span class="label">大小：</span>
              <span>{{ formatSize(item.size) }}</span>
            </div>
            <div class="info-item">
              <span class="label">上传时间：</span>
              <span>{{ item.createTime }}</span>
            </div>
            <div class="operations">
              <el-button type="text" @click="handleView(item)">查看</el-button>
              <el-button type="text" class="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查看素材详情对话框 -->
    <el-dialog title="素材详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="素材类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="素材大小">{{ formatSize(detail.size) }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="使用状态">
          {{ detail.adId ? '已使用' : '未使用' }}
        </el-descriptions-item>
      </el-descriptions>
      <div class="preview-container">
        <img :src="detail.url" class="preview-image">
      </div>
      <div class="usage-info" v-if="detail.adId">
        <h3>使用情况</h3>
        <p>当前用于广告：{{ detail.adTitle }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { uploadMaterial, getMaterialList, getMaterialDetail, deleteMaterial } from '@/api/material'

export default {
  name: 'MaterialManagement',
  data() {
    return {
      materialList: [],
      detailVisible: false,
      detail: {},
      loading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getMaterialList().then(response => {
        this.materialList = response.data
        this.loading = false
      })
    },
    formatSize(size) {
      if (size < 1024) {
        return size + 'B'
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + 'KB'
      } else {
        return (size / (1024 * 1024)).toFixed(2) + 'MB'
      }
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      if (!isLt10M) {
        this.$message.error('上传图片大小不能超过 10MB！')
        return false
      }
      return true
    },
    handleUpload(params) {
      const formData = new FormData()
      formData.append('file', params.file)
      formData.append('type', 1) // 1表示图片类型

      uploadMaterial(formData).then(() => {
        this.$message.success('上传成功')
        this.getList()
      }).catch(() => {
        this.$message.error('上传失败')
      })
    },
    handleView(item) {
      getMaterialDetail(item.id).then(response => {
        this.detail = response.data
        this.detailVisible = true
      })
    },
    handleDelete(item) {
      this.$confirm('确认删除该素材吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMaterial(item.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.material-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .material-card {
    margin-bottom: 20px;
    .material-image {
      width: 100%;
      height: 200px;
      object-fit: cover;
    }
    .material-info {
      padding: 14px;
      .info-item {
        margin-bottom: 8px;
        font-size: 14px;
        .label {
          color: #666;
        }
      }
      .operations {
        margin-top: 10px;
        text-align: right;
        .danger {
          color: #F56C6C;
        }
      }
    }
  }

  .preview-container {
    margin-top: 20px;
    text-align: center;
    .preview-image {
      max-width: 100%;
      max-height: 400px;
    }
  }

  .usage-info {
    margin-top: 20px;
    h3 {
      margin-bottom: 10px;
    }
  }
}
</style> 