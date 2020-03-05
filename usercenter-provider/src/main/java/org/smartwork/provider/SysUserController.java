package org.smartwork.provider;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.constant.DataColumnConstant;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.enums.AdminFlagEnum;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.enums.UserStausEnum;
import org.forbes.comm.exception.ForbesException;
import org.forbes.comm.model.BasePageDto;
import org.forbes.comm.model.SysUserDto;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.UserVo;
import org.smartwork.biz.ISysPermissionService;
import org.smartwork.biz.ISysRoleService;
import org.smartwork.biz.ISysUserRoleService;
import org.smartwork.biz.ISysUserService;
import org.smartwork.dal.entity.SysPermission;
import org.smartwork.dal.entity.SysRole;
import org.smartwork.dal.entity.SysUser;
import org.smartwork.dal.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Api(tags = {"用户管理"})
@Slf4j
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysPermissionService sysPermissionService;
    @Autowired
    private ISysRoleService sysRoleService;


    /***
     * getAllUser方法慨述:通过id获取用户
     * @创建人 niehy(Frunk)
     * @创建时间 2019年12月26日 上午10:24:55
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/get-user-by-id/{id}", method = RequestMethod.GET)
    @ApiOperation("通过id获取用户")
    public Result<SysUser> getAllUser(@PathVariable String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        result.setResult(sysUser);
        return result;
    }

    /***
     * getAllUser方法慨述:获取所有用户
     * @创建人 niehy(Frunk)
     * @创建时间 2019年12月26日 上午10:24:55
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/get-all-user", method = RequestMethod.GET)
    @ApiOperation("获取所有用户")
    public Result<List<SysUser>> getAllUser() {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<SysUser> sysUsers = sysUserService.list();
        result.setResult(sysUsers);
        return result;
    }


    /***
     * receUserStaus方法慨述:获取用户状态
     * @return Result<List < Map < String, String>>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午10:24:55
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/user-status", method = RequestMethod.GET)
    @ApiOperation("获取用户状态")
    public Result<List<Map<String, String>>> receUserStaus() {
        Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>();
        result.setResult(UserStausEnum.receUserStaus());
        return result;
    }


    /***
     * receAdminFlags方法慨述:获取管理员标识
     * @return Result<List < Map < String, String>>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午10:25:49
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/admin-flags", method = RequestMethod.GET)
    @ApiOperation("获取管理员标识")
    public Result<List<Map<String, String>>> receAdminFlags() {
        Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>();
        result.setResult(AdminFlagEnum.receEnums());
        return result;
    }


    /***
     * getUserByName方法慨述:通过用户名查询用户信息
     * @param username
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午10:55:29
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("通过用户名查询用户信息")
    @ApiImplicitParam(value = "username", name = "用户名/电话/邮箱")
    @RequestMapping(value = "/user-by-name", method = RequestMethod.GET)
    public Result<SysUser> getUserByName(@RequestParam(name = "username", required = true) String username) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getUserByName(username);
        result.setResult(sysUser);
        return result;
    }


    /**
     * @ 作者：lzw
     * @ 参数：roleDto
     * @ 返回值：RoleVo
     * @ 时间：2019/11/20
     * @ Description：根据用户id查询对应的角色
     */
    @RequestMapping(value = "/get-role-list/{userId}", method = RequestMethod.GET)
    @ApiOperation("查询用户对应角色")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "userId", value = "用户id")
    )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.ROLE_EMPTY_MSG),
            @ApiResponse(code = 200, message = Result.ROLE_MSG)
    })
    public Result<List<SysRole>> selectRoleByUserId(@PathVariable Long userId) {
        log.debug("传入的参数为" + userId);
        Result<List<SysRole>> result = new Result<List<SysRole>>();
        List<SysUserRole> sysUserRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().eq(DataColumnConstant.USER_ID, userId));
        List<Long> roleIds = sysUserRoles.stream().map(sysUserRole -> sysUserRole.getRoleId()).collect(Collectors.toList());
        List<SysRole> sysRoles = sysRoleService.list(new QueryWrapper<SysRole>().in(DataColumnConstant.ID, roleIds));
        result.setResult(sysRoles);
        return result;
    }


    /***
     * getRole方法慨述:根据用户名查询用户角色
     * @param username
     * @return Result<List < String>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:03:48
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("根据用户名查询用户角色")
    @ApiImplicitParam(value = "username", name = "用户名/电话/邮箱")
    @RequestMapping(value = "/role-by-name", method = RequestMethod.GET)
    public Result<List<String>> getRole(@RequestParam(name = "username", required = true) String username) {
        Result<List<String>> result = new Result<List<String>>();
        result.setResult(sysUserService.getRole(username));
        return result;
    }


    /***
     * permissionByName方法慨述:
     * @param username
     * @return Result<List < SysPermission>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:14:04
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("根据用户名查询用户权限")
    @ApiImplicitParam(value = "username", name = "用户名/电话/邮箱")
    @RequestMapping(value = "/permission-by-name", method = RequestMethod.GET)
    public Result<List<SysPermission>> permissionByName(@RequestParam(name = "username", required = true) String username) {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        SysUser sysUser = sysUserService.getUserByName(username);
        if (ConvertUtils.isNotEmpty(sysUser)) {
            List<SysUserRole> sysUserRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().eq(DataColumnConstant.USER_ID, sysUser.getId()));
            List<Long> roleIds = sysUserRoles.stream().map(sysUserRole -> sysUserRole.getRoleId()).collect(Collectors.toList());
            if (ConvertUtils.isNotEmpty(roleIds)) {
                List<SysPermission> sysPermissions = sysPermissionService.searchPersByRoleIds(roleIds);
                result.setResult(sysPermissions);
            }
        }
        return result;
    }


    /***
     * selectUserList方法慨述:用户分页查询
     * @param basePageDto
     * @return Result<IPage < UserVo>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午9:18:10
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation("用户分页查询")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = Result.SELECT_LIST_USER_AND_ROLE_MSG),
            @ApiResponse(code = 500, message = Result.SELECT_LIST_USER_AND_ROLE_ERROR_MSG)
    })
    public Result<IPage<UserVo>> selectUserList(BasePageDto basePageDto, SysUserDto sysUserDto) {
        log.debug("==============" + JSON.toJSONString(basePageDto));
        Result<IPage<UserVo>> result = new Result<IPage<UserVo>>();
        IPage<UserVo> page = new Page<UserVo>(basePageDto.getPageNo(), basePageDto.getPageSize());
        IPage<UserVo> pageUsers = sysUserService.pageUsers(page, sysUserDto);
        result.setResult(pageUsers);
        return result;
    }


    /***
     * freezeOrThaw方法慨述:冻结/解冻用户
     * @param id
     * @param status
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午9:18:38
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/freeze-thaw/{id}", method = RequestMethod.PUT)
    @ApiOperation("冻结/解冻用户")
    @ApiImplicitParam(value = "status", name = "用户状态", required = false)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.COMM_ACTION_ERROR_MSG),
            @ApiResponse(code = 200, response = SysUser.class, message = Result.COMM_ACTION_MSG)
    })
    public Result<SysUser> freezeOrThaw(@PathVariable Long id,
                                        @RequestParam(value = "status", required = true) String status) {
        Result<SysUser> result = new Result<SysUser>();
        boolean isUserStatus = UserStausEnum.existsUserStausEnum(status);
        if (!isUserStatus) {
            result.setBizCode(BizResultEnum.USER_STATUS_NO_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.USER_STATUS_NO_EXISTS.getBizFormateMessage(), status));
            return result;
        }
        SysUser sysUser = sysUserService.getById(id);
        if (ConvertUtils.isEmpty(sysUser)) {
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        sysUser.setStatus(status);
        sysUserService.updateById(sysUser);
        return result;
    }


    /****
     * addUser方法慨述:添加用户
     * @param userDto
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午9:26:22
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("添加用户")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.COMM_ACTION_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.COMM_ACTION_MSG)
    })
    public Result<SysUserDto> addUser(@RequestBody @Validated(value = SaveValid.class) SysUserDto userDto) {
        log.debug("===========sysUserDto:" + JSON.toJSONString(userDto));
        Result<SysUserDto> result = new Result<SysUserDto>();
        String adminFlag = userDto.getAdminFlag();
        boolean isAdminFlag = AdminFlagEnum.existsCode(adminFlag);
        if (!isAdminFlag) {
            result.setBizCode(BizResultEnum.ADMIN_FLAG_NO_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.ADMIN_FLAG_NO_EXISTS.getBizFormateMessage(), adminFlag));
            return result;
        }
        String username = userDto.getUsername();
        int usernameCount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.USERNAME, username));
        if (usernameCount > 0) {
            result.setBizCode(BizResultEnum.USER_NAME_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.USER_NAME_EXISTS.getBizFormateMessage(), username));
            return result;
        }
        if (ConvertUtils.isNotEmpty(userDto.getEmail())) {
            String email = userDto.getEmail();
            int emailcount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.EMAIL, email));
            if (emailcount > 0) {
                result.setBizCode(BizResultEnum.USER_EMAIL_EXISTS.getBizCode());
                result.setMessage(String.format(BizResultEnum.USER_EMAIL_EXISTS.getBizFormateMessage(), email));
                return result;
            }
        }
        if (ConvertUtils.isNotEmpty(userDto.getPhone())) {
            String phone = userDto.getPhone();
            int emailcount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.PHONE, phone));
            if (emailcount > 0) {
                result.setBizCode(BizResultEnum.PHONE_EXISTS.getBizCode());
                result.setMessage(String.format(BizResultEnum.PHONE_EXISTS.getBizFormateMessage(), phone));
                return result;
            }
        }
        sysUserService.addUser(userDto);
        result.setResult(userDto);
        return result;
    }


    /***
     * updateUser方法慨述:编辑用户
     * @param userDto
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午9:44:33
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @ApiOperation("编辑用户")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.COMM_ACTION_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.COMM_ACTION_MSG)
    })
    public Result<SysUser> updateUser(@RequestBody @Validated(value = UpdateValid.class) SysUserDto userDto) {
        log.debug("==================用户请求参数：" + JSON.toJSONString(userDto));
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(userDto.getId());
        if (ConvertUtils.isEmpty(sysUser)) {
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        String adminFlag = userDto.getAdminFlag();
        boolean isAdminFlag = AdminFlagEnum.existsCode(adminFlag);
        if (!isAdminFlag) {
            result.setBizCode(BizResultEnum.ADMIN_FLAG_NO_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.ADMIN_FLAG_NO_EXISTS.getBizFormateMessage(), adminFlag));
            return result;
        }
        String username = userDto.getUsername();
        if (!username.equalsIgnoreCase(sysUser.getUsername())) {
            int usernameCount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.USERNAME, username));
            if (usernameCount > 0) {
                result.setBizCode(BizResultEnum.USER_NAME_EXISTS.getBizCode());
                result.setMessage(String.format(BizResultEnum.USER_NAME_EXISTS.getBizFormateMessage(), username));
                return result;
            }
        }
        if (ConvertUtils.isNotEmpty(userDto.getEmail())
                && !userDto.getEmail().equalsIgnoreCase(sysUser.getEmail())) {
            String email = userDto.getEmail();
            int emailcount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.EMAIL, email));
            if (emailcount > 0) {
                result.setBizCode(BizResultEnum.USER_EMAIL_EXISTS.getBizCode());
                result.setMessage(String.format(BizResultEnum.USER_EMAIL_EXISTS.getBizFormateMessage(), email));
                return result;
            }
        }
        if (ConvertUtils.isNotEmpty(userDto.getPhone())
                && !userDto.getEmail().equalsIgnoreCase(userDto.getPhone())) {
            String phone = userDto.getPhone();
            int emailcount = sysUserService.count(new QueryWrapper<SysUser>().eq(DataColumnConstant.PHONE, phone));
            if (emailcount > 0) {
                result.setBizCode(BizResultEnum.PHONE_EXISTS.getBizCode());
                result.setMessage(String.format(BizResultEnum.PHONE_EXISTS.getBizFormateMessage(), phone));
                return result;
            }
        }
        sysUserService.editUserWithRole(userDto);
        return result;
    }

    /***
     * checkCode方法慨述:检查用户名/电话/邮箱是否存在
     * @param checkCode
     * @return Result<Boolean>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午10:07:47
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    @ApiOperation("检查用户名/电话/邮箱是否存在")
    @ApiImplicitParam(value = "checkCode", name = "用户名/电话/邮箱", required = true)
    public Result<Boolean> checkCode(@RequestParam(value = "checkCode", required = true) String checkCode) {
        log.debug("====================checkCode===========" + checkCode);
        Result<Boolean> result = new Result<>();
        int existsCount = sysUserService.count(new QueryWrapper<SysUser>()
                .eq(DataColumnConstant.USERNAME, checkCode)
                .or().eq(DataColumnConstant.EMAIL, checkCode)
                .or().eq(DataColumnConstant.PHONE, checkCode));
        if (existsCount > 0) {
            result.setBizCode(BizResultEnum.USER_NAME_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.USER_NAME_EXISTS.getBizFormateMessage(), checkCode));
            return result;
        }
        result.setSuccess(true);
        return result;
    }


    /***
     * delete方法慨述:删除用户
     * @param id
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午10:13:01
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("删除用户")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "用户ID", required = true)
    })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysUser> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (ConvertUtils.isEmpty(sysUser)) {
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        if (AdminFlagEnum.SUPER_ADMIN.getCode()
                .equalsIgnoreCase(sysUser.getAdminFlag())) {
            result.setBizCode(BizResultEnum.ADMIN_FLAG_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.ADMIN_FLAG_EXISTS.getBizFormateMessage(), sysUser.getUsername()));
            return result;
        }
        sysUserService.removeById(id);
        return result;
    }

    /***批量删除用户
     * deleteBatch方法慨述:
     * @param ids
     * @return Result<SysUser>
     * @创建人 huanghy
     * @创建时间 2019年6月3日 下午5:58:16
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("批量删除用户")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "用户IDs", required = true)
    })
    @RequestMapping(value = "/delete-batch", method = RequestMethod.DELETE)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        // 定义SysUserDepart实体类的数据库查询对象
        Result<Boolean> result = new Result<Boolean>();
        try {
            sysUserService.removeByIds(Arrays.asList(ids.split(CommonConstant.SEPARATOR)));
        } catch (ForbesException e) {
            result.setBizCode(e.getErrorCode());
            result.setMessage(e.getErrorMsg());
        }
        return result;
    }

}
