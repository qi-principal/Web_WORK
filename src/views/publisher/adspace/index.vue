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
      <el-table-column label="所属网站">
        <template slot-scope="{row}">
          {{ getWebsiteName(row.websiteId) }}
        </template>
      </el-table-column>
      <el-table-column prop="size" label="尺寸" />
      <el-table-column prop="code" label="广告位代码" show-overflow-tooltip>
        <template slot-scope="{row}">
          <el-tooltip effect="dark" placement="top">
            <div slot="content">
              <pre style="max-width: 400px; white-space: pre-wrap;">{{ row.code }}</pre>
            </div>
            <el-button type="text" @click="handleCopyCode(row.code)">查看代码</el-button>
          </el-tooltip>
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
          <el-button type="text" @click="handleCopyCode(row.code)">复制代码</el-button>
          <el-button type="text" @click="handlePreview(row)">预览</el-button>
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
          <el-input v-model="temp.name" placeholder="请输入广告位名称"/>
        </el-form-item>
        <el-form-item label="广告位尺寸">
          <el-col :span="11">
            <el-form-item prop="width">
              <el-input v-model="temp.width" placeholder="宽度">
                <template slot="append">px</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col class="line" :span="2" style="text-align: center">x</el-col>
          <el-col :span="11">
            <el-form-item prop="height">
              <el-input v-model="temp.height" placeholder="高度">
                <template slot="append">px</template>
              </el-input>
            </el-form-item>
          </el-col>
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

    <!-- 广告预览对话框 -->
    <el-dialog 
      title="广告预览" 
      :visible.sync="previewVisible" 
      width="800px"
      :close-on-click-modal="false"
      custom-class="preview-dialog">
      <div class="preview-container">
        <div class="preview-wrapper" :style="{ width: currentAd.width + 'px', height: currentAd.height + 'px' }">
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
import { getAdSpaceList, createAdSpace, updateAdSpace, getAdSpace, getAdSpaceCode } from '@/api/adspace'
import { getWebsiteList, getWebsiteByUserId } from '@/api/website'
import { getUserInfo } from '@/api/auth'
import Clipboard from 'clipboard'

