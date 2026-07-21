<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日营收</div>
              <div class="stat-value">{{ overview.todayIncome.toFixed(2) }} 元</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="32"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">在线人数</div>
              <div class="stat-value">{{ overview.onlineCount }} 人</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="32"><Cpu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">设备总数</div>
              <div class="stat-value">{{ overview.totalMachines }} 台</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">上座率</div>
              <div class="stat-value">{{ overview.usageRate }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-card shadow="hover">
      <template #header>
        <span>近7天营收趋势</span>
      </template>
      <div ref="chartRef" style="height: 360px"></div>
    </el-card>
  </div>
</template>

<script setup name="Index">
import { getOverview } from "@/api/netcafe/dashboard"
import * as echarts from 'echarts'

const chartRef = ref(null)
const overview = reactive({
  todayIncome: 0,
  onlineCount: 0,
  totalMachines: 0,
  usageRate: 0,
  trend: []
})

let chartInstance = null

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        return params[0].name + '<br/>营收：' + params[0].value.toFixed(2) + ' 元'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: getPast7Days()
    },
    yAxis: {
      type: 'value',
      name: '营收（元）',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '营收',
        type: 'line',
        smooth: true,
        lineStyle: { color: '#2d5a4b', width: 3 },
        itemStyle: { color: '#2d5a4b' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(45, 90, 75, 0.18)' },
            { offset: 1, color: 'rgba(45, 90, 75, 0.01)' }
          ])
        },
        data: overview.trend,
        markLine: {
          silent: true,
          lineStyle: { color: '#c9a84c', type: 'dashed' },
          data: [{ type: 'average', name: '平均值' }]
        }
      }
    ]
  }
  chartInstance.setOption(option)
}

function getPast7Days() {
  const days = []
  const now = new Date()
  for (let i = 6; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    days.push(month + '-' + day)
  }
  return days
}

function fetchData() {
  getOverview().then(response => {
    const data = response.data
    overview.todayIncome = data.todayIncome || 0
    overview.onlineCount = data.onlineCount || 0
    overview.totalMachines = data.totalMachines || 0
    overview.usageRate = data.usageRate || 0
    overview.trend = data.trend || []
    initChart()
  })
}

onMounted(() => {
  fetchData()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

window.addEventListener('resize', () => {
  if (chartInstance) {
    chartInstance.resize()
  }
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  /* 左上角墨绿色装饰条 */
  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0;
    width: 4px; height: 40px;
    background: #2d5a4b;
    border-radius: 0 4px 4px 0;
    pointer-events: none;
  }
}
.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.stat-info { flex: 1; }
.stat-label {
  font-size: 14px;
  color: #5a6a7a;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a2332;
  letter-spacing: 1px;
}
.mb20 { margin-bottom: 20px; }
</style>
