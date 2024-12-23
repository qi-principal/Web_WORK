<template>
  <div class="delivery-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" clearable>
            <el-option label="待投放" :value="0" />
            <el-option label="投放中" :value="1" />
            <el-option label="已暂停" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button type="primary" @click="handleCreate">新建投放</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="adTitle" label="广告标题" />
      <el-table-column prop="spaceName" label="广告位" />
      <el-table-column label="投放时间" width="300">
        <template slot-scope="{row}">
          {{ row.startTime }} 至 {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="{row}">
          <el-button 
            v-if="row.status === 0 || row.status === 2"
            type="text" 
            @click="handleStart(row)">
            开始投放
          </el-button>
          <el-button 
            v-if="row.status === 1"
            type="text" 
            @click="handlePause(row)">
            暂停投放
          </el-button>
          <el-button 
            v-if="row.status !== 3 && row.status !== 4"
            type="text" 
            class="danger"
            @click="handleCancel(row)">
            取消投放
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

    <!-- 新建投放对话框 -->
    <el-dialog title="新建投放" :visible.sync="dialogVisible">
      <el-form ref="dataForm" :model="temp" :rules="rules" label-width="100px">
        <el-form-item label="选择广告" prop="adId">
          <el-select v-model="temp.adId" placeholder="请选择广告" filterable>
            <el-option
              v-for="item in adOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择广告位" prop="spaceId">
          <el-select v-model="temp.spaceId" placeholder="请选择广告位" filterable>
            <el-option
              v-for="item in spaceOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投放时间" prop="scheduleTime">
          <el-date-picker
            v-model="temp.scheduleTime"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDeliveryTasks, createDeliveryTask, updateDeliveryTaskStatus } from '@/api/delivery'
import { getAdList } from '@/api/ad'
import { getAdSpaceList } from '@/api/adspace'

export default {
  name: 'DeliveryManagement',
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
      temp: {
        adId: undefined,
        spaceId: undefined,
        scheduleTime: []
      },
      rules: {
        adId: [{ required: true, message: '请选择广告', trigger: 'change' }],
        spaceId: [{ required: true, message: '请选择广告位', trigger: 'change' }],
        scheduleTime: [{ required: true, message: '请选择投放时间', trigger: 'change' }]
      },
      adOptions: [],
      spaceOptions: []
    }
  },
  created() {
    this.getList()
    this.getAdOptions()
    this.getSpaceOptions()
  },
  methods: {
    getList() {
      this.listLoading = true
      getDeliveryTasks(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getAdOptions() {
      getAdList({ status: 1 }).then(response => {
        this.adOptions = response.data.records
      })
    },
    getSpaceOptions() {
      getAdSpaceList({ status: 1 }).then(response => {
        this.spaceOptions = response.data.records
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
        adId: undefined,
        spaceId: undefined,
        scheduleTime: []
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = {
            adId: this.temp.adId,
            spaceId: this.temp.spaceId,
            startTime: this.temp.scheduleTime[0],
            endTime: this.temp.scheduleTime[1]
          }
          createDeliveryTask(data).then(() => {
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
    handleStart(row) {
      this.updateStatus(row.id, 1)
    },
    handlePause(row) {
      this.updateStatus(row.id, 2)
    },
    handleCancel(row) {
      this.$confirm('确认取消该投放任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.updateStatus(row.id, 4)
      })
    },
    updateStatus(id, status) {
      updateDeliveryTaskStatus(id, status).then(() => {
        this.$message({
          type: 'success',
          message: '操作成功'
        })
        this.getList()
      })
    },
    getStatusType(status) {
      const statusMap = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: '',
        4: 'danger'
      }
      return statusMap[status]
    },
    getStatusName(status) {
      const statusMap = {
        0: '待投放',
        1: '投放中',
        2: '已暂停',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.delivery-container {
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