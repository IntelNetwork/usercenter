package org.smartwork.comm.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.constant.UpdateValid;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "用户注册信息")
public class RegistUserDto implements java.io.Serializable{


    private static final long serialVersionUID = -2351100378435975235L;



    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号为空")
    private String mobile;


    @ApiModelProperty(value = "昵称")
    private String realname;

    @ApiModelProperty(value = "手机验证码")
    @NotEmpty(message = "手机验证码为空")
    private String mobileCode;
}
