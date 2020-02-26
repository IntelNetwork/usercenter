package org.smartwork.provider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.smartwork.biz.ISysRolePermissionService;
import org.smartwork.biz.ISysRoleService;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.constant.DataColumnConstant;
import org.forbes.comm.constant.PermsCommonConstant;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.enums.RoleCodeEnum;
import org.forbes.comm.exception.ForbesException;
import org.forbes.comm.model.BasePageDto;
import org.forbes.comm.model.RoleDto;
import org.forbes.comm.model.RolePageDto;
import org.forbes.comm.model.RolePermissionDto;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.smartwork.dal.entity.SysRole;
import org.smartwork.dal.entity.SysRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName
 * @Description 角色控制
 * @Author lzw
 * @Date 2019/11/20 11:42
 * @Version 1.0
 **/
@RestController
@RequestMapping("/role")
@Api(tags={"用户角色"})
@Slf4j
public class RoleController {


    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    ISysRolePermissionService sysRolePermissionService;



    /***
     * selectAllRole方法慨述:查询所有角色
     * @return Result<List<SysRole>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:57:37
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ApiOperation("查询所有角色")
    @ApiResponses(value = {
            @ApiResponse(code=200,message =Result.ROLE_LIST_MSG),
            @ApiResponse(code = 500,message = Result.ROLE_LIST_ERROR_MSG)
    })
    public Result<List<SysRole>> selectAllRole(){
        Result<List<SysRole>> result=new Result<>();
        List<SysRole> sysRoles = sysRoleService.list();
        result.setResult(sysRoles);
        return result;
    }

    /***
     * addRole方法慨述:添加角色
     * @param sysRole
     * @return Result<SysRole>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:58:15
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("添加角色")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public  Result<SysRole> addRole(@RequestBody @Validated(value=SaveValid.class) SysRole sysRole){
        log.debug("传入的参数为"+ JSON.toJSONString(sysRole));
        Result<SysRole> result=new Result<SysRole>();
            String roleCode = sysRole.getRoleCode();
            int existsCount = sysRoleService.count(new QueryWrapper<SysRole>().eq(DataColumnConstant.ROLE_CODE, roleCode));
            if(existsCount > 0 ) {//存在此记录
                result.setBizCode(BizResultEnum.ROLE_CODE_EXIST.getBizCode());
                result.setMessage(String.format(BizResultEnum.ROLE_CODE_EXIST.getBizFormateMessage(), roleCode));
                return result;
            }
            sysRoleService.save(sysRole);
            result.setResult(sysRole);
            return result;
    }

    /***
     * updateRole方法慨述:编辑角色
     * @param sysRole
     * @return Result<SysRole>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午12:00:04
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("编辑角色")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/edit",method = RequestMethod.PUT)
    public  Result<SysRole> updateRole(@RequestBody @Validated(value=UpdateValid.class) SysRole sysRole){
        log.debug("传入的参数为"+JSON.toJSONString(sysRole));
        Result<SysRole> result=new Result<SysRole>();
        SysRole oldSysRole = sysRoleService.getById(sysRole.getId());
        if(ConvertUtils.isEmpty(oldSysRole)){
        	 result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
	   		 result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
	   		 return result;
        }
        String roleCode = sysRole.getRoleCode();
        //判断当前角色编码与输入的是否一致
        if (!roleCode.equalsIgnoreCase(oldSysRole.getRoleCode())) {
            //查询是否和其他角色编码一致
            int existsCount = sysRoleService.count(new QueryWrapper<SysRole>().eq(DataColumnConstant.ROLE_CODE, roleCode));
            if (existsCount > 0) {//存在此记录
                result.setBizCode(BizResultEnum.ROLE_CODE_EXIST.getBizCode());
                result.setMessage(String.format(BizResultEnum.ROLE_CODE_EXIST.getBizFormateMessage(), roleCode));
                return result;
            }
        }
        sysRoleService.updateById(sysRole);
        result.setResult(sysRole);
        return result;
    }

    /***
     * delete方法慨述:删除角色
     * @param id
     * @return Result<SysRole>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:34:01
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("删除角色")
   	@ApiImplicitParams(value = {
   		@ApiImplicitParam(name = "id",value = "用户ID",required = true)
   	})
   	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
   	public Result<SysRole> delete(@RequestParam(name="id",required=true) String id) {
   		Result<SysRole> result = new Result<SysRole>();
   		SysRole sysRole = sysRoleService.getById(id);
   		if(ConvertUtils.isEmpty(sysRole)){
      		 result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
   	   		 result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
   	   		 return result;
   		}
   		sysRoleService.removeById(id);
   		return result;
   	}

   	/***批量删除角色
   	 * deleteBatch方法慨述:
   	 * @param ids
   	 * @return Result<SysUser>
   	 * @创建人 huanghy
   	 * @创建时间 2019年6月3日 下午5:58:16
   	 * @修改人 (修改了该文件，请填上修改人的名字)
   	 * @修改日期 (请填上修改该文件时的日期)
   	 */
   	@ApiOperation("批量删除角色")
   	@ApiImplicitParams(value = {
   		@ApiImplicitParam(name = "ids",value = "用户IDs",required = true)
   	})
   	@RequestMapping(value = "/delete-batch", method = RequestMethod.DELETE)
   	public Result<Boolean> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
   		// 定义SysUserDepart实体类的数据库查询对象
   		Result<Boolean> result = new Result<Boolean>();
   		try {
   			sysRoleService.removeByIds(Arrays.asList(ids.split(CommonConstant.SEPARATOR)));
   		}catch(ForbesException e){
   			result.setBizCode(e.getErrorCode());
   			result.setMessage(e.getErrorMsg());
   		}
   		return result;
   	}




    /***
     * grantRole方法慨述:角色授权
     * @param roleDtos
     * @return Result<RolePermissionDto>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:35:36
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/grant-role/{roleId}",method = RequestMethod.POST)
    @ApiOperation("角色授权")
    @ApiResponses(value = {
            @ApiResponse(code=500,message= Result.UPDATE_ROLE_PERMISSION_NOT_ERROR_MSG),
            @ApiResponse(code=200,message = Result.UPDATE_PERMISSION_MSG)
    })
    public Result<Boolean> grantRole(@PathVariable Long roleId,
    	@RequestBody @Valid RoleDto roleDtos){
    	List<RolePermissionDto> permissionDtos = roleDtos.getPermissionDtos();
        log.debug("传入的参数为"+JSON.toJSONString(permissionDtos));
        Result<Boolean> result=new Result<Boolean>();
        try{
        	sysRoleService.grantRole(roleId,permissionDtos);
            result.setResult(true);
        }catch(ForbesException e){
        	result.setBizCode(e.getErrorCode());
        	result.setMessage(e.getErrorMsg());
        }
        return result;
    }
    
    
    
    /***
     * rolePermissionByRoleId方法慨述:根据角色ID获取授权
     * @param roleId
     * @return Result<List<SysRolePermission>>
     * @创建人 huanghy
     * @创建时间 2019年12月14日 下午2:21:11
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("根据角色ID获取授权")
    @ApiImplicitParam(value="roleId",name="角色ID")
    @RequestMapping(value="/role-permissions-role-id",method=RequestMethod.GET)
	public Result<List<SysRolePermission>> rolePermissionByRoleId(@RequestParam(name="roleId",required=true)String roleId){
    	Result<List<SysRolePermission>> result = new Result<List<SysRolePermission>>();
    	List<SysRolePermission> sysPermissions = sysRolePermissionService.list(new QueryWrapper<SysRolePermission>()
    			.eq(DataColumnConstant.ROLE_ID, roleId));
    	result.setResult(sysPermissions);
    	return result;
    }

    /***
     * selectRolePage方法慨述:分页查询角色
     * @param pageDto
     * @return Result<IPage<SysRole>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:43:51
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation("分页查询角色")
    @ApiImplicitParams(value={
            @ApiImplicitParam(dataTypeClass=RolePageDto.class)
    })
    @ApiResponses(value={
            @ApiResponse(code=500,message= Result.ROLE_LIST_ERROR_MSG),
            @ApiResponse(code=200,response=Result.class, message = Result.ROLE_LIST_MSG)
    })
    public Result<IPage<SysRole>> selectRolePage(BasePageDto pageDto,RolePageDto rolePageDto){
        log.debug("传入的参数为"+JSON.toJSONString(pageDto));
        Result<IPage<SysRole>> result = new Result<>();
        QueryWrapper<SysRole> qw = new QueryWrapper<SysRole>();
        if(ConvertUtils.isNotEmpty(rolePageDto)){
        	if(ConvertUtils.isNotEmpty(rolePageDto.getRoleName())){
                qw.like(PermsCommonConstant.ROLE_NAME,rolePageDto.getRoleName());
            }
            if(ConvertUtils.isNotEmpty(rolePageDto.getRoleCode())){
                qw.eq(PermsCommonConstant.ROLE_CODE,rolePageDto.getRoleCode());
            }
        }
        IPage<SysRole> page = new Page<SysRole>(pageDto.getPageNo(),pageDto.getPageSize());
        IPage<SysRole> s = sysRoleService.page(page,qw);
        result.setResult(s);
        log.info("返回值为:"+JSON.toJSONString(result.getResult()));
        return result;
    }

    
    /****
     * checkRoleCode方法慨述:校验角色编码唯一
     * @param roleCode
     * @return Result<Boolean>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:45:17
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("校验角色编码唯一")
    @ApiImplicitParam(name = "roleCode" ,value = "角色编码")
    @RequestMapping(value = "/check-role-code", method = RequestMethod.GET)
    public Result<Boolean> checkRoleCode(@RequestParam(value="roleCode",required=true)String roleCode) {
        Result<Boolean> result = new Result<Boolean>();
        int existsCount = sysRoleService.count(new QueryWrapper<SysRole>().eq(DataColumnConstant.ROLE_CODE, roleCode));
        if(existsCount > 0 ) {//存在此记录
            result.setBizCode(BizResultEnum.ROLE_CODE_EXIST.getBizCode());
            result.setMessage(String.format(BizResultEnum.ROLE_CODE_EXIST.getBizFormateMessage(), roleCode));
            result.setResult(false);
            return result;
        }
        return result;
    }
    
    
    /***
     * receRoleCodes方法慨述:获取角色编码
     * @return Result<List<Map<String,String>>>
     * @创建人 huanghy
     * @创建时间 2019年12月16日 上午9:54:42
     * @修改人 (修改了该文件，请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/role-codes", method = RequestMethod.GET)
    @ApiOperation("获取角色编码")
    public Result<List<Map<String,String>>> receRoleCodes(){
        Result<List<Map<String,String>>> result = new Result<List<Map<String,String>>>();
        result.setResult(RoleCodeEnum.receEnums());
        return result;
    }
}
