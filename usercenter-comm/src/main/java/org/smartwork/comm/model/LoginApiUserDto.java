package org.smartwork.comm.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
@Data
@ApiModel(description = "前台用户登录信息")
public class LoginApiUserDto implements Serializable {


    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号为空")
    private String mobile;
    
    @ApiModelProperty(value = "手机验证码")
    @NotEmpty(message = "手机验证码为空")
    private String mobileCode;
}
