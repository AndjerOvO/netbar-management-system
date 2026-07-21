<template>
  <div class="member-login">
    <div class="login-wrapper">
      <!-- 登录卡片 -->
      <div class="login-card">
        <div class="card-header">
          <h1>网咖会员登录</h1>
          <p>输入卡号和密码，开启您的游戏之旅</p>
        </div>

        <el-form ref="loginRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="memberNo">
            <el-input v-model="form.memberNo" placeholder="请输入卡号" clearable>
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" show-password @keyup.enter="handleLogin">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="machineId">
            <el-select v-model="form.machineId" placeholder="请选择机位" style="width: 100%">
              <el-option v-for="m in machines" :key="m.id" :label="m.machineNo + ' (' + m.type + ' ' + m.pricePerHour + '元/时)'" :value="m.id" />
            </el-select>
          </el-form-item>

          <el-button type="primary" :loading="loading" style="width: 100%; height: 44px" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>

          <p class="forgot-hint">忘记密码？请联系店员重置</p>
        </el-form>
      </div>
    </div>

    <div class="login-footer">网咖综合业务管理平台</div>
  </div>
</template>

<script setup>
import { getMachines, memberLogin, startUsage } from '@/api/member/index'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginRef = ref(null)


const form = reactive({
  memberNo: '',
  password: '',
  machineId: null
})

const rules = {
  memberNo: [{ required: true, message: '请输入卡号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  machineId: [{ required: true, message: '请选择机位', trigger: 'change' }]
}

const machines = ref([])
const loading = ref(false)

onMounted(() => {
  getMachines().then(res => {
    machines.value = res.data || []
  }).catch(() => {})
})

function handleLogin() {
  const formEl = loginRef.value
  if (!formEl) {
    ElMessage.warning('页面加载中，请稍候')
    return
  }
  formEl.validate(valid => {
    if (!valid) return
    loading.value = true
    memberLogin({ memberNo: form.memberNo, password: form.password }).then(res => {
      const data = res.data || res
      localStorage.setItem('member-token', data.token)
      localStorage.setItem('member-info', JSON.stringify(data))
      ElMessage.success('登录成功')

      // 选了机位 → 自动开台
      if (form.machineId) {
        startUsage(data.token, form.machineId).then(() => {
          ElMessage.success('已开台上机，计时开始')
          setTimeout(() => router.push('/member-dashboard'), 500)
        }).catch(err => {
          const msg = err?.response?.data?.msg || err?.data?.msg || '开台失败'
          ElMessage.warning(msg)
          setTimeout(() => router.push('/member-dashboard'), 300)
        })
      } else {
        setTimeout(() => router.push('/member-dashboard'), 300)
      }
    }).catch(err => {
      loading.value = false
      const msg = err?.response?.data?.msg || err?.data?.msg || '登录失败'
      ElMessage.error(msg)
    })
  })
}
</script>

<style scoped>
.member-login {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url("../../image/Login-bg.jpg");
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.login-wrapper {
  position: relative;
  z-index: 1;
}
.login-card {
  width: 400px;
  background: #ffffff;
  border-radius: 16px;
  padding: 40px 36px 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
.card-header {
  text-align: center;
  margin-bottom: 28px;
}
.card-header h1 {
  font-size: 22px;
  color: #1a2332;
  margin: 0 0 8px 0;
  font-weight: 700;
}
.card-header p {
  font-size: 13px;
  color: #8a9aa8;
  margin: 0;
}
.forgot-hint {
  text-align: center; font-size: 12px; color: #a0aec0; margin-top: 12px;
}
.login-footer {
  margin-top: 24px; font-size: 12px; color: #8a9aa8; z-index: 1;
}
</style>
