# 网站管理页面组件
<template>
  <div class="website-container">
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
          <el-button type="primary" @click="handleCreate">新建网站</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column prop="name" label="网站名称" />
      <el-table-column prop="domain" label="域名" />
      <el-table-column prop="category" label="网站分类" />
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

    <!-- 新建/编辑网站对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新建网站' : '编辑网站'" :visible.sync="dialogVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="网站名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="网站域名" prop="domain">
          <el-input v-model="temp.domain" />
        </el-form-item>
        <el-form-item label="网站分类" prop="category">
          <el-select v-model="temp.category" placeholder="请选择网站分类">
            <el-option label="新闻资讯" value="news" />
            <el-option label="社交媒体" value="social" />
            <el-option label="电子商务" value="ecommerce" />
            <el-option label="游戏娱乐" value="game" />
            <el-option label="教育培训" value="education" />
            <el-option label="其他" value="other" />
          </el-select>
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

    <!-- 查看网站详情对话框 -->
    <el-dialog title="网站详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="网站名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="网站域名">{{ detail.domain }}</el-descriptions-item>
        <el-descriptions-item label="网站分类">{{ detail.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="网站简介" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
      <div class="space-list" v-if="detail.spaces && detail.spaces.length">
        <h3>广告位列表</h3>
        <el-table :data="detail.spaces" border>
          <el-table-column prop="name" label="广告位名称" />
          <el-table-column prop="type" label="广告位类型" />
          <el-table-column prop="size" label="尺寸" />
          <el-table-column prop="status" label="状态">
            <template slot-scope="{row}">
              <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'">
                {{ row.statusName }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWebsiteList, createWebsite, updateWebsite, deleteWebsite, getWebsite, submitWebsiteReview } from '@/api/website'

export default {
  name: 'WebsiteManagement',
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
        name: '',
        domain: '',
        category: undefined,
        description: ''
      },
      detail: {},
      rules: {
        name: [{ required: true, message: '请输入网站名称', trigger: 'blur' }],
        domain: [{ required: true, message: '请输入网站域名', trigger: 'blur' }],
        category: [{ required: true, message: '请选择网站分类', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getWebsiteList(this.listQuery).then(response => {
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
        name: '',
        domain: '',
        category: undefined,
        description: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createWebsite(this.temp).then(() => {
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
          updateWebsite(data.id, data).then(() => {
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
      this.$confirm('确认删除该网站吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteWebsite(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功'
          })
          this.getList()
        })
      })
    },
    handleView(row) {
      getWebsite(row.id).then(response => {
        this.detail = response.data
        this.detailVisible = true
      })
    },
    handleSubmit(row) {
      this.$confirm('确认提交审核吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        submitWebsiteReview(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '提交成功'
          })
          this.getList()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.website-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .space-list {
    margin-top: 20px;
    h3 {
      margin-bottom: 15px;
    }
  }

  .danger {
    color: #F56C6C;
  }
}
</style> 