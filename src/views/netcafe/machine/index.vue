<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="机器编号" prop="machineNo">
        <el-input v-model="queryParams.machineNo" placeholder="请输入机器编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="机器类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="机器类型" clearable style="width: 200px">
          <el-option label="普通区" value="普通区" />
          <el-option label="竞技区" value="竞技区" />
          <el-option label="包厢" value="包厢" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="机器状态" clearable style="width: 200px">
          <el-option label="空闲" value="0" />
          <el-option label="使用中" value="1" />
          <el-option label="故障" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['netcafe:machine:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['netcafe:machine:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['netcafe:machine:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="machineList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机器编号" align="center" prop="machineNo" />
      <el-table-column label="机器类型" align="center" prop="type" width="100" />
      <el-table-column label="每小时单价" align="center" prop="pricePerHour" width="110">
        <template #default="scope">
          <span>{{ scope.row.pricePerHour }} 元/时</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="160">
        <template #default="scope">
          <el-button
            :type="scope.row.status === '0' ? 'success' : scope.row.status === '1' ? 'warning' : 'danger'"
            size="small"
            plain
            @click="toggleStatus(scope.row)"
            v-hasPermi="['netcafe:machine:edit']"
            :disabled="scope.row.status === '1'"
          >
            {{ scope.row.status === '0' ? '空闲' : scope.row.status === '1' ? '使用中' : '故障' }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['netcafe:machine:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['netcafe:machine:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改机器对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="machineRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="机器编号" prop="machineNo">
          <el-input v-model="form.machineNo" placeholder="请输入机器编号" />
        </el-form-item>
        <el-form-item label="机器类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择机器类型" style="width: 100%">
            <el-option label="普通区" value="普通区" />
            <el-option label="竞技区" value="竞技区" />
            <el-option label="包厢" value="包厢" />
          </el-select>
        </el-form-item>
        <el-form-item label="每小时单价" prop="pricePerHour">
          <el-input-number v-model="form.pricePerHour" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="NetcafeMachine">
import { listMachine, addMachine, getMachine, delMachine, updateMachine } from "@/api/netcafe/machine"

const { proxy } = getCurrentInstance()

const machineList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    machineNo: undefined,
    type: undefined,
    status: undefined
  },
  rules: {
    machineNo: [{ required: true, message: "机器编号不能为空", trigger: "blur" }],
    type: [{ required: true, message: "机器类型不能为空", trigger: "change" }],
    pricePerHour: [{ required: true, message: "单价不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listMachine(queryParams.value).then(response => {
    machineList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    machineNo: undefined,
    type: undefined,
    pricePerHour: undefined,
    status: '0',
    remark: undefined
  }
  proxy.resetForm("machineRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加机器"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getMachine(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改机器"
  })
}

function submitForm() {
  proxy.$refs["machineRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateMachine(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addMachine(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const machineIds = row.id || ids.value.join(',')
  proxy.$modal.confirm('是否确认删除机器编号为"' + (row.machineNo || machineIds) + '"的数据项？').then(function() {
    return delMachine(machineIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function toggleStatus(row) {
  if (row.status === '1') return
  const newStatus = row.status === '0' ? '2' : '0'
  const text = newStatus === '0' ? '恢复空闲' : '标记故障'
  proxy.$modal.confirm('确认要' + text + '该机器吗？').then(function() {
    return updateMachine({ id: row.id, status: newStatus })
  }).then(() => {
    proxy.$modal.msgSuccess(text + '成功')
    getList()
  }).catch(() => {})
}

getList()
</script>
