package org.smartwork.dal.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.entity.BaseEntity;

/**
 * Table: f_sys_permission
 */
@Data
@ApiModel(description = "权限信息")
@EqualsAndHashCode(callSuper = false)
@TableName("f_sys_permission")
public class SysPermission extends BaseEntity {

    private static final long serialVersionUID = 6430290986069297694L;

    /**
     * 父级id
     * Table:     f_sys_permission
     * Column:    parent_id
     * Nullable:  true
     */
    @ApiModelProperty(value = "父级id", example = "-1")
    private Long parentId;

    /**
     * 资源名称
     * Table:     f_sys_permission
     * Column:    name
     * Nullable:  true
     */
    @ApiModelProperty(value = "资源名称", required = true)
    @NotEmpty(message = "资源名称为空")
    private String name;

    /**
     * 菜单权限编码
     * Table:     f_sys_permission
     * Column:    perms
     * Nullable:  true
     */
    @ApiModelProperty(value = "菜单权限编码", required = true)
    @NotEmpty(message = "菜单权限编码为空")
    private String perms;

    /**
     * 组件
     * Table:     f_sys_permission
     * Column:    component
     * Nullable:  true
     */
    @ApiModelProperty(value = "组件")
    private String component;

    /**
     * 组件名称
     * Table:     f_sys_permission
     * Column:    component_name
     * Nullable:  true
     */
    @ApiModelProperty(value = "组件名称")
    private String componentName;

    /**
     * 是否聚合子路由
     * Table:     f_sys_permission
     * Column:    always_show
     * Nullable:  true
     */
    @ApiModelProperty(value = "是否聚合子路由", required = true)
    @NotEmpty(message = "是否聚合子路由为空", groups = SaveValid.class)
    private String alwaysShow;

    /**
     * 是否路由
     * Table:     f_sys_permission
     * Column:    is_route
     * Nullable:  true
     */
    @ApiModelProperty(value = "是否路由", required = true)
    @NotEmpty(message = "是否路由为空", groups = SaveValid.class)
    private String isRoute;

    /**
     * 是否子集
     * Table:     f_sys_permission
     * Column:    is_leaf
     * Nullable:  true
     */
    @ApiModelProperty(value = "是否子集")
    private String isLeaf;

    /**
     * 是否隐藏
     * Table:     f_sys_permission
     * Column:    is_hidden
     * Nullable:  true
     */
    @ApiModelProperty(value = "是否隐藏")
    private String isHidden;

    /**
     * 排序号
     * Table:     f_sys_permission
     * Column:    sort_no
     * Nullable:  true
     */
    @ApiModelProperty(value = "排序号", example = "0")
    private Integer sortNo;

    /**
     * 描述
     * Table:     f_sys_permission
     * Column:    description
     * Nullable:  true
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 路由请求地址
     * Table:     f_sys_permission
     * Column:    redirect
     * Nullable:  true
     */
    @ApiModelProperty(value = "路由请求地址", required = true)
    @NotEmpty(message = "路由请求地址为空", groups = SaveValid.class)
    private String redirect;

    /**
     * 后台请求地址
     * Table:     f_sys_permission
     * Column:    url
     * Nullable:  true
     */
    @ApiModelProperty(value = "后台请求地址", required = true)
    @NotEmpty(message = "后台请求地址为空", groups = SaveValid.class)
    private String url;

    /**
     * 菜单图标
     * Table:     f_sys_permission
     * Column:    icon
     * Nullable:  true
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;

    /**
     * 类型
     * Table:     f_sys_permission
     * Column:    type
     * Nullable:  true
     */
    @ApiModelProperty(value = "类型", example = "0", required = true)
    @NotNull(message = "类型为空", groups = SaveValid.class)
    private Long type;

    /**
     * 等级
     * Table:     f_sys_permission
     * Column:    type
     * Nullable:  true
     */
    @ApiModelProperty(value = "等级", example = "0", required = true)
    @NotNull(message = "等级为空", groups = SaveValid.class)
    private Long grade;

}