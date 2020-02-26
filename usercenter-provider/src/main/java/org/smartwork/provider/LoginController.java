package org.smartwork.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.forbes.comm.utils.RedisUtil;
import org.smartwork.biz.ISysUserService;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.model.SysLoginModel;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.JwtUtil;
import org.forbes.comm.utils.PasswordUtil;
import org.forbes.comm.vo.LoginVo;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.SysUserVo;
import org.forbes.cache.UserCache;
import org.smartwork.dal.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags={"用户登录"})
@Slf4j
public class LoginController {
	
	@Autowired
	private ISysUserService sysUserService;

	
	@Autowired
    private RedisUtil redisUtil;

	/***
	 * login方法慨述:
	 * @param sysLoginModel
	 * @return Result<JSONObject>
	 * @创建人 huanghy
	 * @创建时间 2019年5月27日 下午6:08:18
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation("登录")
	@ApiResponses(value={
			@ApiResponse(code=500,message=Result.LOGIN_NOT_USER_ERROR_MSG),
			@ApiResponse(code=200,response=LoginVo.class, message = Result.LOGIN_MSG)
	})
	public Result<LoginVo> login(@RequestBody @Valid SysLoginModel sysLoginModel) {
		Result<LoginVo> result = new Result<LoginVo>();
		try {
			String username = sysLoginModel.getUsername();
			String password = sysLoginModel.getPassword();
			SysUser sysUser = sysUserService.getUserByName(username);
			if(sysUser==null) {
				result.error500(Result.LOGIN_NOT_USER_ERROR_MSG);
				return result;
			}else {
				//密码验证
				String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
				String syspassword = sysUser.getPassword();
				if(!syspassword.equals(userpassword)) {
					result.error500(Result.LOGIN_PASS_ERROR_MSG);
					return result;
				}
				//生成token
				String token = JwtUtil.sign(username, syspassword);
				String key = CommonConstant.PREFIX_USER_TOKEN + token;
				redisUtil.set(key, token);
				//设置超时时间
				redisUtil.expire(key, JwtUtil.EXPIRE_TIME/1000);
				LoginVo obj = new LoginVo();
				obj.setToken(token);
				SysUserVo sysUserVo = new SysUserVo();
				BeanUtils.copyProperties(sysUser, sysUserVo);
				obj.setUserInfo(sysUserVo);
				result.setResult(obj);
				result.success(Result.LOGIN_MSG);
				/**设置用户**/
				String usernameKey = String.format(CommonConstant.PREFIX_USER, username);
				if(!UserCache.existsUser(username)
						&& !redisUtil.hasKey(usernameKey)){
					redisUtil.set(usernameKey, sysUserVo);
				}
			}
		}catch(Exception e){
			 result.error500(e.getMessage());
			 log.error(String.valueOf(CommonConstant.SC_INTERNAL_SERVER_ERROR_500),e);
		}
		return result;
	}
	
	
	/****
	 * logout方法慨述:
	 * @param request
	 * @param response
	 * @return Result<Object>
	 * @创建人 huanghy
	 * @创建时间 2019年12月7日 上午10:33:01
	 * @修改人 (修改了该文件，请填上修改人的名字)
	 * @修改日期 (请填上修改该文件时的日期)
	 */
	@ApiOperation("退出登录")
	@RequestMapping(value = "/logout",method = RequestMethod.GET)
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(ConvertUtils.isNotEmpty(token)){
	    	 //清空用户Token缓存
		    redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
		    //清空用户角色缓存
		    String userName = JwtUtil.getUsername(token);
		    redisUtil.del(CommonConstant.PREFIX_USER_ROLE + userName);
		    log.info("退出成功");
	    }
		return Result.ok(Result.LOGOUT_SUCCESS_MSG);
	}
}
