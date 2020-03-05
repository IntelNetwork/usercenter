package org.smartwork.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.smartwork.biz.ISysPermissionService;
import org.smartwork.biz.ISysUserRoleService;
import org.smartwork.biz.ISysUserService;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.constant.DataColumnConstant;
import org.forbes.comm.constant.PermsCommonConstant;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.enums.PermissionTypeEnum;
import org.forbes.comm.enums.YesNoEnum;
import org.forbes.comm.exception.ForbesException;
import org.forbes.comm.model.BasePageDto;
import org.forbes.comm.model.SysPermissionPageDto;
import org.forbes.comm.model.TreeModel;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.JwtUtil;
import org.forbes.comm.vo.Result;
import org.smartwork.dal.entity.SysPermission;
import org.smartwork.dal.entity.SysUser;
import org.smartwork.dal.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

@RequestMapping("/permission")
@Api(tags = {"权限管理"})
@Slf4j
@RestController
public class SysPermissionController {
    @Autowired
    ISysPermissionService sysPermissionService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;


    /***
     * selectPage方法慨述:分页查询权限
     * @param pageDto
     * @return Result<IPage < SysPermission>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:48:45
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询权限")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.PERMISSIONS_NOT_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.PERMISSIONS_MSG)
    })
    public Result<IPage<SysPermission>> selectPage(BasePageDto pageDto,
                                                   SysPermissionPageDto permissionPageDto) {
        log.debug("传入参数为：" + JSON.toJSONString(permissionPageDto));
        Result<IPage<SysPermission>> result = new Result<>();
        QueryWrapper<SysPermission> qw = new QueryWrapper<SysPermission>();
        if (ConvertUtils.isNotEmpty(permissionPageDto)) {
            if (ConvertUtils.isNotEmpty(permissionPageDto.getType())) {
                qw.eq(PermsCommonConstant.PERMS, permissionPageDto.getType());
            }
            if (ConvertUtils.isNotEmpty(permissionPageDto.getName())) {
                qw.like(PermsCommonConstant.NAME, permissionPageDto.getName());
            }
        }
        IPage<SysPermission> page = new Page<SysPermission>(pageDto.getPageNo(), pageDto.getPageSize());
        IPage<SysPermission> sysPermissions = sysPermissionService.page(page, qw);
        result.setResult(sysPermissions);
        log.debug("返回值为:" + JSON.toJSONString(result.getResult()));
        return result;
    }


    /***
     * checkPerms方法慨述:校验权限编码是否唯一
     * @param perms
     * @return Result<Boolean>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午1:49:32
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("校验权限编码是否唯一")
    @ApiImplicitParam(name = "perms", value = "权限编码", required = true)
    @RequestMapping(value = "/check-perms", method = RequestMethod.GET)
    public Result<Boolean> checkPerms(@RequestParam(value = "perms", required = true) String perms) {
        log.debug("传入参数为：" + perms);
        Result<Boolean> result = new Result<Boolean>();
        int exitsCount = sysPermissionService.count(new QueryWrapper<SysPermission>().eq(PermsCommonConstant.PERMS, perms));
        //如果编码存在，则返回false
        if (exitsCount > 0) {
            result.setMessage(Result.EXISTS_PERMISSION_MSG);
            result.setResult(false);
            return result;
        }
        result.setMessage(Result.AVAILABLE_PERMISSION_MSG);
        return result;
    }


    /***
     * getPermissionById方法概述: 通过权限id查询权限内容
     * @param id 权限id
     * @return
     * @创建人 niehy(Frunk)
     * @创建时间 2019/12/7
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */

