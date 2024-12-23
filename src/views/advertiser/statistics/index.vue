<template>
  <div class="statistics-container">
    <div class="filter-container">
      <el-form :inline="true" :model="query">
        <el-form-item label="广告">
          <el-select v-model="query.adId" placeholder="请选择广告" clearable>
            <el-option
              v-for="item in adOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="query.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-title">总展示次数</div>
          <div class="card-value">{{ statistics.impressions }}</div>
          <div class="card-trend">
            较前日
            <span :class="['trend', statistics.impressionsTrend >= 0 ? 'up' : 'down']">
              {{ Math.abs(statistics.impressionsTrend) }}%
              <i :class="statistics.impressionsTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-title">总点击次数</div>
          <div class="card-value">{{ statistics.clicks }}</div>
          <div class="card-trend">
            较前日
            <span :class="['trend', statistics.clicksTrend >= 0 ? 'up' : 'down']">
              {{ Math.abs(statistics.clicksTrend) }}%
              <i :class="statistics.clicksTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-title">点击率</div>
          <div class="card-value">{{ statistics.ctr }}%</div>
          <div class="card-trend">
            较前日
            <span :class="['trend', statistics.ctrTrend >= 0 ? 'up' : 'down']">
              {{ Math.abs(statistics.ctrTrend) }}%
              <i :class="statistics.ctrTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-title">消费金额</div>
          <div class="card-value">¥{{ statistics.cost }}</div>
          <div class="card-trend">
            较前日
            <span :class="['trend', statistics.costTrend >= 0 ? 'up' : 'down']">
              {{ Math.abs(statistics.costTrend) }}%
              <i :class="statistics.costTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>展示/点击趋势</span>
          </div>
          <div class="chart-container">
            <!-- 这里使用echarts图表 -->
            <div ref="trendChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>消费趋势</span>
          </div>
          <div class="chart-container">
            <!-- 这里使用echarts图表 -->
            <div ref="costChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <div slot="header">
        <span>数据明细</span>
      </div>
      <el-table
        :data="dailyStats"
        border
        style="width: 100%">
        <el-table-column prop="date" label="日期" width="180" />
        <el-table-column prop="impressions" label="展示次数" />
        <el-table-column prop="clicks" label="点击次数" />
        <el-table-column prop="ctr" label="点击率">
          <template slot-scope="{row}">
            {{ row.ctr }}%
          </template>
        </el-table-column>
        <el-table-column prop="cost" label="消费金额">
          <template slot-scope="{row}">
            ¥{{ row.cost }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getAdList } from '@/api/ad'
import * as echarts from 'echarts'

export default {
  name: 'Statistics',
  data() {
    return {
      query: {
        adId: undefined,
        dateRange: []
      },
      adOptions: [],
      statistics: {
        impressions: 10000,
        clicks: 500,
        ctr: 5,
        cost: 1000,
        impressionsTrend: 10,
        clicksTrend: -5,
        ctrTrend: 2,
        costTrend: 15
      },
      dailyStats: [
        {
          date: '2023-12-25',
          impressions: 1200,
          clicks: 60,
          ctr: 5,
          cost: 120
        },
        {
          date: '2023-12-26',
          impressions: 1500,
          clicks: 75,
          ctr: 5,
          cost: 150
        }
      ],
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      trendChart: null,
      costChart: null
    }
  },
  mounted() {
    this.getAdOptions()
    this.initCharts()
    // 监听窗口大小变化，重新调整图表大小
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.costChart) {
      this.costChart.dispose()
    }
  },
  methods: {
    getAdOptions() {
      getAdList({ status: 1 }).then(response => {
        this.adOptions = response.data.records
      })
    },
    handleQuery() {
      // 这里调用后端API获取数据
      this.updateCharts()
    },
    initCharts() {
      // 初始化趋势图表
      this.trendChart = echarts.init(this.$refs.trendChart)
      this.trendChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['展示次数', '点击次数']
        },
        xAxis: {
          type: 'category',
          data: ['12-20', '12-21', '12-22', '12-23', '12-24', '12-25', '12-26']
        },
        yAxis: [
          {
            type: 'value',
            name: '展示次数'
          },
          {
            type: 'value',
            name: '点击次数'
          }
        ],
        series: [
          {
            name: '展示次数',
            type: 'line',
            data: [1000, 1200, 1100, 1300, 1400, 1200, 1500]
          },
          {
            name: '点击次数',
            type: 'line',
            yAxisIndex: 1,
            data: [50, 60, 55, 65, 70, 60, 75]
          }
        ]
      })

      // 初始化消费图表
      this.costChart = echarts.init(this.$refs.costChart)
      this.costChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['12-20', '12-21', '12-22', '12-23', '12-24', '12-25', '12-26']
        },
        yAxis: {
          type: 'value',
          name: '消费金额（元）'
        },
        series: [
          {
            type: 'bar',
            data: [100, 120, 110, 130, 140, 120, 150]
          }
        ]
      })
    },
    updateCharts() {
      // 更新图表数据
      if (this.trendChart && this.costChart) {
        // 这里使用实际数据更新图表
        this.trendChart.setOption({
          // 更新数据
        })
        this.costChart.setOption({
          // 更新数据
        })
      }
    },
    resizeCharts() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
      if (this.costChart) {
        this.costChart.resize()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.statistics-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .data-card {
    .card-title {
      font-size: 14px;
      color: #666;
    }
    .card-value {
      font-size: 24px;
      font-weight: bold;
      margin: 10px 0;
    }
    .card-trend {
      font-size: 12px;
      color: #666;
      .trend {
        &.up {
          color: #67C23A;
        }
        &.down {
          color: #F56C6C;
        }
      }
    }
  }

  .chart-row {
    margin-top: 20px;
  }

  .chart-card {
    .chart-container {
      height: 300px;
      .chart {
        width: 100%;
        height: 100%;
      }
    }
  }

  .table-card {
    margin-top: 20px;
  }
}
</style> 