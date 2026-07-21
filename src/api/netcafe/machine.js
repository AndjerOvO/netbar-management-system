import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

// 查询机器列表
export function listMachine(query) {
  return request({
    url: '/netcafe/machine/list',
    method: 'get',
    params: query
  })
}

// 查询机器详细
export function getMachine(id) {
  return request({
    url: '/netcafe/machine/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增机器
export function addMachine(data) {
  return request({
    url: '/netcafe/machine',
    method: 'post',
    data: data
  })
}

// 修改机器
export function updateMachine(data) {
  return request({
    url: '/netcafe/machine',
    method: 'put',
    data: data
  })
}

// 删除机器
export function delMachine(ids) {
  return request({
    url: '/netcafe/machine/' + ids,
    method: 'delete'
  })
}
