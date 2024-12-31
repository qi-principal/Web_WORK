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
              <span class="label">描述：</span>
              <span>{{ item.content }}</span>
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

    <el-pagination
      v-if="usePagination"
      class="pagination-container"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="listQuery.current"
      :page-sizes="[10, 20, 30, 50]"
      :page-size="listQuery.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- 查看素材详情对话框 -->
    <el-dialog title="素材详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="素材类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="素材描述">{{ detail.content }}</el-descriptions-item>
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
import { uploadMaterial, getMaterialList, getMaterialListByPage, getMaterialDetail, deleteMaterial } from '@/api/material'

export default {
  name: 'MaterialManagement',
  data() {
    return {
      materialList: [],
      detailVisible: false,
      detail: {},
      loading: false,
      usePagination: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10
      },
      userId: null
    }
  },
  created() {
    const userInfo = this.$store.state.user.userInfo;
    console.log('当前用户信息：', userInfo);
    
    if (!userInfo) {
      // 如果没有用户信息，先获取用户信息
      this.$store.dispatch('user/getUserInfo')
        .then(data => {
          console.log('获取到的用户信息：', data);
          this.userId = data.id;
          this.getList();
        })
        .catch(error => {
          console.error('获取用户信息失败：', error);
          this.$message.error('获取用户信息失败');
        });
    } else {
      this.userId = userInfo.id;
      this.getList();
    }
  },
  methods: {
    getList() {
      if (!this.userId) {
        this.$message.error('用户ID不能为空');
        return;
      }

      this.loading = true;
      const request = this.usePagination
        ? getMaterialListByPage(this.userId, this.listQuery)
        : getMaterialList(this.userId);

      request
        .then(response => {
          if (this.usePagination) {
            this.materialList = response.data.records;
            this.total = response.data.total;
          } else {
            this.materialList = response.data;
          }
        })
        .catch(error => {
          console.error('获取素材列表失败:', error);
          this.$message.error('获取素材列表失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleSizeChange(val) {
      this.listQuery.size = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.current = val;
      this.getList();
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
      console.log('素材页面-上传前的文件信息：', {
        fileName: file.name,
        fileType: file.type,
        fileSize: file.size,
        lastModified: file.lastModified
      })
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
      if (!this.userId) {
        this.$message.error('用户ID不能为空');
        return;
      }

      console.log('素材页面-准备上传的参数：', params);
      const formData = new FormData();
      formData.append('file', params.file);
      formData.append('type', 1); // 1表示图片类型
      formData.append('content', params.file.name); // 添加content字段，默认使用文件名
      formData.append('userId', this.userId); // 添加用户ID

      console.log('素材页面-FormData内容：');
      for (let [key, value] of formData.entries()) {
        console.log(key, ':', value instanceof File ? {
          name: value.name,
          type: value.type,
          size: value.size
        } : value);
      }

      uploadMaterial(formData)
        .then(response => {
          console.log('素材页面-上传成功响应：', response);
          this.$message.success('上传成功');
          this.getList();
        })
        .catch(error => {
          console.error('素材页面-上传失败：', error);
          this.$message.error('上传失败');
        });
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

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
}
</style> 