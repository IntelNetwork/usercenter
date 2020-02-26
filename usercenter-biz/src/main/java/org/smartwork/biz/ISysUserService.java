package org.smartwork.biz;

import java.util.List;

import org.forbes.comm.model.SysUserDto;
import org.forbes.comm.vo.UserVo;
import org.smartwork.dal.entity.SysUser;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ISysUserService extends IService<SysUser> {


	/***
	 * getUserByName方法慨述:根据用户名查询用户
	 * @param username
	 * @return SysUser
	 * @创建人 huanghy
	 * @创建时间 2019年11月15日 下午2:10:16
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	SysUser  getUserByName(String username);

	
	
	/****
	 * getRole方法慨述:
	 * @param username
	 * @return List<String>
	 * @创建人 huanghy
	 * @创建时间 2019年12月10日 上午11:05:38
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	List<String>  getRole(String username);
	
	/***
	 * pageUsers方法慨述:分页查询用户信息
	 * @param page
	 * @param sysUserDto
	 * @return IPage<UserVo>
	 * @创建人 huanghy
	 * @创建时间 2019年12月7日 下午4:20:25
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	IPage<UserVo> pageUsers(IPage<UserVo> page, SysUserDto sysUserDto);
	
	/***
	 * addUser方法慨述:增加用户
	 * @param sysUserDto void
	 * @创建人 huanghy
	 * @创建时间 2019年12月7日 下午5:01:22
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	void addUser(SysUserDto sysUserDto);

	/***
	 * editUserWithRole方法慨述:编辑用户
	 * @param sysUserDto void
	 * @创建人 huanghy
	 * @创建时间 2019年12月10日 上午10:10:27
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	public void editUserWithRole(SysUserDto sysUserDto);
}
