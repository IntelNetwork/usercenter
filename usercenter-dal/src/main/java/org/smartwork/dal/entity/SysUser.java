package org.smartwork.dal.entity;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import org.forbes.comm.annotations.QueryColumn;
import org.forbes.comm.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Table: f_sys_user
 */
@Data
@ApiModel(description = "用户信息")
@EqualsAndHashCode(callSuper = false)
@TableName("f_sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 6199785596106991966L;

    /**
     * 登录账号
     * Table:     f_sys_user
     * Column:    username
     * Nullable:  false
     */
    @ApiModelProperty(value = "登录账号", required = true)
    @NotEmpty(message = "登录账号为空")
    @QueryColumn(column = "username",sqlKeyword = SqlKeyword.LIKE)
    private String username;

    /**
     * 状态
     * Table:     f_sys_user
     * Column:    status
     * Nullable:  false
     */
    @ApiModelProperty(value = "状态", required = true)
    private String status;

    /**
     * 密码
     * Table:     f_sys_user
     * Column:    password
     * Nullable:  true
     */
    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码账号为空")
    private String password;

    /**
     * md5密码盐
     * Table:     f_sys_user
     * Column:    salt
     * Nullable:  true
     */
    @ApiModelProperty(value = "md5密码盐")
    private String salt;

    /**
     * 头像
     * Table:     f_sys_user
     * Column:    avatar
     * Nullable:  true
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 邮件
     * Table:     f_sys_user
     * Column:    email
     * Nullable:  true
     */
    @ApiModelProperty(value = "邮件")
    private String email;

    /**
     * 电话
     * Table:     f_sys_user
     * Column:    phone
     * Nullable:  true
     */
    @ApiModelProperty(value = "电话", required = true)
    @NotEmpty(message = "电话为空")
    private String phone;

    /**
     * 姓名
     * Table:     f_sys_user
     * Column:    realname
     * Nullable:  true
     */
    @ApiModelProperty(value = "姓名", required = true)
    @QueryColumn(column = "realname",sqlKeyword = SqlKeyword.LIKE)
    private String realname;


    @ApiModelProperty(value = "管理员标识(-1-普通人员,0-超级管理员,1-企业管理员,2-不限制企业号)")
    @NotEmpty(message = "管理员标识不能为空")
    private String adminFlag;

    @ApiModelProperty(value = "性别0-男,1-女",example = "0")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    private Date birthday;

    @ApiModelProperty(value = "户口所在地")
    private String domicileAddress;

    @ApiModelProperty(value = "现居地")
    private String address;

    @ApiModelProperty(value = "个性签名")
    private String chiSaid;
}