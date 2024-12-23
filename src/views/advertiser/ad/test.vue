<template>
  <div class="test-container">
    <el-card>
      <div slot="header">
        <span>广告列表接口测试</span>
      </div>
      
      <!-- 测试参数表单 -->
      <el-form :inline="true" :model="testParams" class="test-form">
        <el-form-item label="状态">
          <el-select v-model="testParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="页码">
          <el-input-number v-model="testParams.current" :min="1" />
        </el-form-item>
        <el-form-item label="每页条数">
          <el-input-number v-model="testParams.size" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testGetAdList">测试接口</el-button>
        </el-form-item>
      </el-form>

      <!-- 请求信息 -->
      <div class="request-info" v-if="requestInfo.url">
        <h3>请求信息</h3>
        <p><strong>URL:</strong> {{ requestInfo.url }}</p>
        <p><strong>Method:</strong> {{ requestInfo.method }}</p>
        <p><strong>Parameters:</strong></p>
        <pre>{{ JSON.stringify(requestInfo.params, null, 2) }}</pre>
      </div>

      <!-- 响应结果 -->
      <div class="response-info" v-if="responseInfo.status !== undefined">
        <h3>响应结果</h3>
        <p><strong>HTTP Status:</strong> {{ responseInfo.status }}</p>
        <template v-if="responseInfo.data">
          <div class="data-section">
            <p><strong>总记录数:</strong> {{ responseInfo.data.total }}</p>
            <p><strong>当前页码:</strong> {{ responseInfo.data.current }}</p>
            <p><strong>每页大小:</strong> {{ responseInfo.data.size }}</p>
            <p><strong>数据列表:</strong></p>
            <el-table
              :data="responseInfo.data.records"
              border
              style="width: 100%">
              <el-table-column
                v-for="(value, key) in (responseInfo.data.records[0] || {})"
                :key="key"
                :prop="key"
                :label="formatColumnLabel(key)"
                :width="getColumnWidth(key)"
              />
            </el-table>
          </div>
        </template>
      </div>

      <!-- 错误信息 -->
      <div class="error-info" v-if="errorInfo">
        <h3>错误信息</h3>
        <pre class="error-message">{{ errorInfo }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getAdList } from '@/api/ad'
import axios from 'axios'

export default {
  name: 'AdApiTest',
  data() {
    return {
      testParams: {
        current: 1,
        size: 10,
        status: undefined,
        userId: undefined
      },
      requestInfo: {},
      responseInfo: {},
      errorInfo: null,
      columnLabels: {
        id: 'ID',
        title: '广告标题',
        type: '广告类型',
        status: '状态',
        clickUrl: '点击链接',
        description: '描述',
        budget: '总预算',
        dailyBudget: '日预算',
        startTime: '开始时间',
        endTime: '结束时间',
        createTime: '创建时间',
        updateTime: '更新时间',
        userId: '用户ID'
      }
    }
  },
  methods: {
    formatColumnLabel(key) {
      return this.columnLabels[key] || key
    },
    getColumnWidth(key) {
      switch (key) {
        case 'id':
          return '80'
        case 'createTime':
        case 'updateTime':
        case 'startTime':
        case 'endTime':
          return '180'
        case 'status':
          return '100'
        default:
          return ''
      }
    },
    async testGetAdList() {
      // 清空之前的结果
      this.requestInfo = {}
      this.responseInfo = {}
      this.errorInfo = null

      try {
        // 记录请求信息
        this.requestInfo = {
          url: '/v1/ads',
          method: 'GET',
          params: this.testParams
        }

        // 发起请求
        const response = await getAdList(this.testParams)
        
        // 记录响应信息
        this.responseInfo = {
          status: response.status,
          headers: response.headers,
          data: response.data
        }
      } catch (error) {
        // 记录错误信息
        this.errorInfo = {
          message: error.message,
          response: error.response ? {
            status: error.response.status,
            data: error.response.data
          } : null,
          config: error.config ? {
            url: error.config.url,
            method: error.config.method,
            params: error.config.params
          } : null
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.test-container {
  padding: 20px;

  .test-form {
    margin-bottom: 20px;
  }

  .request-info,
  .response-info,
  .error-info {
    margin-top: 20px;
    padding: 15px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;

    h3 {
      margin-top: 0;
      margin-bottom: 15px;
      color: #303133;
    }

    pre {
      background-color: #f5f7fa;
      padding: 10px;
      border-radius: 4px;
      overflow-x: auto;
    }
  }

  .error-info {
    .error-message {
      color: #f56c6c;
    }
  }

  .data-section {
    margin-top: 10px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;

    .el-table {
      margin-top: 10px;
    }
  }
}
</style> 