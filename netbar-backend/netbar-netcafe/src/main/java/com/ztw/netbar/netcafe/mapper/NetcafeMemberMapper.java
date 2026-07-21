package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeMember;

/**
 * 会员管理 数据层
 *
 * @author ruoyi
 */
public interface NetcafeMemberMapper
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
     * 逻辑删除会员
     *
     * @param id 会员ID
     * @return 结果
     */
    public int deleteNetcafeMemberById(Long id);

    /**
     * 批量逻辑删除会员
     *
     * @param ids 需要删除的会员ID
     * @return 结果
     */
    public int deleteNetcafeMemberByIds(Long[] ids);

    /**
     * 会员余额扣减（消费时使用，同时累加消费总额）
     *
     * @param id 会员ID
     * @param amount 扣减金额
     * @return 结果
     */
    public int deductMemberBalance(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("amount") java.math.BigDecimal amount);

    /**
     * 会员余额充值
     *
     * @param id 会员ID
     * @param amount 充值金额
     * @return 结果
     */
    public int rechargeMemberBalance(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("amount") java.math.BigDecimal amount);
}
