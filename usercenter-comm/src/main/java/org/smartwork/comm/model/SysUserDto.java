package org.smartwork.comm.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/***更新
 */
@Data
@ApiModel(description = "用户信息")
public class SysUserDto implements java.io.Serializable {




    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "邮件")
    private String email;

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
