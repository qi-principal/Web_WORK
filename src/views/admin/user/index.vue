# 用户管理页面组件
<template>
  <div class="user-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="用户类型">
          <el-select v-model="listQuery.type" placeholder="请选择用户类型" clearable>
            <el-option label="广告主" :value="1" />
            <el-option label="网站主" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="type" label="用户类型">
        <template slot-scope="{row}">
          {{ row.typeName }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="{row}">
          <el-button 
            type="text"
            @click="handleStatusChange(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button type="text" @click="handleView(row)">查看</el-button>
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

    <!-- 查看用户详情对话框 -->
    <el-dialog title="用户详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
            {{ detail.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录时间">{{ detail.lastLoginTime }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 广告主信息 -->
      <div v-if="detail.type === 1" class="advertiser-info">
        <h3>广告主信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="广告数量">{{ detail.adCount }}</el-descriptions-item>
          <el-descriptions-item label="投放中广告">{{ detail.activeAdCount }}</el-descriptions-item>
          <el-descriptions-item label="总消费">¥{{ detail.totalCost }}</el-descriptions-item>
          <el-descriptions-item label="账户余额">¥{{ detail.balance }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 网站主信息 -->
      <div v-if="detail.type === 2" class="publisher-info">
        <h3>网站主信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="网站数量">{{ detail.websiteCount }}</el-descriptions-item>
          <el-descriptions-item label="广告位数量">{{ detail.adSpaceCount }}</el-descriptions-item>
          <el-descriptions-item label="总收益">¥{{ detail.totalRevenue }}</el-descriptions-item>
          <el-descriptions-item label="待结算金额">¥{{ detail.pendingRevenue }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, updateUserStatus } from '@/api/admin'

export default {
  name: 'UserManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        current: 1,
        size: 10,
        type: undefined,
        status: undefined
      },
      detailVisible: false,
      detail: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getUserList(this.listQuery).then(response => {
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
    handleStatusChange(row) {
      const status = row.status === 1 ? 0 : 1
      const statusText = status === 1 ? '启用' : '禁用'
      this.$confirm(`确认${statusText}该用户吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateUserStatus(row.id, status).then(() => {
          this.$message({
            type: 'success',
            message: `${statusText}成功`
          })
          this.getList()
        })
      })
    },
    handleView(row) {
      this.detail = row
      this.detailVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.user-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .advertiser-info,
  .publisher-info {
    margin-top: 20px;
    h3 {
      margin-bottom: 15px;
    }
  }
}
</style> 