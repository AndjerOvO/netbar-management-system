<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="会员姓名" prop="memberName">
        <el-input v-model="queryParams.memberName" placeholder="请输入会员姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
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

    <el-table v-loading="loading" :data="list">
      <el-table-column label="记录ID" align="center" prop="id" width="80" />
      <el-table-column label="会员姓名" align="center" prop="memberName" width="100" />
      <el-table-column label="充值金额" align="center" prop="amount" width="100" />
      <el-table-column label="支付方式" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="row.payType==='0'?'info':'warning'">{{ row.payType==='0'?'现金':'微信' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="operator" width="120" />
      <el-table-column label="充值时间" align="center" width="160">
        <template #default="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="NetcafeRecharge">
import { listRechargeRecord } from "@/api/netcafe/sale"

const { proxy } = getCurrentInstance()
const loading = ref(true), showSearch = ref(true), total = ref(0)
const dateRange = ref([])
const list = ref([])

const data = reactive({
  queryParams: { pageNum:1, pageSize:10, memberName:undefined }
})
const { queryParams } = toRefs(data)

function getList() {
  loading.value = true
  const p = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    p.params = { beginTime: dateRange.value[0], endTime: dateRange.value[1] }
  }
  listRechargeRecord(p).then(res => { list.value = res.rows; total.value = res.total; loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { dateRange.value = []; proxy.resetForm("queryRef"); handleQuery() }
getList()
</script>
