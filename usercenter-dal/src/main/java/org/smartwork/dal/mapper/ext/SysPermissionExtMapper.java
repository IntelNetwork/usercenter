package org.smartwork.dal.mapper.ext;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.smartwork.dal.entity.SysPermission;

/***
 * SysPermissionExtMapper：扩展类
 * @author niehy(Frunk)
 */
public interface SysPermissionExtMapper {

    
    
    /***查询权限信息
     * searchPersByRoleIds方法慨述:
     * @param roleIds
     * @return List<PermissionVo>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:35:33
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    List<SysPermission> searchPersByRoleIds(@Param("roleIds")List<Long> roleIds);
}
