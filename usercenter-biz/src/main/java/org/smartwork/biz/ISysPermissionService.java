package org.smartwork.biz;

import java.util.List;

import org.smartwork.dal.entity.SysPermission;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ISysPermissionService extends IService<SysPermission> {


    /****
     * searchPersByRoleIds方法慨述:根据角色IDS查询权限
     * @param roleIds
     * @return List<PermissionVo>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:34:09
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    List<SysPermission> searchPersByRoleIds(List<Long> roleIds);
}
