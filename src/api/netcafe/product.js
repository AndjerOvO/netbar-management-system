import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

// 查询商品列表
export function listProduct(query) {
  return request({
    url: '/netcafe/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getProduct(id) {
  return request({
    url: '/netcafe/product/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/netcafe/product',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/netcafe/product',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delProduct(ids) {
  return request({
    url: '/netcafe/product/' + ids,
    method: 'delete'
  })
}
