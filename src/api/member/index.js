import request from '@/utils/request'
import axios from 'axios'

const BASE = import.meta.env.VITE_APP_BASE_API

// 会员端请求（不经过 admin token 拦截器）
const memberRequest = axios.create({
  baseURL: BASE,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 响应拦截：非200 code视为错误
memberRequest.interceptors.response.use(
  res => {
    if (res.data.code !== 200) {
      return Promise.reject({ data: res.data })
    }
    return res
  },
  err => Promise.reject(err)
)

// 会员登录
export function memberLogin(data) {
  return memberRequest.post('/member/login', data).then(r => r.data)
}

// 获取空闲机位
export function getMachines() {
  return memberRequest.get('/member/machines').then(r => r.data)
}

// 获取会员信息
export function getMemberInfo(token) {
  return memberRequest.get('/member/info', {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 上机开台
export function startUsage(token, machineId) {
  return memberRequest.post('/member/usage/start', { machineId }, {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 获取当前上机记录
export function getCurrentUsage(token) {
  return memberRequest.get('/member/usage/current', {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 会员充值
export function recharge(token, amount, payType) {
  return memberRequest.post('/member/recharge', { amount, payType }, {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 下机结算
export function endUsage(token) {
  return memberRequest.post('/member/usage/end', {}, {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 商品列表
export function getProducts() {
  return memberRequest.get('/member/products').then(r => r.data)
}

// 心跳保活
export function heartbeat(token, recordId) {
  return memberRequest.post('/member/heartbeat', { recordId }, {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 购物车结算
export function createSaleOrder(token, items, payType) {
  return memberRequest.post('/member/sale/create', { items, payType }, {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}

// 上机记录列表
export function getRecords(token) {
  return memberRequest.get('/member/records', {
    headers: { 'Member-Token': token }
  }).then(r => r.data)
}
