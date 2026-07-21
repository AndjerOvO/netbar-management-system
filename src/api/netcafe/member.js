import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi"

// 查询会员列表
export function listMember(query) {
  return request({
    url: '/netcafe/member/list',
    method: 'get',
    params: query
  })
}

// 查询会员详细
export function getMember(id) {
  return request({
    url: '/netcafe/member/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 新增会员
export function addMember(data) {
  return request({
    url: '/netcafe/member',
    method: 'post',
    data: data
  })
}

// 修改会员
export function updateMember(data) {
  return request({
    url: '/netcafe/member',
    method: 'put',
    data: data
  })
}

// 删除会员
export function delMember(ids) {
  return request({
    url: '/netcafe/member/' + ids,
    method: 'delete'
  })
}

// 重置会员密码（恢复为身份证后六位）
export function resetMemberPwd(memberId) {
  return request({
    url: '/netcafe/member/resetPwd/' + memberId,
    method: 'put'
  })
}
