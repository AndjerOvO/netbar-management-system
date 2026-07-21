import request from '@/utils/request'

// 获取首页概览数据
export function getOverview() {
  return request({
    url: '/netcafe/dashboard/overview',
    method: 'get'
  })
}
