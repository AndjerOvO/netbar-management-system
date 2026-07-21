<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="会员姓名" prop="memberName">
        <el-input v-model="queryParams.memberName" placeholder="请输入会员姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="机台编号" prop="machineNo">
        <el-input v-model="queryParams.machineNo" placeholder="请输入机台编号" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="上机中" value="0" />
          <el-option label="已下机" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" style="width:100%">
      <el-table-column label="记录ID" align="center" prop="id" width="100" />
      <el-table-column label="会员姓名" align="center" prop="memberName" min-width="100" />
      <el-table-column label="机台编号" align="center" prop="machineNo" min-width="110" />
      <el-table-column label="开始时间" align="center" min-width="160">
        <template #default="{ row }">{{ parseTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" min-width="160">
        <template #default="{ row }">{{ parseTime(row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="时长(分)" align="center" prop="totalMinutes" width="100" />
      <el-table-column label="金额" align="center" prop="totalAmount" width="100" />
      <el-table-column label="状态" align="center" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'warning' : 'success'">{{ row.status === '0' ? '上机中' : '已下机' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <el-button v-if="row.status === '0'" link type="danger" @click="handleForceEnd(row)" v-hasPermi="['netcafe:usage:end']">强制下机</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="NetcafeUsage">
import { listUsageRecord, endUsage } from "@/api/netcafe/sale"

const { proxy } = getCurrentInstance()
const loading = ref(true), showSearch = ref(true), total = ref(0)
const dateRange = ref([])
const list = ref([])

const data = reactive({
  queryParams: { pageNum:1, pageSize:10, memberName:undefined, machineNo:undefined, status:undefined }
})
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  const p = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    p.params = { beginTime: dateRange.value[0], endTime: dateRange.value[1] }
  }
  listUsageRecord(p).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { dateRange.value = []; proxy.resetForm("queryRef"); handleQuery() }
function handleForceEnd(row) {
  proxy.$modal.confirm('确认强制下机 [机台' + row.machineNo + ']？').then(() => {
    endUsage(row.id).then(() => { proxy.$modal.msgSuccess('已强制下机'); getList() })
  }).catch(() => {})
}
getList()
</script>