export default {
  name: 'AdSpaceManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      initLoading: false,
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
        width: '',
        height: '',
        status: 0
      },
      detail: {},
      adCode: '',
      websiteOptions: [],
      rules: {
        websiteId: [{ required: true, message: '请选择网站', trigger: 'change' }],
        name: [{ required: true, message: '请输入广告位名称', trigger: 'blur' }],
        width: [{ required: true, message: '请输入宽度', trigger: 'blur' }],
        height: [{ required: true, message: '请输入高度', trigger: 'blur' }]
      },
      previewVisible: false,
      currentAd: {
        width: 0,
        height: 0,
        displayPageUrl: ''
      }
    }
  },
  async created() {
    await this.initData()
  },
  methods: {
    async initData() {
      console.log('开始初始化数据...')
      this.initLoading = true
      try {
        // 1. 先获取当前用户信息
        console.log('1. 开始获取用户信息...')
        const userResponse = await getUserInfo()
        if (!userResponse.data) {
          console.error('获取用户信息失败：响应数据为空')
          throw new Error('获取用户信息失败')
        }
        const userId = userResponse.data.id
        console.log('获取用户信息成功：', userResponse.data)

        // 2. 获取用户的网站列表
        console.log(`2. 开始获取用户(${userId})的网站列表...`)
        const websiteResponse = await getWebsiteByUserId(userId)
        console.log('网站列表响应数据：', websiteResponse)
        console.log('websiteResponse.data 的类型：', typeof websiteResponse.data)
        console.log('websiteResponse.data 的值：', websiteResponse.data)
        
        if (Array.isArray(websiteResponse.data)) {
          console.log('websiteResponse.data 是数组类型')
        } else if (typeof websiteResponse.data === 'object') {
          console.log('websiteResponse.data 是对象类型，具体属性：', Object.keys(websiteResponse.data))
          if (websiteResponse.data.records) {
            console.log('包含 records 属性，其值为：', websiteResponse.data.records)
          }
        }

        // 检查响应数据结构
        if (!websiteResponse || !websiteResponse.data) {
          console.error('获取网站列表失败：响应数据为空')
          throw new Error('获取网站列表失败')
        }

        // 根据数据类型设置 websiteOptions
        if (Array.isArray(websiteResponse.data)) {
          this.websiteOptions = websiteResponse.data
        } else if (websiteResponse.data.records) {
          this.websiteOptions = websiteResponse.data.records
        } else if (typeof websiteResponse.data === 'object') {
          this.websiteOptions = [websiteResponse.data]
        }
        
        console.log('最终处理后的网站列表数据：', this.websiteOptions)
        
        // 3. 如果有网站，默认选择第一个网站
        if (this.websiteOptions && this.websiteOptions.length > 0) {
          this.listQuery.websiteId = this.websiteOptions[0].id
          console.log(`3. 选择默认网站，ID：${this.listQuery.websiteId}`)
          // 4. 获取广告位列表
          console.log('4. 开始获取广告位列表...')
          await this.getList()
        } else {
          console.warn('没有找到任何网站')
          this.$message.warning('您还没有添加任何网站，请先添加网站')
        }
        console.log('数据初始化完成')
      } catch (error) {
        console.error('初始化数据失败：', error)
        this.$message.error(error.message || '获取数据失败')
      } finally {
        this.initLoading = false
        console.log('初始化加载状态已重置')
      }
    },
    async getList() {
      console.log('开始获取广告位列表...')
      console.log('查询参数：', this.listQuery)
      
      if (!this.listQuery.websiteId) {
        console.warn('未选择网站，无法获取广告位列表')
        this.$message.warning('请先选择网站')
        return
      }
      
      this.listLoading = true
      try {
        console.log(`正在获取网站(${this.listQuery.websiteId})的广告位列表...`)
        const response = await getAdSpaceList(this.listQuery.websiteId, {
          status: this.listQuery.status,
          page: this.listQuery.current,
          size: this.listQuery.size
        })
        
        console.log('广告位列表原始响应：', response)
        
        if (!response.data) {
          console.error('获取广告位列表失败：响应数据为空')
          throw new Error('获取广告位列表失败')
        }

        // 直接使用返回的数组
        this.list = response.data.map(item => ({
          ...item,
          size: `${item.width}x${item.height}`,
          statusName: item.status === 0 ? '待审核' : 
                     item.status === 1 ? '审核通过' : 
                     item.status === 2 ? '审核拒绝' : '未知状态'
        }))
        this.total = response.data.length
        
        console.log('处理后的广告位列表：', {
          total: this.total,
          currentPage: this.listQuery.current,
          pageSize: this.listQuery.size,
          list: this.list
        })
      } catch (error) {
        console.error('获取广告位列表失败：', error)
        this.$message.error(error.message || '获取广告位列表失败')
      } finally {
        this.listLoading = false
        console.log('列表加载状态已重置')
      }
    },
    handleFilter() {
      console.log('触发筛选操作')
      this.listQuery.current = 1
      this.getList()
    },
    handleSizeChange(val) {
      console.log('改变每页显示数量：', val)
      this.listQuery.size = val
      this.getList()
    },
    handleCurrentChange(val) {
      console.log('改变当前页码：', val)
      this.listQuery.current = val
      this.getList()
    },
    resetTemp() {
      this.temp = {
        websiteId: undefined,
        name: '',
        width: '',
        height: '',
        status: 0
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
      console.log('开始创建广告位...')
      console.log('原始表单数据：', this.temp)
      
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            // 1. 构造请求数据
            const requestData = {
              websiteId: this.temp.websiteId,
              name: this.temp.name,
              width: this.temp.width,
              height: this.temp.height,
              status: this.temp.status
            }
            
            console.log('处理后的请求数据：', requestData)

            // 2. 发送创建请求
            console.log(`开始发送创建请求，网站ID：${requestData.websiteId}`)
            const response = await createAdSpace(requestData.websiteId, requestData)
            console.log('创建广告位响应数据：', response)

            // 3. 检查响应结果
            if (!response.data) {
              throw new Error('创建广告位失败：响应数据为空')
            }

            // 4. 创建成功，关闭对话框并刷新列表
            this.dialogVisible = false
            this.$message({
              type: 'success',
              message: '创建成功'
            })
            console.log('广告位创建成功，开始刷新列表...')
            await this.getList()
            
          } catch (error) {
            console.error('创建广告位失败：', error)
            this.$message.error(error.message || '创建失败')
          }
        } else {
          console.warn('表单验证失败')
          return false
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
    },
    // 获取网站名称的方法
    getWebsiteName(websiteId) {
      const website = this.websiteOptions.find(item => item.id === websiteId)
      return website ? website.name : '未知网站'
    },
    // 复制代码的方法
    handleCopyCode(code) {
      const clipboard = new Clipboard('.copy-code-btn', {
        text: () => code
      })
      
      clipboard.on('success', () => {
        this.$message({
          message: '代码复制成功',
          type: 'success'
        })
        clipboard.destroy()
      })
      
      clipboard.on('error', () => {
        this.$message({
          message: '代码复制失败',
          type: 'error'
        })
        clipboard.destroy()
      })
      
      // 创建一个临时按钮来触发复制
      const button = document.createElement('button')
      button.className = 'copy-code-btn'
      document.body.appendChild(button)
      button.click()
      document.body.removeChild(button)
    },
    handlePreview(row) {
      if (!row.displayPageUrl) {
        this.$message.warning('该广告位暂无预览页面')
        return
      }
      
      this.currentAd = {
        width: parseInt(row.width) || 100,
        height: parseInt(row.height) || 100,
        displayPageUrl: row.displayPageUrl
      }
      this.previewVisible = true
      console.log('预览广告位：', this.currentAd)
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