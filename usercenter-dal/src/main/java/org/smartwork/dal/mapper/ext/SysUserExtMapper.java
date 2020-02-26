package org.smartwork.dal.mapper.ext;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.forbes.comm.model.SysUserDto;
import org.forbes.comm.vo.UserVo;
import org.smartwork.dal.entity.SysUser;
import com.baomidou.mybatisplus.core.metadata.IPage;

/***
 * SysUserExtMapper概要说明：扩展类
 * @author Huanghy
 */
public interface SysUserExtMapper  {

	
	/***
	 * getUserByName方法慨述:根据用户名查询用户
	 * @param username
	 * @return SysUser
	 * @创建人 huanghy
	 * @创建时间 2019年11月15日 下午2:10:16
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	SysUser  getUserByName(@Param("username")String username);

	
	/***
	 * selectRole方法慨述:根据用户ID
	 * @param userId
	 * @return List<String>
	 * @创建人 huanghy
	 * @创建时间 2019年12月10日 上午11:06:26
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	List<String>  selectRole(@Param("userId")Long userId);
	
	/***
	 * pageUsers方法慨述:分页查询用户信息
	 * @param page
	 * @param sysUser
	 * @return IPage<UserVo>
	 * @创建人 huanghy
	 * @创建时间 2019年12月7日 下午4:21:50
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	IPage<UserVo> pageUsers(IPage<UserVo> page, @Param("sysUser") SysUserDto sysUser);
}
