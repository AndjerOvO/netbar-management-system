<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="商品分类" clearable style="width: 200px">
          <el-option label="饮料" value="饮料" />
          <el-option label="零食" value="零食" />
          <el-option label="外设" value="外设" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="上架状态" clearable style="width: 200px">
          <el-option label="上架" value="0" />
          <el-option label="下架" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['netcafe:product:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['netcafe:product:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['netcafe:product:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="商品分类" align="center" prop="category" width="100" />
      <el-table-column label="售价" align="center" prop="price" width="100">
        <template #default="scope">
          <span>{{ scope.row.price }} 元</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" align="center" prop="stock" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.stock !== null && scope.row.warningStock !== null && scope.row.stock < scope.row.warningStock" type="danger">
            {{ scope.row.stock }}
          </el-tag>
          <span v-else>{{ scope.row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预警阈值" align="center" prop="warningStock" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['netcafe:product:edit']">修改</el-button>
          <el-button link type="warning" icon="Plus" @click="handleStockAdjust(scope.row)" v-hasPermi="['netcafe:product:edit']">库存</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['netcafe:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" v-model="open" width="550px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择商品分类" style="width: 100%">
            <el-option label="饮料" value="饮料" />
            <el-option label="零食" value="零食" />
            <el-option label="外设" value="外设" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品售价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="form.stock" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="warningStock">
          <el-input-number v-model="form.warningStock" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上架状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">上架</el-radio>
            <el-radio value="1">下架</el-radio>
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

    <!-- 库存调整对话框 -->
    <el-dialog title="库存调整" v-model="stockOpen" width="400px" append-to-body>
      <el-form ref="stockRef" :model="stockForm" :rules="stockRules" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stockForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustQuantity">
          <el-input-number v-model="stockForm.adjustQuantity" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整类型">
          <el-radio-group v-model="stockForm.adjustType">
            <el-radio value="in">入库（增加库存）</el-radio>
            <el-radio value="out">出库（减少库存）</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitStockAdjust">确 定</el-button>
          <el-button @click="stockOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="NetcafeProduct">
import { listProduct, addProduct, getProduct, delProduct, updateProduct } from "@/api/netcafe/product"

const { proxy } = getCurrentInstance()

const productList = ref([])
const open = ref(false)
const stockOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  stockForm: {
    productId: undefined,
    productName: '',
    currentStock: 0,
    adjustQuantity: undefined,
    adjustType: 'in'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productName: undefined,
    category: undefined,
    status: undefined
  },
  rules: {
    productName: [{ required: true, message: "商品名称不能为空", trigger: "blur" }],
    category: [{ required: true, message: "商品分类不能为空", trigger: "change" }],
    price: [{ required: true, message: "商品售价不能为空", trigger: "blur" }],
  },
  stockRules: {
    adjustQuantity: [{ required: true, message: "调整数量不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules, stockForm, stockRules } = toRefs(data)

function getList() {
  loading.value = true
  listProduct(queryParams.value).then(response => {
    productList.value = response.rows
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
    productName: undefined,
    category: undefined,
    price: undefined,
    stock: 0,
    warningStock: 10,
    status: '0',
    remark: undefined
  }
  proxy.resetForm("productRef")
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
  title.value = "添加商品"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getProduct(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改商品"
  })
}

function submitForm() {
  proxy.$refs["productRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateProduct(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addProduct(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const productIds = row.id || ids.value.join(',')
  proxy.$modal.confirm('是否确认删除商品"' + (row.productName || productIds) + '"？').then(function() {
    return delProduct(productIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleStockAdjust(row) {
  stockForm.value = {
    productId: row.id,
    productName: row.productName,
    currentStock: row.stock,
    adjustQuantity: undefined,
    adjustType: 'in'
  }
  stockOpen.value = true
}

function submitStockAdjust() {
  proxy.$refs["stockRef"].validate(valid => {
    if (valid) {
      let newStock = stockForm.value.currentStock
      if (stockForm.value.adjustType === 'in') {
        newStock += stockForm.value.adjustQuantity
      } else {
        newStock = Math.max(0, newStock - stockForm.value.adjustQuantity)
      }
      updateProduct({
        id: stockForm.value.productId,
        stock: newStock
      }).then(() => {
        proxy.$modal.msgSuccess("库存调整成功")
        stockOpen.value = false
        getList()
      })
    }
  })
}

getList()
</script>
