<template>
  <div class="member-app">
    <!-- 顶部栏 -->
    <header class="top-bar">
      <div class="top-left">
        <h2>网咖会员中心</h2>
      </div>
      <div class="top-right">
        <span v-if="isGuest" class="member-info">临时卡</span>
        <span v-else class="member-info">{{ member.name }} | {{ member.memberNo }}</span>
        <el-tag v-if="isGuest" type="info" size="small">散客</el-tag>
        <el-tag v-else-if="member.level === '2'" type="danger" size="small">钻石会员</el-tag>
        <el-tag v-else-if="member.level === '1'" type="warning" size="small">黄金会员</el-tag>
        <el-tag v-else size="small">普通会员</el-tag>
        <el-button type="danger" size="small" @click="handleCheckout">结账下机</el-button>
      </div>
    </header>

    <!-- 强制下线倒计时横幅 -->
    <div v-if="balanceZero && countdownSeconds > 0" class="countdown-bar">
      <el-icon><WarningFilled /></el-icon>
      余额已用尽，系统将在 <strong>{{ countdownMinutes }} 分 {{ countdownSeconds % 60 }} 秒</strong> 后强制下线！
    </div>

    <!-- 主体 -->
    <div class="main-content">
      <!-- 左侧：余额卡片 -->
      <div class="card balance-card" :class="{ danger: balanceDanger }">
        <div class="card-label">账户余额</div>
        <div v-if="isGuest" class="card-value" style="font-size:22px;color:#8a9aa8">临时卡</div>
        <div v-else class="card-value">¥ {{ member.balance?.toFixed(2) || '0.00' }}</div>
        <div v-if="!isGuest" class="card-sub">累计消费 ¥ {{ member.totalSpent?.toFixed(2) || '0.00' }}</div>
        <el-button v-if="!isGuest" type="primary" size="small" @click="showRecharge = true" style="margin-top:12px">在线充值</el-button>
        <el-button type="danger" size="small" @click="handleGuestCheckout" style="margin-top:12px" v-if="isGuest && usage">立即结账</el-button>
      </div>

      <!-- 中间：计时卡片 -->
      <div class="card timer-card">
        <div class="card-label">上机计时</div>
        <div v-if="usage" class="card-value timer">{{ elapsedStr }}</div>
        <div v-else class="card-value" style="font-size:18px;color:#8a9aa8">未上机</div>
        <div class="card-sub" v-if="usage">机位：{{ usage.machineNo }}</div>
        <el-button v-if="!usage" type="primary" size="small" @click="showStartUsage = true" style="margin-top:12px">选择机位上机</el-button>
      </div>

      <!-- 右侧：快捷操作 -->
      <div class="card actions-card">
        <div class="card-label">快捷操作</div>
        <el-button type="warning" size="small" style="width:100%;margin-bottom:10px" @click="showRecharge = true">充值</el-button>
        <el-button size="small" style="width:100%;margin-bottom:10px" @click="showRecords = true">上机记录</el-button>
      </div>
    </div>

    <!-- 价格明细 -->
    <div class="pricing-section">
      <div class="card pricing-card">
        <div class="card-label">价格明细</div>
        <el-table :data="priceList" size="small" stripe>
          <el-table-column label="区域类型" prop="type" />
          <el-table-column label="普通会员" align="center">
            <template #default="{ row }">¥{{ row.normal }}/时</template>
          </el-table-column>
          <el-table-column label="黄金会员" align="center">
            <template #default="{ row }">¥{{ row.gold }}/时</template>
          </el-table-column>
          <el-table-column label="钻石会员" align="center">
            <template #default="{ row }">¥{{ row.diamond }}/时</template>
          </el-table-column>
        </el-table>
        <div class="pricing-tip">当前等级：<el-tag v-if="member.level==='2'" type="danger" size="small">钻石会员</el-tag><el-tag v-else-if="member.level==='1'" type="warning" size="small">黄金会员</el-tag><el-tag v-else size="small">普通会员</el-tag>，享受对应折扣</div>
      </div>
    </div>

    <!-- 🏪 小卖部 -->
    <div class="shop-section">
      <el-divider><span style="font-size:16px">🏪 网咖小卖部</span></el-divider>

      <el-empty v-if="productList.length === 0" description="暂无商品销售，请稍后再来" :image-size="80" />

      <el-row v-else :gutter="16">
        <el-col v-for="p in productList" :key="p.id" :xs="12" :sm="8" :lg="6" style="margin-bottom:16px">
          <div class="product-card" :class="{ soldout: p.stock <= 0 }">
            <el-badge :value="getCartQty(p.id)" :hidden="!getCartQty(p.id)" class="product-badge">
              <div class="product-img">
                <el-icon :size="40" color="#C0C4CC"><PictureFilled /></el-icon>
              </div>
            </el-badge>
            <div class="product-name">{{ p.productName }}</div>
            <div class="product-price">¥{{ p.price }}</div>
            <div class="product-category">
              <el-tag size="small" :type="p.category === '饮料' ? 'success' : p.category === '零食' ? 'warning' : 'info'">{{ p.category }}</el-tag>
              <span class="stock-text" v-if="p.stock <= 5"> 剩{{ p.stock }}件</span>
            </div>
            <div class="product-actions">
              <template v-if="p.stock <= 0">
                <el-button size="small" disabled>售罄</el-button>
              </template>
              <template v-else>
                <el-button size="small" v-if="!getCartQty(p.id)" type="primary" plain @click="addToCart(p)">+ 添加</el-button>
                <div v-else class="qty-control">
                  <el-button size="small" circle @click="removeFromCart(p)">-</el-button>
                  <span class="qty-num">{{ getCartQty(p.id) }}</span>
                  <el-button size="small" circle type="primary" @click="addToCart(p)">+</el-button>
                </div>
              </template>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 购物车结算栏 -->
      <div v-if="cart.length > 0" class="cart-bar">
        <div class="cart-summary">
          <span class="cart-title">🛒 购物车</span>
          <span v-for="(item, i) in cart" :key="i" class="cart-item-tag">
            {{ item.productName }} ×{{ item.quantity }}
          </span>
        </div>
        <div class="cart-checkout">
          <span class="cart-total">合计：<strong>¥{{ totalPrice.toFixed(2) }}</strong></span>
          <el-button type="primary" size="large" @click="handleCartCheckout">
            商品结算（{{ checkPayType }}）
          </el-button>
        </div>
      </div>
    </div>

    <!-- 临时卡支付对话框 -->
    <el-dialog v-model="showPayDialog" title="结账支付" width="320px" append-to-body>
      <div style="text-align:center">
        <el-radio-group v-model="guestPayType" style="margin-bottom:16px">
          <el-radio-button value="0">现金</el-radio-button>
          <el-radio-button value="2">微信</el-radio-button>
        </el-radio-group>
        <el-button type="danger" @click="confirmGuestPay" style="width:100%">确认支付并下机</el-button>
      </div>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog v-model="showRecharge" title="在线充值" width="350px" append-to-body>
      <div style="text-align:center">
        <p style="margin-bottom:16px">当前余额：<strong>¥ {{ member.balance?.toFixed(2) || '0.00' }}</strong></p>
        <el-radio-group v-model="rechargeAmount" style="margin-bottom:16px">
          <el-radio-button :label="10">10元</el-radio-button>
          <el-radio-button :label="50">50元</el-radio-button>
          <el-radio-button :label="100">100元</el-radio-button>
        </el-radio-group>
        <el-button type="primary" :loading="recharging" @click="doRecharge" style="width:100%">确认充值</el-button>
      </div>
    </el-dialog>

    <!-- 上机开台对话框 -->
    <el-dialog v-model="showStartUsage" title="选择机位" width="400px" append-to-body>
      <el-select v-model="selectedMachine" placeholder="请选择空闲机位" style="width:100%" size="large">
        <el-option v-for="m in idleMachines" :key="m.id" :label="m.machineNo + ' (' + m.type + ' ¥' + m.pricePerHour + '/时)'" :value="m.id" />
      </el-select>
      <el-button type="primary" :loading="startingUsage" @click="doStartUsage" style="width:100%;margin-top:16px" size="large">开台上机</el-button>
    </el-dialog>

    <!-- 上机记录对话框 -->
    <el-dialog v-model="showRecords" title="近7天上机记录" width="600px" append-to-body>
      <el-table :data="records" size="small">
        <el-table-column label="机位" prop="machineNo" width="100" />
        <el-table-column label="开始时间" width="160">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="160">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="时长(分)" prop="totalMinutes" width="80" />
        <el-table-column label="金额" prop="totalAmount" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { getMemberInfo, getCurrentUsage, getRecords, getMachines, recharge, startUsage, endUsage, getProducts, createSaleOrder, heartbeat } from '@/api/member/index'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()

