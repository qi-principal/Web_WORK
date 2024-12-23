# 广告位管理页面组件
<template>
  <div class="adspace-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="网站">
          <el-select v-model="listQuery.websiteId" placeholder="请选择网站" clearable>
            <el-option
              v-for="item in websiteOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button type="primary" @click="handleCreate">新建广告位</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column prop="name" label="广告位名称" />
      <el-table-column prop="websiteName" label="所属网站" />
      <el-table-column prop="type" label="广告位类型">
        <template slot-scope="{row}">
          {{ row.typeName }}
        </template>
      </el-table-column>
      <el-table-column prop="size" label="尺寸" />
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
          <el-button type="text" @click="handleCode(row)">获取代码</el-button>
          <el-button 
            v-if="row.status === 0"
            type="text" 
            @click="handleSubmit(row)">
            提交审核
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

    <!-- 新建/编辑广告位对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新建广告位' : '编辑广告位'" :visible.sync="dialogVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="所属网站" prop="websiteId">
          <el-select v-model="temp.websiteId" placeholder="请选择网站">
            <el-option
              v-for="item in websiteOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="广告位名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="广告位类型" prop="type">
          <el-select v-model="temp.type" placeholder="请选择广告位类型">
            <el-option label="横幅广告" :value="1" />
            <el-option label="开屏广告" :value="2" />
            <el-option label="信息流广告" :value="3" />
            <el-option label="视频广告" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="广告位尺寸" prop="size">
          <el-input v-model="temp.width" style="width: 100px" /> x
          <el-input v-model="temp.height" style="width: 100px" />
        </el-form-item>
        <el-form-item label="广告位描述" prop="description">
          <el-input type="textarea" v-model="temp.description" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">确定</el-button>
      </div>
    </el-dialog>

    <!-- 查看广告位详情对话框 -->
    <el-dialog title="广告位详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="广告位名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="所属网站">{{ detail.websiteName }}</el-descriptions-item>
        <el-descriptions-item label="广告位类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="尺寸">{{ detail.size }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="广告位描述" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 广告位代码对话框 -->
    <el-dialog title="广告位代码" :visible.sync="codeVisible" width="800px">
      <div class="code-container">
        <pre><code>{{ adCode }}</code></pre>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleCopy">复制代码</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAdSpaceList, createAdSpace, updateAdSpace, getAdSpace, getAdSpaceCode } from '@/api/adspace'
import { getWebsiteList } from '@/api/website'
import Clipboard from 'clipboard'

export default {
  name: 'AdSpaceManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        websiteId: undefined,
        status: undefined
      },
      dialogVisible: false,
      dialogStatus: 'create',
      detailVisible: false,
      codeVisible: false,
      temp: {
        websiteId: undefined,
        name: '',
        type: undefined,
        width: '',
        height: '',
        description: ''
      },
      detail: {},
      adCode: '',
      websiteOptions: [],
      rules: {
        websiteId: [{ required: true, message: '请选择网站', trigger: 'change' }],
        name: [{ required: true, message: '请输入广告位名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择广告位类型', trigger: 'change' }],
        width: [{ required: true, message: '请输入宽度', trigger: 'blur' }],
        height: [{ required: true, message: '请输入高度', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getWebsiteOptions()
  },
  methods: {
    getList() {
      this.listLoading = true
      getAdSpaceList(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getWebsiteOptions() {
      getWebsiteList({ status: 1 }).then(response => {
        this.websiteOptions = response.data.records
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
        websiteId: undefined,
        name: '',
        type: undefined,
        width: '',
        height: '',
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
          const data = Object.assign({}, this.temp)
          data.size = `${data.width}x${data.height}`
          createAdSpace(data.websiteId, data).then(() => {
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
      const [width, height] = row.size.split('x')
      this.temp.width = width
      this.temp.height = height
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
          data.size = `${data.width}x${data.height}`
          updateAdSpace(data.id, data).then(() => {
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
    handleView(row) {
      getAdSpace(row.id).then(response => {
        this.detail = response.data
        this.detailVisible = true
      })
    },
    handleCode(row) {
      getAdSpaceCode(row.id).then(response => {
        this.adCode = response.data
        this.codeVisible = true
      })
    },
    handleCopy() {
      const clipboard = new Clipboard('.copy-btn', {
        text: () => this.adCode
      })
      clipboard.on('success', () => {
        this.$message({
          message: '复制成功',
          type: 'success'
        })
        clipboard.destroy()
      })
      clipboard.on('error', () => {
        this.$message({
          message: '复制失败',
          type: 'error'
        })
        clipboard.destroy()
      })
      clipboard.onClick(event)
    }
  }
}
</script>

<style lang="scss" scoped>
.adspace-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .code-container {
    background-color: #f5f7fa;
    padding: 15px;
    border-radius: 4px;
    pre {
      margin: 0;
      code {
        font-family: Monaco, Menlo, Consolas, "Courier New", monospace;
        font-size: 14px;
      }
    }
  }
}
</style> 