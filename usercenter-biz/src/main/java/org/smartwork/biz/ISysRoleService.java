package org.smartwork.biz;

import org.forbes.comm.model.RolePermissionDto;
import org.smartwork.dal.entity.SysRole;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysRoleService  extends IService<SysRole> {
    
  
	
	
	/***
	 * grantRole方法慨述:角色授权
	 * @param roleId
	 * @param rolePermissionDtos void
	 * @创建人 huanghy
	 * @创建时间 2019年12月10日 下午1:38:36
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
    void grantRole(Long roleId, List<RolePermissionDto> rolePermissionDtos);

}