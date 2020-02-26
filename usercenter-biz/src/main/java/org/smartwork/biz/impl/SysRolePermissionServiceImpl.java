package org.smartwork.biz.impl;

import org.smartwork.biz.ISysRolePermissionService;
import org.smartwork.dal.entity.SysRolePermission;
import org.smartwork.dal.mapper.SysRolePermissionMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class SysRolePermissionServiceImpl
        extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {
	
}
