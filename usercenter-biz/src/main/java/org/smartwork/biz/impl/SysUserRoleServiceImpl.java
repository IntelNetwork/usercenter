package org.smartwork.biz.impl;

import org.smartwork.biz.ISysUserRoleService;
import org.smartwork.dal.entity.SysUserRole;
import org.smartwork.dal.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

}