    @RequestMapping(value = "/select-permission-by-id/{id}", method = RequestMethod.GET)
    @ApiOperation("通过权限id查询权限内容")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "权限id")
    )
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.PERMISSION_BY_ID_NOT_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.PERMISSION_BY_ID_MSG)
    })
    public Result<SysPermission> getPermissionById(@PathVariable Long id) {
        log.debug("传入id为:" + id);
        Result<SysPermission> result = new Result<>();
        SysPermission permission = sysPermissionService.getById(id);
        result.setResult(permission);
        log.debug("返回值为:" + result.getResult());
        return result;
    }


    /***
     * recePermissionTypes方法概述:获取权限类型
     * @return
     * @创建人 niehy(Frunk)
     * @创建时间 2019/12/9
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/permission-types", method = RequestMethod.GET)
    @ApiOperation("获取权限类型")
    public Result<List<Map<String, String>>> recePermissionTypes() {
        Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>();
        result.setResult(PermissionTypeEnum.recePermissionTypes());
        return result;
    }


    /***
     * receYesNos方法概述:获取是否枚举值
     * @return
     * @创建人 niehy(Frunk)
     * @创建时间 2019/12/9
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/yes-nos", method = RequestMethod.GET)
    @ApiOperation("获取是否枚举值")
    public Result<List<Map<String, String>>> receYesNos() {
        Result<List<Map<String, String>>> result = new Result<List<Map<String, String>>>();
        result.setResult(YesNoEnum.receYesNos());
        return result;
    }


    /***
     * addPermission方法概述: 增加权限
     * @param sysPermission 添加的实体
     * @return
     * @创建人 niehy(Frunk)
     * @创建时间 2019/12/7
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("增加权限")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.ADD_PERMISSION_NOT_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.ADD_PERMISSION_MSG)
    })
    public Result<SysPermission> addPermission(@RequestBody @Validated(value = SaveValid.class) SysPermission sysPermission) {
        log.debug("传入sysPermission为:" + JSON.toJSONString(sysPermission));
        Result<SysPermission> result = new Result<SysPermission>();
        String perms = sysPermission.getPerms();
        int exitsCount = sysPermissionService.count(new QueryWrapper<SysPermission>().eq(PermsCommonConstant.PERMS, perms));
        if (exitsCount > 0) {
            result.setBizCode(BizResultEnum.PERMISSION_CODE_EXISTS.getBizCode());
            result.success(String.format(BizResultEnum.PERMISSION_CODE_EXISTS.getBizFormateMessage(), perms));
            return result;
        } else {
            String type = String.valueOf(sysPermission.getType());
            if (!PermissionTypeEnum.existsPermissionTypeEnum(type)) {
                result.setBizCode(BizResultEnum.PERMISSION_TYPE_NO_EXISTS.getBizCode());
                result.success(String.format(BizResultEnum.PERMISSION_TYPE_NO_EXISTS.getBizFormateMessage(), type));
                return result;
            }
            //是否隐藏,默认否
            sysPermission.setIsHidden(YesNoEnum.NO.getCode());
            //是否子集,默认否
            sysPermission.setIsLeaf(YesNoEnum.YES.getCode());
            //判父级权权限值并更改父级自己点值
            sysPermissionService.save(sysPermission);
        }
        result.setResult(sysPermission);
        return result;
    }


    /***
     * updatePermission方法概述: 修改权限内容
     * @param sysPermission 修改实体类
     * @return
     * @创建人 niehy(Frunk)
     * @创建时间 2019/12/7
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @ApiOperation("修改权限内容")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.UPDATE_PERMISSION_NOT_ERROR_MSG),
            @ApiResponse(code = 200, message = Result.UPDATE_PERMISSION_MSG)
    })
    public Result<SysPermission> updatePermission(@RequestBody @Validated(value = UpdateValid.class) SysPermission sysPermission) {
        log.debug("传入sysPermission为:" + JSON.toJSONString(sysPermission));
        Result<SysPermission> result = new Result<>();
        SysPermission permission = sysPermissionService.getById(sysPermission.getId());
        if (ConvertUtils.isEmpty(permission)) {
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        //判断当前权限编码与输入的是否一致
        if (!permission.getPerms().equalsIgnoreCase(sysPermission.getPerms())) {
            String perms = sysPermission.getPerms();
            //查询是否和其他权限编码一致
            int exitsCount = sysPermissionService.count(new QueryWrapper<SysPermission>().eq(PermsCommonConstant.PERMS, perms));
            //已存在相同的权限编码，不操作
            if (exitsCount > 0) {
                result.setBizCode(BizResultEnum.PERMISSION_CODE_EXISTS.getBizCode());
                result.success(String.format(BizResultEnum.PERMISSION_CODE_EXISTS.getBizFormateMessage(), perms));
                return result;
            }
        }
        String type = String.valueOf(sysPermission.getType());
        if (!PermissionTypeEnum.existsPermissionTypeEnum(type)) {
            result.setBizCode(BizResultEnum.PERMISSION_TYPE_NO_EXISTS.getBizCode());
            result.success(String.format(BizResultEnum.PERMISSION_TYPE_NO_EXISTS.getBizFormateMessage(), type));
            return result;
        }
        sysPermissionService.updateById(sysPermission);
        result.setResult(sysPermission);
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
    @ApiOperation("删除权限")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "用户ID", required = true)
    })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysPermission> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysPermission> result = new Result<SysPermission>();
        SysPermission sysPermission = sysPermissionService.getById(id);
        if (ConvertUtils.isEmpty(sysPermission)) {
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        int childCount = sysPermissionService.count(new QueryWrapper<SysPermission>().eq(PermsCommonConstant.PARENT_ID, id));
        if (childCount > 0) {
            result.setBizCode(BizResultEnum.PERMISSION_CHILD_EXISTS.getBizCode());
            result.setMessage(String.format(BizResultEnum.PERMISSION_CHILD_EXISTS.getBizFormateMessage(), sysPermission.getName()));
            return result;
        }
        sysPermissionService.removeById(id);
        return result;
    }

    /***批量删除权限
     * deleteBatch方法慨述:
     * @param ids
     * @return Result<Boolean>
     * @创建人 huanghy
     * @创建时间 2019年6月3日 下午5:58:16
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("批量删除权限")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "权限IDs", required = true)
    })
    @RequestMapping(value = "/delete-batch", method = RequestMethod.DELETE)
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Boolean> result = new Result<Boolean>();
        sysPermissionService.removeByIds(Arrays.asList(ids.split(CommonConstant.SEPARATOR)));
        return result;
    }

    /***
     * queryTreeList方法慨述:获取所有资源权限树形
     * @return Result<Map < String, Object>>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:56:15
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("获取所有资源权限树形")
    @RequestMapping(value = "/query-tree-list", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeList() {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        try {
            QueryWrapper<SysPermission> query = new QueryWrapper<SysPermission>();
            query.orderByAsc(DataColumnConstant.SORT_NO);
            List<SysPermission> list = sysPermissionService.list(query);
            List<Long> ids = list.stream().map(sysPermission -> sysPermission.getId()).collect(Collectors.toList());
            List<TreeModel> treeList = new ArrayList<TreeModel>();
            Map<Long, Boolean> idMaps = new HashMap<Long, Boolean>();
            receTreeModelList(treeList, list, null, idMaps);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put(CommonConstant.TREE_LIST_CODE, treeList); //全部树节点数据
            resMap.put(CommonConstant.IDS_CODE, ids);//全部树ids
            result.setResult(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.trace("获取所有权限异常", e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /***
     * receTreeModelList方法慨述:
     * @param treeList
     * @param metaList
     * @param temp void
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:54:31
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    private void receTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp, Map<Long, Boolean> idMaps) {
        try {
            metaList.forEach(permission -> {
                Long id = permission.getId();
                if (!idMaps.containsKey(id)) {
                    Long tempPid = permission.getParentId();
                    String icon = permission.getIcon();
                    String name = permission.getName();
                    String isLeaf = permission.getIsLeaf();
                    TreeModel tree = new TreeModel(id, icon, tempPid, name, isLeaf);
                    if (temp == null
                            && 0 == tempPid) {
                        idMaps.put(id, true);
                        treeList.add(tree);
                        if (!tree.getLeaf()) {
                            receTreeModelList(treeList, metaList, tree, idMaps);
                        }
                    } else if (temp != null
                            && 0 != tempPid
                            && tempPid.equals(Long.valueOf(temp.getKey()))) {
                        idMaps.put(id, true);
                        temp.getChildren().add(tree);
                        if (!tree.getLeaf()) {
                            receTreeModelList(treeList, metaList, tree, idMaps);
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new ForbesException(e.getMessage());
        }
    }


    /***
     * queryByUser方法慨述:查询用户拥有的菜单权限和按钮权限
     * @param req
     * @return Result<JSONArray>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午3:18:36
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @ApiOperation("查询用户拥有的菜单权限和按钮权限")
    @RequestMapping(value = "/query-by-user", method = RequestMethod.GET)
    public Result<JSONArray> queryByUser(HttpServletRequest req) {
        Result<JSONArray> result = new Result<>();
        try {
            String token = req.getHeader(CommonConstant.X_ACCESS_TOKEN);
            String username = JwtUtil.getUsername(token);
            SysUser sysUser = sysUserService.getUserByName(username);
            if (ConvertUtils.isNotEmpty(sysUser)) {
                List<SysUserRole> sysUserRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().eq(DataColumnConstant.USER_ID, sysUser.getId()));
                List<Long> roleIds = sysUserRoles.stream().map(sysUserRole -> sysUserRole.getRoleId()).collect(Collectors.toList());
                if (ConvertUtils.isNotEmpty(roleIds)) {
                    List<SysPermission> sysPermissions = sysPermissionService.searchPersByRoleIds(roleIds);
                    JSONArray jsonArray = new JSONArray();
                    recePermissionJsonArray(jsonArray, sysPermissions, null);
                    result.setResult(jsonArray);
                }
            }

        } catch (Exception e) {
            result.error500(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    /***
     * recePermissionJsonArray方法慨述:获取当前用户菜单
     * @param jsonArray
     * @param metaList
     * @param parentJson void
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午3:02:15
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    private void recePermissionJsonArray(JSONArray jsonArray, List<SysPermission> metaList, JSONObject parentJson) {
        try {
            for (SysPermission permission : metaList) {
                Long menuType = permission.getType();
                if (ConvertUtils.isEmpty(menuType)) {
                    continue;
                }
                Long tempPid = permission.getParentId();
                JSONObject json = recePermissionJsonObject(permission);
                if (parentJson == null
                        && 0 == tempPid) {
                    jsonArray.add(json);
                    if (YesNoEnum.NO.getCode().equals(permission.getIsLeaf())) {
                        recePermissionJsonArray(jsonArray, metaList, json);
                    }
                } else if (parentJson != null
                        && 0 != tempPid
                        && tempPid.equals(Long.parseLong(parentJson.getString(CommonConstant.ID)))) {
                    //类型( 0：一级菜单 1：子菜单  2：按钮 )
                    if (PermissionTypeEnum.BUTTON.getCode().equals(menuType.toString())) {
                        JSONObject metaJson = parentJson.getJSONObject(CommonConstant.META);
                        if (metaJson.containsKey(CommonConstant.PERMISSION_LIST)) {
                            metaJson.getJSONArray(CommonConstant.PERMISSION_LIST).add(json);
                        } else {
                            JSONArray permissionList = new JSONArray();
                            permissionList.add(json);
                            metaJson.put(CommonConstant.PERMISSION_LIST, permissionList);
                        }
                        //类型( 0：一级菜单 1：子菜单  2：按钮 )
                    } else if (PermissionTypeEnum.CATALOGUE.getCode().equals(menuType.toString())
                            || PermissionTypeEnum.MENU.getCode().equals(menuType.toString())) {
                        if (parentJson.containsKey(CommonConstant.CHILDREN)) {
                            parentJson.getJSONArray(CommonConstant.CHILDREN).add(json);
                        } else {
                            JSONArray children = new JSONArray();
                            children.add(json);
                            parentJson.put(CommonConstant.CHILDREN, children);
                        }
                        if (YesNoEnum.NO.getCode().equals(permission.getIsLeaf())) {
                            recePermissionJsonArray(jsonArray, metaList, json);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ForbesException(e.getMessage());
        }
    }

    /***
     * recePermissionJsonObject方法慨述:
     * @param permission
     * @return JSONObject
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午3:18:27
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    private JSONObject recePermissionJsonObject(SysPermission permission) {
        JSONObject json = new JSONObject();
        Long menuType = permission.getType();
        //类型(0：一级菜单 1：子菜单  2：按钮)
        if (PermissionTypeEnum.BUTTON.getCode().equals(menuType.toString())) {
            json.put("action", permission.getPerms());
            json.put("describe", permission.getName());
        } else if (PermissionTypeEnum.CATALOGUE.getCode().equals(menuType.toString())
                || PermissionTypeEnum.MENU.getCode().equals(menuType.toString())) {
            json.put(CommonConstant.ID, permission.getId());
            if (YesNoEnum.YES.getCode().equals(permission.getIsRoute())) {
                //表示生成路由
                json.put("route", "1");
            } else {
                //表示不生成路由
                json.put("route", "0");
            }
            if (YesNoEnum.YES.getCode().equals(permission.getIsHidden())) {
                //表示隐藏
                json.put("hidden", "1");
            } else {
                //表示显示
                json.put("hidden", "0");
            }
            json.put("component", permission.getComponent());
            JSONObject meta = new JSONObject();
            //默认所有的菜单都加路由缓存，提高系统性能
            meta.put("keepAlive", "true");
            meta.put("title", permission.getName());
            if (ConvertUtils.isEmpty(permission.getParentId())) {
                //一级菜单跳转地址
                json.put("redirect", permission.getRedirect());
                if (ConvertUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            } else {
                json.put("redirect", permission.getRedirect());
                if (ConvertUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            }
            json.put("meta", meta);
        }

        return json;
    }
}
