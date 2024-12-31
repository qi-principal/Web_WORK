<template>
  <div class="ad-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button type="primary" @click="handleCreate">新建广告</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="广告标题" />
      <el-table-column prop="type" label="广告类型">
        <template slot-scope="{row}">
          {{ row.typeName }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'">
            {{ row.statusName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="text" @click="handleView(row)">查看</el-button>
          <el-button type="text" @click="handlePreview(row)">预览</el-button>
          <el-button 
            v-if="row.status === 0"
            type="text" 
            @click="handleSubmit(row)">
            提交审核
          </el-button>
          <el-button 
            type="text" 
            class="danger"
            @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
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

    <!-- 新建/编辑广告对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新建广告' : '编辑广告'" :visible.sync="dialogVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="广告标题" prop="title">
          <el-input v-model="temp.title" />
        </el-form-item>
        <el-form-item label="广告类型" prop="type">
          <el-select v-model="temp.type" placeholder="请选择广告类型">
            <el-option label="图片广告" :value="1" />
            <el-option label="视频广告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="广告描述" prop="description">
          <el-input type="textarea" v-model="temp.description" />
        </el-form-item>
        <el-form-item label="点击链接" prop="clickUrl">
          <el-input v-model="temp.clickUrl" />
        </el-form-item>
        <el-form-item label="投放时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="总预算" prop="budget">
          <el-input-number v-model="temp.budget" :min="0" />
        </el-form-item>
        <el-form-item label="日预算" prop="dailyBudget">
          <el-input-number v-model="temp.dailyBudget" :min="0" />
        </el-form-item>
        <el-form-item label="广告素材" prop="materials">
          <div class="material-upload-list" v-if="temp.materials && temp.materials.length">
            <el-row :gutter="20">
              <el-col :span="8" v-for="(material, index) in temp.materials" :key="material.id || index">
                <el-card :body-style="{ padding: '0px' }">
                  <img :src="material.url" class="material-image">
                  <div class="material-info">
                    <div class="bottom clearfix">
                      <el-button type="text" class="danger" @click="removeMaterial(index)">删除</el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-upload
            class="material-uploader"
            action="#"
            :http-request="handleMaterialUpload"
            :show-file-list="false"
            :before-upload="beforeMaterialUpload">
            <el-button type="primary">上传素材</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">确定</el-button>
      </div>
    </el-dialog>

    <!-- 查看广告详情对话框 -->
    <el-dialog title="广告详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="广告标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="广告类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusName }}</el-descriptions-item>
        <el-descriptions-item label="点击链接">{{ detail.clickUrl }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="总预算">{{ detail.budget }}</el-descriptions-item>
        <el-descriptions-item label="日预算">{{ detail.dailyBudget }}</el-descriptions-item>
        <el-descriptions-item label="广告描述" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
      <div class="material-list" v-if="detail.materials && detail.materials.length">
        <h3>广告素材</h3>
        <el-row :gutter="20">
          <el-col :span="8" v-for="material in detail.materials" :key="material.id">
            <el-card :body-style="{ padding: '0px' }">
              <img :src="material.url" class="material-image">
              <div class="material-info">
                <span>{{ material.typeName }}</span>
                <div class="bottom">
                  <time>{{ material.createTime }}</time>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 广告预览对话框 -->
    <el-dialog 
      title="广告预览" 
      :visible.sync="previewVisible" 
      width="800px"
      :close-on-click-modal="false"
      custom-class="preview-dialog">
      <div class="preview-container">
        <div class="preview-wrapper">
          <iframe 
            :src="currentAd.displayPageUrl"
            :width="currentAd.width"
            :height="currentAd.height"
            frameborder="0"
            scrolling="no">
          </iframe>
        </div>
        <div class="preview-info">
          <p>广告位尺寸：{{ currentAd.width }} x {{ currentAd.height }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAdList, createAd, updateAd, deleteAd, getAdDetail, submitAdReview } from '@/api/ad'
import { uploadMaterial } from '@/api/material'

export default {
  name: 'AdManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        status: undefined
      },
      dialogVisible: false,
      dialogStatus: 'create',
      detailVisible: false,
      temp: {
        title: '',
        type: undefined,
        description: '',
        clickUrl: '',
        budget: 0,
        dailyBudget: 0,
        materials: []
      },
      detail: {},
      dateRange: [],
      rules: {
        title: [{ required: true, message: '请输入广告标题', trigger: 'blur' }],
        type: [{ required: true, message: '请选择广告类型', trigger: 'change' }],
        clickUrl: [{ required: true, message: '请输入点击链接', trigger: 'blur' }],
        budget: [{ required: true, message: '请输入总预算', trigger: 'blur' }],
        dailyBudget: [{ required: true, message: '请输入日预算', trigger: 'blur' }],
        materials: [{ required: true, message: '请上传广告素材', trigger: 'change' }]
      },
      multipleSelection: [],
      previewVisible: false,
      currentAd: {
        displayPageUrl: '',
        width: 100,
        height: 100
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getAdList(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.current = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.size = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.current = val
      this.getList()
    },
    resetTemp() {
      this.temp = {
        title: '',
        type: undefined,
        description: '',
        clickUrl: '',
        budget: 0,
        dailyBudget: 0,
        materials: []
      }
      this.dateRange = []
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    beforeMaterialUpload(file) {
      console.log('广告页面-上传前的文件信息：', {
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
    handleMaterialUpload(params) {
      const userInfo = this.$store.state.user.userInfo;
      if (!userInfo || !userInfo.id) {
        this.$message.error('获取用户信息失败');
        return;
      }

      console.log('广告页面-准备上传的参数：', params);
      const formData = new FormData();
      formData.append('file', params.file);
      formData.append('type', 1); // 1表示图片类型
      formData.append('content', params.file.name); // 添加content字段，默认使用文件名
      formData.append('userId', userInfo.id); // 添加用户ID
      
      console.log('广告页面-FormData内容：');
      for (let [key, value] of formData.entries()) {
        console.log(key, ':', value instanceof File ? {
          name: value.name,
          type: value.type,
          size: value.size
        } : value);
      }

      uploadMaterial(formData).then(response => {
        console.log('广告页面-上传成功响应：', response);
        this.temp.materials.push(response.data);
        this.$message.success('上传成功');
      }).catch(error => {
        console.error('广告页面-上传失败：', error);
        this.$message.error('上传失败');
      });
    },
    removeMaterial(index) {
      console.log('广告页面-移除素材：', {
        index,
        removedMaterial: this.temp.materials[index]
      })
      this.temp.materials.splice(index, 1)
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = Object.assign({}, this.temp)
          if (this.dateRange.length === 2) {
            data.startTime = this.dateRange[0]
            data.endTime = this.dateRange[1]
          }
          data.materialIds = this.temp.materials.map(item => item.id)
          createAd(data).then(() => {
            this.dialogVisible = false
            this.$message({
              type: 'success',
              message: '创建成功'
            })
            this.getList()
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dateRange = [this.temp.startTime, this.temp.endTime]
      this.dialogStatus = 'update'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = Object.assign({}, this.temp)
          if (this.dateRange.length === 2) {
            data.startTime = this.dateRange[0]
            data.endTime = this.dateRange[1]
          }
          data.materialIds = this.temp.materials.map(item => item.id)
          updateAd(data.id, data).then(() => {
            this.dialogVisible = false
            this.$message({
              type: 'success',
              message: '更新成功'
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该广告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteAd(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功'
          })
          this.getList()
        })
      })
    },
    handleView(row) {
      getAdDetail(row.id).then(response => {
        this.detail = response.data
        console.log("detail")
        console.log(response.data)
        this.detailVisible = true
      })
    },
    handleSubmit(row) {
      this.$confirm('确认提交审核吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        submitAdReview(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '提交成功'
          })
          this.getList()
        })
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handlePreview(row) {
      if (!row.displayPageUrl) {
        this.$message.warning('该广告暂无预览页面')
        return
      }
      
      this.currentAd = {
        displayPageUrl: row.displayPageUrl,
        width: row.width || 100,
        height: row.height || 100
      }
      this.previewVisible = true
      console.log('预览广告：', this.currentAd)
    }
  }
}
</script>

<style lang="scss" scoped>
.ad-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .material-list {
    margin-top: 20px;
    h3 {
      margin-bottom: 15px;
    }
    .material-image {
      width: 100%;
      height: 150px;
      object-fit: cover;
    }
    .material-info {
      padding: 10px;
      .bottom {
        margin-top: 5px;
        font-size: 12px;
        color: #999;
      }
    }
  }

  .danger {
    color: #F56C6C;
  }

  .material-upload-list {
    margin-bottom: 20px;
    .material-image {
      width: 100%;
      height: 150px;
      object-fit: cover;
    }
    .material-info {
      padding: 10px;
      .bottom {
        margin-top: 5px;
        text-align: right;
      }
    }
  }

  .preview-dialog {
    .preview-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .preview-wrapper {
        background: #f5f7fa;
        padding: 20px;
        border-radius: 4px;
        margin-bottom: 20px;
        box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
      }

      .preview-info {
        text-align: center;
        color: #666;
        font-size: 14px;
      }
    }
  }
}
</style> 