package com.ztw.netbar.netcafe.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.ztw.netbar.common.exception.ServiceException;
import com.ztw.netbar.common.utils.StringUtils;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.mapper.NetcafeMemberMapper;
import com.ztw.netbar.netcafe.service.INetcafeMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员管理 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeMemberServiceImpl implements INetcafeMemberService
{
    private static final Logger log = LoggerFactory.getLogger(NetcafeMemberServiceImpl.class);

    @Autowired
    private NetcafeMemberMapper memberMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 根据会员ID查询会员信息
     *
     * @param id 会员ID
     * @return 会员信息
     */
    @Override
    public NetcafeMember selectNetcafeMemberById(Long id)
    {
        return memberMapper.selectNetcafeMemberById(id);
    }

    /**
     * 根据会员编号查询会员信息
     *
     * @param memberNo 会员编号
     * @return 会员信息
     */
    @Override
    public NetcafeMember selectNetcafeMemberByMemberNo(String memberNo)
    {
        return memberMapper.selectNetcafeMemberByMemberNo(memberNo);
    }

    /**
     * 查询会员列表
     *
     * @param member 会员信息
     * @return 会员集合
     */
    @Override
    public List<NetcafeMember> selectNetcafeMemberList(NetcafeMember member)
    {
        return memberMapper.selectNetcafeMemberList(member);
    }

    /**
     * 新增会员
     *
     * @param member 会员信息
     * @return 结果
     */
    @Override
    public int insertNetcafeMember(NetcafeMember member)
    {
        // 身份证号唯一性校验
        if (StringUtils.isNotEmpty(member.getIdCard()))
        {
            NetcafeMember exist = memberMapper.selectNetcafeMemberByIdCard(member.getIdCard());
            if (exist != null)
            {
                throw new ServiceException("该身份证号已登记，请勿重复添加");
            }
            // 密码默认为身份证后六位，BCrypt加密
            String idCard = member.getIdCard();
            if (idCard.length() >= 6)
            {
                String rawPwd = idCard.substring(idCard.length() - 6);
                member.setPassword(passwordEncoder.encode(rawPwd));
            }
        }
        else
        {
            // 无身份证号时，手动填的密码直接加密
            if (StringUtils.isNotEmpty(member.getPassword()))
            {
                member.setPassword(passwordEncoder.encode(member.getPassword()));
            }
        }
        return memberMapper.insertNetcafeMember(member);
    }

    /**
     * 修改会员
     *
     * @param member 会员信息
     * @return 结果
     */
    @Override
    public int updateNetcafeMember(NetcafeMember member)
    {
        return memberMapper.updateNetcafeMember(member);
    }

    /**
     * 删除会员对象
     *
     * @param id 会员ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeMemberById(Long id)
    {
        return memberMapper.deleteNetcafeMemberById(id);
    }

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeMemberByIds(Long[] ids)
    {
        return memberMapper.deleteNetcafeMemberByIds(ids);
    }

    @Override
    public NetcafeMember selectNetcafeMemberByIdCard(String idCard)
    {
        return memberMapper.selectNetcafeMemberByIdCard(idCard);
    }

    @Override
    public int resetPassword(Long memberId)
    {
        NetcafeMember member = memberMapper.selectNetcafeMemberById(memberId);
        if (member == null || StringUtils.isEmpty(member.getIdCard()))
        {
            throw new ServiceException("会员不存在或无身份证号");
        }
        String idCard = member.getIdCard();
        String rawPwd = idCard.substring(idCard.length() - 6);
        String encPwd = passwordEncoder.encode(rawPwd);
        member.setPassword(encPwd);
        log.warn("管理员重置会员[{}]密码", member.getName());
        return memberMapper.updateNetcafeMember(member);
    }
}
