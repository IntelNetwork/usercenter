package org.smartwork.dal.entity;


import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.TableName;
import org.forbes.comm.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Table: f_sys_role
 */
@Data
@ApiModel(description="角色信息")
@EqualsAndHashCode(callSuper = false)
@TableName("f_sys_role")
public class SysRole extends BaseEntity {
    
	
	private static final long serialVersionUID = -1667826254533837671L;

    /**
     * 角色名称
     * Table:     f_sys_role
     * Column:    role_name
     * Nullable:  true
     */
    @ApiModelProperty(value = "角色名称",required = true)
    @NotEmpty(message="角色名称为空")
    private String roleName;

    /**
     * 角色编码
     * Table:     f_sys_role
     * Column:    role_code
     * Nullable:  true
     */
    @ApiModelProperty(value = "角色编码",required = true)
    @NotEmpty(message="角色编码为空")
    private String roleCode;

    /**
     * 角色描述
     * Table:     f_sys_role
     * Column:    description
     * Nullable:  true
     */
    @ApiModelProperty(value = "角色描述")
    private String description;
}