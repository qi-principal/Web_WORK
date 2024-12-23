# 广告审核页面组件
<template>
  <div class="ad-review-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="广告主">
          <el-input v-model="listQuery.advertiser" placeholder="请输入广告主" />
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
      <el-table-column prop="title" label="广告标题" />
      <el-table-column prop="advertiserName" label="广告主" />
      <el-table-column prop="type" label="广告类型">
        <template slot-scope="{row}">
          {{ row.typeName }}
        </template>
      </el-table-column>
      <el-table-column prop="budget" label="预算">
        <template slot-scope="{row}">
          ¥{{ row.budget }}
        </template>
      </el-table-column>
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

    <!-- 查看广告详情对话框 -->
    <el-dialog title="广告详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="广告标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="广告主">{{ detail.advertiserName }}</el-descriptions-item>
        <el-descriptions-item label="广告类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="点击链接">{{ detail.clickUrl }}</el-descriptions-item>
        <el-descriptions-item label="投放时间">
          {{ detail.startTime }} 至 {{ detail.endTime }}
        </el-descriptions-item>
        <el-descriptions-item label="预算">¥{{ detail.budget }}</el-descriptions-item>
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
import { getPendingAdList, approveAd, rejectAd } from '@/api/admin'

export default {
  name: 'AdReview',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        advertiser: undefined
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
      getPendingAdList(this.listQuery).then(response => {
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
      this.$confirm('确认通过该广告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        approveAd(row.id).then(() => {
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
          rejectAd(this.rejectForm.id, this.rejectForm.reason).then(() => {
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
.ad-review-container {
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
}
</style> 