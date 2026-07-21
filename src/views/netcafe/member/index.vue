<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="会员姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入会员姓名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="会员等级" prop="level">
        <el-select v-model="queryParams.level" placeholder="会员等级" clearable style="width: 200px">
          <el-option label="普通" value="0" />
          <el-option label="黄金" value="1" />
          <el-option label="钻石" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="会员状态" clearable style="width: 200px">
          <el-option label="正常" value="0" />
          <el-option label="冻结" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['netcafe:member:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="User" @click="handleTempCard" v-hasPermi="['netcafe:member:add']">开临时卡</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['netcafe:member:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['netcafe:member:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="memberList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会员编号" align="center" prop="memberNo" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180">
        <template #default="scope">
          <span>{{ maskIdCard(scope.row.idCard) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员姓名" align="center" prop="name" />
      <el-table-column label="手机号码" align="center" prop="phone" width="120" />
      <el-table-column label="账户余额" align="center" prop="balance" width="100">
        <template #default="scope">
          <span>{{ scope.row.balance }} 元</span>
        </template>
      </el-table-column>
      <el-table-column label="累计消费" align="center" prop="totalSpent" width="100">
        <template #default="scope">
          <span>{{ scope.row.totalSpent }} 元</span>
        </template>
      </el-table-column>
      <el-table-column label="会员等级" align="center" prop="level" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.level === '0'" type="info">普通</el-tag>
          <el-tag v-else-if="scope.row.level === '1'" type="warning">黄金</el-tag>
          <el-tag v-else-if="scope.row.level === '2'" type="danger">钻石</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会员类型" align="center" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.memberType==='1'?'warning':''">{{ scope.row.memberType==='1'?'临时卡':'正式' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-switch v-if="scope.row.memberType!=='1'" v-model="scope.row.status" :active-value="'0'" :inactive-value="'1'" active-text="正常" inactive-text="冻结" @change="handleStatusChange(scope.row)" />
          <el-tag v-else :type="scope.row.status==='0'?'success':'danger'">{{ scope.row.status==='0'?'正常':'冻结' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期" align="center" width="160">
        <template #default="scope">
          <span v-if="scope.row.memberType==='1'">{{ parseTime(scope.row.expireTime) }}</span>
          <span v-else style="color:#909399">永久</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['netcafe:member:edit']">修改</el-button>
          <el-button link type="warning" icon="Key" @click="handleResetPwd(scope.row)" v-hasPermi="['netcafe:member:edit']">重置密码</el-button>
          <el-button link type="success" icon="Wallet" @click="handleRecharge(scope.row)" v-hasPermi="['netcafe:recharge:add']">充值</el-button>
          <el-button v-if="scope.row.memberType==='1'" link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-else link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['netcafe:member:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改会员对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form ref="memberRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="会员编号" prop="memberNo">
          <el-input v-model="form.memberNo" placeholder="请输入会员编号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="会员姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入会员姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="会员密码" v-if="!form.id">
          <el-input v-model="form.password" placeholder="密码默认为身份证后六位" show-password disabled />
          <span style="color:#909399;font-size:12px">密码默认为身份证后六位，无需填写</span>
        </el-form-item>
        <el-form-item label="初始余额" prop="balance" v-if="!form.id">
          <el-input-number v-model="form.balance" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="会员等级" prop="level">
          <el-radio-group v-model="form.level">
            <el-radio value="0">普通</el-radio>
            <el-radio value="1">黄金</el-radio>
            <el-radio value="2">钻石</el-radio>
          </el-radio-group>
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

    <!-- 开临时卡对话框 -->
    <el-dialog title="开临时卡" v-model="tempOpen" width="450px" append-to-body>
      <el-form ref="tempRef" :model="tempForm" :rules="tempRules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="tempForm.name" placeholder="顾客真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="tempForm.idCard" placeholder="18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="tempForm.phone" placeholder="选填" maxlength="11" />
        </el-form-item>
        <el-form-item label="初始余额" prop="balance">
          <el-input-number v-model="tempForm.balance" :min="0" :precision="2" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-alert title="密码默认为身份证后六位，有效期24小时" type="info" :closable="false" show-icon style="margin-top:8px" />
      </el-form>
      <template #footer>
        <el-button @click="tempOpen=false">取消</el-button>
        <el-button type="primary" :loading="tempLoading" @click="submitTempCard">确认开卡</el-button>
      </template>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog title="会员充值" v-model="rechargeOpen" width="400px" append-to-body>
      <el-form ref="rechargeRef" :model="rechargeForm" :rules="rechargeRules" label-width="90px">
        <el-form-item label="会员">
          <span>{{ rechargeForm.memberName }} ({{ rechargeForm.memberNo }})</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span>{{ rechargeForm.currentBalance }} 元</span>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="rechargeForm.amount" :min="1" :precision="2" controls-position="right" placeholder="请输入充值金额" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式" prop="payType">
          <el-radio-group v-model="rechargeForm.payType">
            <el-radio value="0">现金</el-radio>
            <el-radio value="1">微信</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRecharge">确 定</el-button>
          <el-button @click="rechargeOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="NetcafeMember">
import { listMember, addMember, getMember, delMember, updateMember, resetMemberPwd } from "@/api/netcafe/member"
import { rechargeMember } from "@/api/netcafe/sale"
import request from '@/utils/request'
import { ElMessageBox } from 'element-plus'

const { proxy } = getCurrentInstance()

const memberList = ref([])
const open = ref(false)
const rechargeOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 临时卡
const tempOpen = ref(false)
const tempLoading = ref(false)
const tempForm = reactive({ name: '', idCard: '', phone: '', balance: 0 })
const tempRules = {
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  idCard: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }, { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的18位身份证号', trigger: 'blur' }]
}

const data = reactive({
  form: {},
  rechargeForm: {
    memberId: undefined,
    memberName: '',
    memberNo: '',
    currentBalance: 0,
    amount: undefined,
    payType: '0'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    phone: undefined,
    idCard: undefined,
    level: undefined,
    status: undefined
  },
  rules: {
    memberNo: [{ required: true, message: "会员编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "会员姓名不能为空", trigger: "blur" }],
    idCard: [{ required: true, message: "身份证号不能为空", trigger: "blur" }, { pattern: /^\d{17}[\dXx]$/, message: "请输入正确的18位身份证号", trigger: "blur" }],
  },
  rechargeRules: {
    amount: [{ required: true, message: "充值金额不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules, rechargeForm, rechargeRules } = toRefs(data)

function getList() {
  loading.value = true
  listMember(queryParams.value).then(response => {
    memberList.value = response.rows
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
    memberNo: undefined,
    idCard: undefined,
    name: undefined,
    phone: undefined,
    password: undefined,
    balance: 0,
    level: '0',
    status: '0',
    remark: undefined
  }
  proxy.resetForm("memberRef")
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
  title.value = "添加会员"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getMember(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改会员"
  })
}

function submitForm() {
  proxy.$refs["memberRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateMember(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addMember(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const memberIds = row.id || ids.value.join(',')
  proxy.$modal.confirm('是否确认删除该会员？').then(function() {
    return delMember(memberIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleStatusChange(row) {
  const text = row.status === '0' ? '解冻' : '冻结'
  proxy.$modal.confirm('确认要' + text + '该会员吗？').then(function() {
    return updateMember({ id: row.id, status: row.status })
  }).then(() => {
    proxy.$modal.msgSuccess(text + '成功')
    getList()
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

function handleRecharge(row) {
  rechargeForm.value = {
    memberId: row.id,
    memberName: row.name,
    memberNo: row.memberNo,
    currentBalance: row.balance,
    amount: undefined,
    payType: '0'
  }
  rechargeOpen.value = true
}

function submitRecharge() {
  proxy.$refs["rechargeRef"].validate(valid => {
    if (valid) {
      rechargeMember({
        memberId: rechargeForm.value.memberId,
        amount: rechargeForm.value.amount,
        payType: rechargeForm.value.payType
      }).then(() => {
        proxy.$modal.msgSuccess("充值成功")
        rechargeOpen.value = false
        getList()
      })
    }
  })
}

// ---- 临时卡 ----
function handleTempCard() {
  tempForm.name = ''
  tempForm.idCard = ''
  tempForm.phone = ''
  tempForm.balance = 0
  tempOpen.value = true
}
function submitTempCard() {
  proxy.$refs["tempRef"].validate(valid => {
    if (!valid) return
    tempLoading.value = true
    request({
      url: '/netcafe/member/tempCard',
      method: 'post',
      data: { name: tempForm.name, idCard: tempForm.idCard, phone: tempForm.phone, balance: tempForm.balance }
    }).then(res => {
      const d = res.data || res
      const msg = `卡号: ${d.cardNo}<br/>密码: ${d.password}<br/>有效期至: ${new Date(d.expireTime).toLocaleString()}`
      ElMessageBox.alert(msg, '开卡成功', { dangerouslyUseHTMLString: true, confirmButtonText: '知道了' })
      tempOpen.value = false
      getList()
    }).catch(() => {
      proxy.$modal.msgError('开卡失败')
    }).finally(() => { tempLoading.value = false })
  })
}

function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) return idCard || '-'
  return idCard.substring(0, 3) + '***********' + idCard.substring(idCard.length - 4)
}

function handleResetPwd(row) {
  proxy.$modal.confirm('确认重置该会员密码为身份证后六位吗？').then(function() {
    return resetMemberPwd(row.id)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("密码已重置为身份证后六位")
  }).catch(() => {})
}

getList()
</script>
