import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useLockStore from '@/store/modules/lock'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import { getMemberInfo } from '@/api/member/index'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/member']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

// 会员端路径判断
const isMemberPath = (path) => {
  return path === '/member' || path === '/member-dashboard' || path.startsWith('/member-')
}

router.beforeEach(async (to, from) => {
  NProgress.start()

  // === 会员端路由守卫 ===
  if (isMemberPath(to.path)) {
    // 如果已登录后台管理，不允许访问会员页面
    if (getToken()) {
      NProgress.done()
      return { path: '/' }
    }
    const memberToken = localStorage.getItem('member-token')
    const tempToken = localStorage.getItem('temp-token')
    // 临时卡也放行
    if (tempToken && to.path === '/member-dashboard') {
      NProgress.done()
      return true
    }
    if (to.path === '/member') {
      NProgress.done()
      return true
    }
    if (!memberToken) {
      NProgress.done()
      return { path: '/member' }
    }
    try {
      await getMemberInfo(memberToken)
      NProgress.done()
      return true
    } catch (err) {
      localStorage.removeItem('member-token')
      localStorage.removeItem('member-info')
      NProgress.done()
      return { path: '/member' }
    }
  }

  // === 后台管理路由守卫 ===
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    const isLock = useLockStore().isLock
    if (to.path === '/login') {
      NProgress.done()
      return { path: '/' }
    }
    if (isWhiteList(to.path)) {
      return true
    }
    if (isLock && to.path !== '/lock') {
      NProgress.done()
      return { path: '/lock' }
    }
    if (!isLock && to.path === '/lock') {
      NProgress.done()
      return { path: '/' }
    }
    if (useUserStore().roles.length === 0) {
      isRelogin.show = true
      try {
        await useUserStore().getInfo()
        isRelogin.show = false
        const accessRoutes = await usePermissionStore().generateRoutes()
        accessRoutes.forEach(route => {
          if (!isHttp(route.path)) {
            router.addRoute(route)
          }
        })
        return { ...to, replace: true }
      } catch (err) {
        await useUserStore().logOut()
        ElMessage.error(err)
        return { path: '/' }
      }
    }
    return true
  } else {
    if (isWhiteList(to.path)) {
      return true
    }
    NProgress.done()
    return `/login?redirect=${to.fullPath}`
  }
})

router.afterEach(() => {
  NProgress.done()
})
