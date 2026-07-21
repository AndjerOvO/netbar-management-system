import request from '@/utils/request'

// ---------- 上机记录 ----------

// 查询上机记录列表
export function listUsageRecord(query) {
  return request({
    url: '/netcafe/usage/list',
    method: 'get',
    params: query
  })
}

// 上机开台
export function startUsage(data) {
  return request({
    url: '/netcafe/usage/start',
    method: 'post',
    data: data
  })
}

// 下机结算
export function endUsage(recordId) {
  return request({
    url: '/netcafe/usage/end/' + recordId,
    method: 'put'
  })
}

// ---------- 销售订单 ----------

// 查询销售订单列表
export function listSaleOrder(query) {
  return request({
    url: '/netcafe/sale/list',
    method: 'get',
    params: query
  })
}

// 查询销售订单详情
export function getSaleOrder(id) {
  return request({
    url: '/netcafe/sale/' + id,
    method: 'get'
  })
}

// 创建销售订单（零售出库）
export function createSaleOrder(data) {
  return request({
    url: '/netcafe/sale/create',
    method: 'post',
    data: data
  })
}

// ---------- 充值 ----------

// 会员充值
export function rechargeMember(data) {
  return request({
    url: '/netcafe/recharge',
    method: 'post',
    data: data
  })
}

// 查询充值记录列表
export function listRechargeRecord(query) {
  return request({
    url: '/netcafe/recharge/list',
    method: 'get',
    params: query
  })
}
