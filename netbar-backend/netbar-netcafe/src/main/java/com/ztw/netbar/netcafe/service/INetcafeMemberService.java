package com.ztw.netbar.netcafe.service;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeMember;

/**
 * 会员管理 服务层
 *
 * @author ruoyi
 */
public interface INetcafeMemberService
{
    /**
     * 根据会员ID查询会员信息
     *
     * @param id 会员ID
     * @return 会员信息
     */
    public NetcafeMember selectNetcafeMemberById(Long id);

    /**
     * 根据会员编号查询会员信息
     *
     * @param memberNo 会员编号
     * @return 会员信息
     */
    public NetcafeMember selectNetcafeMemberByMemberNo(String memberNo);

    /**
     * 根据身份证号查询会员信息
     *
     * @param idCard 身份证号
     * @return 会员信息
     */
    public NetcafeMember selectNetcafeMemberByIdCard(String idCard);

    /**
     * 重置会员密码为身份证后六位
     *
     * @param memberId 会员ID
     * @return 结果
     */
    public int resetPassword(Long memberId);

    /**
     * 查询会员列表
     *
     * @param member 会员信息
     * @return 会员集合
     */
    public List<NetcafeMember> selectNetcafeMemberList(NetcafeMember member);

    /**
     * 新增会员
     *
     * @param member 会员信息
     * @return 结果
     */
    public int insertNetcafeMember(NetcafeMember member);

    /**
     * 修改会员
     *
     * @param member 会员信息
     * @return 结果
     */
    public int updateNetcafeMember(NetcafeMember member);

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     * @return 结果
     */
    public int deleteNetcafeMemberById(Long id);

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     * @return 结果
     */
    public int deleteNetcafeMemberByIds(Long[] ids);
}