const token = ref(localStorage.getItem('member-token') || '')
const isGuest = ref(!!localStorage.getItem('temp-token'))
const tempInfo = ref(JSON.parse(localStorage.getItem('temp-info') || '{}'))
const member = reactive({ name: '', memberNo: '', balance: 0, totalSpent: 0, level: '0' })
const usage = ref(null)
const records = ref([])
const idleMachines = ref([])
const productList = ref([])
const cart = reactive([])

const totalPrice = computed(() => {
  return cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const checkPayType = computed(() => {
  const hasPeripheral = cart.some(item => item.category === '外设')
  return hasPeripheral ? '余额支付' : '余额支付'
})

function getCartQty(productId) {
  const found = cart.find(item => item.productId === productId)
  return found ? found.quantity : 0
}

function addToCart(p) {
  const found = cart.find(item => item.productId === p.id)
  if (found) {
    if (found.quantity < p.stock) found.quantity++
    else { ElMessage.warning('库存不足'); return }
  } else {
    cart.push({ productId: p.id, productName: p.productName, price: p.price, quantity: 1, category: p.category, stock: p.stock })
  }
}

function removeFromCart(p) {
  const idx = cart.findIndex(item => item.productId === p.id)
  if (idx >= 0) {
    if (cart[idx].quantity > 1) cart[idx].quantity--
    else cart.splice(idx, 1)
  }
}

function fetchProducts() {
  getProducts().then(res => {
    productList.value = res.data || []
  }).catch(() => {})
}

function handleCartCheckout() {
  if (cart.length === 0) { ElMessage.warning('购物车为空'); return }
  const hasPeripheral = cart.some(item => item.category === '外设')
  const msg = hasPeripheral ? '确认结算？外设租借已计入费用，下机时请归还至前台。' : '确认使用余额支付 ¥' + totalPrice.value.toFixed(2) + '？'
  ElMessageBox.confirm(msg, '确认结算', { confirmButtonText: '确认支付', type: 'warning' }).then(() => {
    const items = cart.map(item => ({ productId: item.productId, quantity: item.quantity }))
    createSaleOrder(token.value, items, '1').then(res => {
      ElMessage.success('购买成功！')
      cart.length = 0
      fetchMemberInfo()
      fetchProducts()
    }).catch(err => {
      const msg = err?.data?.msg || err?.response?.data?.msg || '支付失败'
      if (msg.includes('余额不足')) {
        ElMessageBox.alert('余额不足，请充值！', '支付失败', { confirmButtonText: '去充值', callback: () => { showRecharge.value = true } })
      } else {
        ElMessage.error(msg)
      }
    })
  }).catch(() => {})
}

// 价格表（普通/黄金/钻石）
const priceList = [
  { type: '普通区', normal: '5.00', gold: '4.50', diamond: '4.00' },
  { type: '竞技区', normal: '10.00', gold: '9.00', diamond: '8.00' },
  { type: '包厢', normal: '15.00', gold: '13.50', diamond: '12.00' }
]

// 对话框
const showRecharge = ref(false)
const showStartUsage = ref(false)
const showRecords = ref(false)
const rechargeAmount = ref(10)
const recharging = ref(false)
const startingUsage = ref(false)
const selectedMachine = ref(null)

function fetchMachines() {
  getMachines().then(res => {
    idleMachines.value = res.data || []
  }).catch(() => {})
}

// 计时器
const elapsedStr = ref('00:00:00')
let timerInterval = null

// 余额归零倒计时
const balanceZero = ref(false)
const countdownSeconds = ref(300)
const countdownMinutes = computed(() => Math.floor(countdownSeconds.value / 60))
let countdownInterval = null

// 余额<10危险状态
const balanceDanger = computed(() => member.balance > 0 && member.balance < 10)

// 最后低余额警告时间
let lastLowBalanceWarn = 0

// ========== 数据刷新 ==========
function fetchMemberInfo() {
  getMemberInfo(token.value).then(res => {
    const data = res.data || res
    Object.assign(member, data)
    if (data.balance <= 0 && data.balance !== null) {
      balanceZero.value = true
    } else if (data.balance > 0) {
      balanceZero.value = false
      countdownSeconds.value = 300
    }
    // 低余额警告
    if (data.balance > 0 && data.balance < 10) {
      const now = Date.now()
      if (now - lastLowBalanceWarn > 5 * 60 * 1000) {
        lastLowBalanceWarn = now
        ElMessage.warning('余额不足10元，请及时充值')
      }
    }
    // 余额=0警告
    if (data.balance <= 0) {
      ElMessageBox.alert('余额已用尽，系统将在5分钟后强制下线！', '警告', { type: 'warning', confirmButtonText: '我知道了' }).catch(() => {})
    }
  }).catch(() => {})
}

function fetchUsage() {
  getCurrentUsage(token.value).then(res => {
    usage.value = res.data || null
    if (usage.value && usage.value.startTime) {
      startTimer()
    } else {
      clearTimer()
    }
  }).catch(() => {})
}

function fetchRecords() {
  getRecords(token.value).then(res => {
    records.value = res.data || []
  }).catch(() => {})
}

// ========== 计时器 ==========
function startTimer() {
  clearTimer()
  timerInterval = setInterval(() => {
    if (usage.value && usage.value.startTime) {
      const diff = Date.now() - new Date(usage.value.startTime).getTime()
      const h = Math.floor(diff / 3600000)
      const m = Math.floor((diff % 3600000) / 60000)
      const s = Math.floor((diff % 60000) / 1000)
      elapsedStr.value = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
    }
  }, 1000)
}

function clearTimer() {
  if (timerInterval) { clearInterval(timerInterval); timerInterval = null }
  elapsedStr.value = '00:00:00'
}

// 倒计时
function startCountdown() {
  if (countdownInterval) return
  countdownInterval = setInterval(() => {
    if (balanceZero.value && countdownSeconds.value > 0) {
      countdownSeconds.value--
      if (countdownSeconds.value <= 0) {
        clearInterval(countdownInterval)
        countdownInterval = null
        ElMessageBox.alert('系统已强制下线，请重新登录', '强制下线', { callback: () => { handleLogout() } })
      }
    }
  }, 1000)
}

// ========== 操作 ==========
function doRecharge() {
  recharging.value = true
  recharge(token.value, rechargeAmount.value, '1').then(() => {
    ElMessage.success('充值成功')
    showRecharge.value = false
    fetchMemberInfo()
  }).catch(() => {
    ElMessage.error('充值失败')
  }).finally(() => { recharging.value = false })
}

function doStartUsage() {
  if (!selectedMachine.value) { ElMessage.warning('请选择机位'); return }
  startingUsage.value = true
  startUsage(token.value, selectedMachine.value).then(res => {
    ElMessage.success('开台成功')
    showStartUsage.value = false
    fetchUsage()
    fetchMemberInfo()
  }).catch(err => {
    const msg = err?.response?.data?.msg || err?.data?.msg || '开台失败'
    ElMessage.error(msg)
  }).finally(() => { startingUsage.value = false })
}

const showPayDialog = ref(false)
const guestPayType = ref('0')
const checkoutAmount = ref(0)

function handleGuestCheckout() {
  if (!usage.value) { ElMessage.warning('无进行中的上机记录'); return }
  // 计算预估费用（简化：按当前时长算个大概）
  const diff = Date.now() - new Date(usage.value.startTime).getTime()
  const mins = Math.floor(diff / 60000)
  checkoutAmount.value = 0 // 前端不精确计算，后端会算
  guestPayType.value = '0'
  showPayDialog.value = true
}

function confirmGuestPay() {
  showPayDialog.value = false
  const info = JSON.parse(localStorage.getItem('temp-info') || '{}')
  axios.post(import.meta.env.VITE_APP_BASE_API + '/member/usage/tempEnd', {
    recordId: info.recordId
  }).then(r => {
    const data = r.data
    if (data.code === 200) {
      ElMessage.success('结账成功，应付 ¥' + ((data.data && data.data.totalAmount) || '0'))
    } else {
      ElMessage.error(data.msg || '结账失败')
    }
    localStorage.removeItem('temp-token')
    localStorage.removeItem('temp-info')
    clearTimer()
    setTimeout(() => router.push('/member'), 1500)
  }).catch(() => {
    ElMessage.error('结账失败，请联系店员')
  })
}

function handleCheckout() {
  if (!usage.value) {
    ElMessage.warning('当前无进行中的上机记录')
    return
  }
  ElMessageBox.confirm('确认结账下机？系统将自动结算费用。', '结账确认', {
    confirmButtonText: '确认下机',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    endUsage(token.value).then(res => {
      const data = res.data || res
      ElMessage.success('结账成功，消费 ¥' + (data.totalAmount || '0') + '，欢迎下次光临')
      localStorage.removeItem('member-token')
      localStorage.removeItem('member-info')
      clearTimer()
      if (countdownInterval) clearInterval(countdownInterval)
      setTimeout(() => router.push('/member'), 1500)
    }).catch(err => {
      const msg = err?.response?.data?.msg || err?.data?.msg || '结账失败'
      ElMessage.error(msg)
    })
  }).catch(() => {})
}

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN')
}

// ========== 生命周期 ==========
onMounted(() => {
  const isGuestMode = !!localStorage.getItem('temp-token')
  if (!token.value && !isGuestMode) { window.location.href = '/member'; return }
  if (!isGuestMode) {
    fetchMemberInfo()
    fetchRecords()
    startCountdown()
  }
  fetchUsage()
  fetchMachines()
  fetchProducts()
  // 10秒轮询余额
  setInterval(fetchMemberInfo, 10000)
  // 30秒刷新上机状态
  setInterval(fetchUsage, 30000)
  // 每15秒心跳保活
  setInterval(() => {
    if (usage.value) {
      heartbeat(token.value, usage.value.recordId).catch(() => {})
    }
  }, 15000)
  // 关页面前同步发送下机请求（兜底）
  window.addEventListener('beforeunload', () => {
    if (usage.value) {
      navigator.sendBeacon(
        import.meta.env.VITE_APP_BASE_API + '/member/usage/end',
        JSON.stringify({})
      )
    }
  })
})

onUnmounted(() => {
  clearTimer()
  clearInterval(countdownInterval)
})
</script>

<style scoped>
.member-app {
  min-height: 100vh;
  background: #f4f6f9;
  font-family: 'Helvetica Neue', sans-serif;
}
.top-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 32px; height: 56px;
  background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.top-left h2 { margin: 0; font-size: 18px; color: #1a2332; }
.top-right { display: flex; align-items: center; gap: 12px; }
.member-info { color: #5a6a7a; font-size: 14px; }

.countdown-bar {
  background: #e53e3e; color: #fff; text-align: center;
  padding: 10px; font-size: 15px; font-weight: 600;
  display: flex; align-items: center; justify-content: center; gap: 8px;
}

.main-content { display: flex; gap: 20px; padding: 24px 32px; }
.card {
  background: #fff; border-radius: 12px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06); flex: 1;
  position: relative; overflow: hidden;
}
.card::before { content: ''; position: absolute; top: 0; left: 0; width: 4px; height: 40px; background: #2d5a4b; border-radius: 0 4px 4px 0; }
.card-label { font-size: 13px; color: #8a9aa8; margin-bottom: 8px; }
.card-value { font-size: 32px; font-weight: 700; color: #1a2332; }
.card-value.timer { font-family: 'Courier New', monospace; letter-spacing: 2px; color: #2d5a4b; }
.card-sub { font-size: 13px; color: #a0aec0; margin-top: 4px; }

.balance-card.danger .card-value { color: #e53e3e; animation: blink 1s infinite; }
@keyframes blink { 50% { opacity: 0.4; } }

.pricing-section { padding: 0 32px 24px; }
.pricing-card .card-label { margin-bottom: 12px; }
.pricing-tip { text-align: right; margin-top: 8px; font-size: 12px; color: #8a9aa8; }

.shop-section { padding: 0 32px 24px; }
.product-card {
  background: #fff; border-radius: 8px; border: 1px solid #ebeef5; padding: 16px;
  text-align: center; transition: box-shadow 0.2s; position: relative; height: 100%;
  display: flex; flex-direction: column; align-items: center;
}
.product-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.product-card.soldout { opacity: 0.6; }
.product-badge { width: 100%; }
.product-img {
  width: 80px; height: 60px; background: #f5f7fa; border-radius: 6px;
  display: flex; align-items: center; justify-content: center; margin: 0 auto 8px;
}
.product-name { font-weight: 700; font-size: 14px; color: #1a2332; margin-bottom: 4px; }
.product-price { font-size: 16px; font-weight: 700; color: #2d5a4b; margin-bottom: 6px; }
.product-category { margin-bottom: 8px; }
.stock-text { font-size: 11px; color: #e53e3e; }
.product-actions { margin-top: auto; }
.qty-control { display: flex; align-items: center; gap: 6px; }
.qty-num { font-size: 16px; font-weight: 700; min-width: 24px; text-align: center; }

.cart-bar {
  background: #f8fafb; border-radius: 12px; padding: 16px 20px;
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 20px; border: 1px solid #ebeef5;
}
.cart-title { font-weight: 700; margin-right: 12px; }
.cart-item-tag {
  background: #e8ecf0; padding: 2px 8px; border-radius: 4px; font-size: 12px; margin-right: 6px;
}
.cart-checkout { display: flex; align-items: center; gap: 16px; white-space: nowrap; }
.cart-total { font-size: 16px; }
</style>
