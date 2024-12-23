# 广告位审核页面组件
<template>
  <div class="adspace-review-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="网站主">
          <el-input v-model="listQuery.publisher" placeholder="请输入网站主" />
        </el-form-item>
        <el-form-item label="网站">
          <el-input v-model="listQuery.website" placeholder="请输入网站" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
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
      <el-table-column prop="publisherName" label="网站主" />
      <el-table-column prop="type" label="广告位类型">
        <template slot-scope="{row}">
          {{ row.typeName }}
        </template>
      </el-table-column>
      <el-table-column prop="size" label="尺寸" />
      <el-table-column prop="submitTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleView(row)">查看</el-button>
          <el-button type="text" @click="handleApprove(row)">通过</el-button>
          <el-button type="text" class="danger" @click="handleReject(row)">拒绝</el-button>
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

    <!-- 查看广告位详情对话框 -->
    <el-dialog title="广告位详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="广告位名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="所属网站">{{ detail.websiteName }}</el-descriptions-item>
        <el-descriptions-item label="网站主">{{ detail.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="广告位类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="尺寸">{{ detail.size }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="广告位描述" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog title="拒绝原因" :visible.sync="rejectVisible" width="500px">
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules">
        <el-form-item label="拒绝原因" prop="reason">
          <el-input
            type="textarea"
            v-model="rejectForm.reason"
            :rows="4"
            placeholder="请输入拒绝原因">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingAdSpaceList, approveAdSpace, rejectAdSpace } from '@/api/admin'

export default {
  name: 'AdSpaceReview',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        publisher: undefined,
        website: undefined
      },
      detailVisible: false,
      rejectVisible: false,
      detail: {},
      rejectForm: {
        id: undefined,
        reason: ''
      },
      rejectRules: {
        reason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getPendingAdSpaceList(this.listQuery).then(response => {
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
    handleView(row) {
      this.detail = row
      this.detailVisible = true
    },
    handleApprove(row) {
      this.$confirm('确认通过该广告位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        approveAdSpace(row.id).then(() => {
          this.$message({
            type: 'success',
            message: '审核通过成功'
          })
          this.getList()
        })
      })
    },
    handleReject(row) {
      this.rejectForm.id = row.id
      this.rejectForm.reason = ''
      this.rejectVisible = true
      this.$nextTick(() => {
        this.$refs['rejectForm'].clearValidate()
      })
    },
    submitReject() {
      this.$refs['rejectForm'].validate((valid) => {
        if (valid) {
          rejectAdSpace(this.rejectForm.id, this.rejectForm.reason).then(() => {
            this.rejectVisible = false
            this.$message({
              type: 'success',
              message: '审核拒绝成功'
            })
            this.getList()
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.adspace-review-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .danger {
    color: #F56C6C;
  }
}
</style> 