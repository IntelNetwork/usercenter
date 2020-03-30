package org.smartwork.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.forbes.biz.IMsgLogService;
import org.forbes.cache.UserCache;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.enums.AdminFlagEnum;
import org.forbes.comm.enums.BusCodeEnum;
import org.forbes.comm.enums.MsgTypeEnum;
import org.forbes.comm.enums.UserStausEnum;
import org.forbes.comm.model.SysLoginModel;
import org.forbes.comm.model.SysUserDto;
import org.forbes.comm.service.KafkaProducers;
import org.forbes.comm.utils.*;
import org.forbes.comm.vo.LoginVo;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.comm.vo.SysUserVo;
import org.smartwork.biz.ISysUserService;
import org.forbes.comm.vo.Result;
import org.smartwork.comm.GenderEnum;
import org.smartwork.comm.UserBizResultEnum;
import org.smartwork.comm.model.LoginApiUserDto;
import org.smartwork.comm.model.RegistUserDto;
import org.smartwork.dal.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = {"平台用户管理"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/user")
public class UserApiProvider {

    @Autowired
    KafkaProducers kafkaProducers;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    IMsgLogService msgLogService;


    /***
     * 验证是否注册
     * @param mobile
     * @return
     */
    @ApiOperation("验证是否注册")
    @ApiImplicitParam(value = "mobile", name = "手机号")
    @RequestMapping(value = "/check-regist", method = RequestMethod.GET)
    public Result<Boolean> checkRegist(@RequestParam(name = "mobile", required = true) String mobile) {
        Result<Boolean> result = new Result<Boolean>();
        result.setResult(true);
        if (!ConvertUtils.validateMobile(mobile)){
            result.setBizCode(UserBizResultEnum.MOBILE_FORMAT.getBizCode());
            result.setMessage(UserBizResultEnum.MOBILE_FORMAT.getBizMessage());
            result.setResult(false);
            return result;
        }
        int mobileCount = sysUserService.count(new QueryWrapper<SysUser>()
                .eq("phone",mobile));
        if(0 < mobileCount){
            result.setBizCode(UserBizResultEnum.PHONE_EXISTS.getBizCode());
            result.setMessage(String.format(UserBizResultEnum.PHONE_EXISTS.getBizFormateMessage(),mobile));
            result.setResult(false);
            return result;
        }
        return result;
    }



    /*** 验证码发送
     * @param mobile
     * @return
     */
    @ApiOperation("发送验证码")
    @ApiImplicitParam(value = "mobile", name = "手机号")
    @RequestMapping(value = "/veri-code", method = RequestMethod.GET)
    public Result<?> veriCode(@RequestParam(name = "mobile", required = true) String mobile) {
        Result<?> result = new Result<>();
        if (!ConvertUtils.validateMobile(mobile)){
            result.setBizCode(UserBizResultEnum.MOBILE_FORMAT.getBizCode());
            result.setMessage(UserBizResultEnum.MOBILE_FORMAT.getBizMessage());
            return result;
        }
        String codeKey = String.format(CommonConstant.PREFIX_AUTH_CODE,mobile);
        if(redisUtil.hasKey(codeKey)){
            result.setBizCode(UserBizResultEnum.AUTH_CODE_EXISTS.getBizCode());
            result.setMessage(UserBizResultEnum.AUTH_CODE_EXISTS.getBizMessage());
            return result;
        }
        String content = ConvertUtils.randomNext();
        Map<String,Object> sendMap = Maps.newHashMap();
        String  msgId = IDCreater.newID32();
        sendMap.put("mobile",mobile);
        sendMap.put("content",content);
        sendMap.put("busCode", BusCodeEnum.REG_VERI_CODE.getCode());
        sendMap.put("msgId", msgId);
        String sendMsg = JSON.toJSONString(sendMap);
        kafkaProducers.msgProducer("topicSms",sendMsg,o->{
            msgLogService.addMsgLog(msgId,0,true, MsgTypeEnum.SMS,sendMsg);
        },o -> {
            msgLogService.addMsgLog(msgId,0,false, MsgTypeEnum.SMS,sendMsg);
        });
        //设置超时时间
        redisUtil.set(codeKey,content);
        redisUtil.expire(codeKey, 60);
        return result;
    }

    /****
     * 性别
     * @return
     */
    @ApiOperation("性别")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/gendes",method = RequestMethod.GET)
    public List<ResultEnum> gendes(){
        return GenderEnum.resultEnums();
    }


    /****
     * 登录
     * @param mobile
     * @return
     */
    @ApiOperation("登录验证码")
    @ApiImplicitParam(value = "mobile", name = "手机号")
    @RequestMapping(value = "/login-veri-code", method = RequestMethod.GET)
    public Result<?> loginCode(@RequestParam(name = "mobile", required = true) String mobile) {
        Result<?> result = new Result<>();
        if (!ConvertUtils.validateMobile(mobile)){
            result.setBizCode(UserBizResultEnum.MOBILE_FORMAT.getBizCode());
            result.setMessage(UserBizResultEnum.MOBILE_FORMAT.getBizMessage());
            return result;
        }
        int mobileCount = sysUserService.count(new QueryWrapper<SysUser>()
                .eq("phone",mobile));
        if(0 == mobileCount){
            result.setBizCode(UserBizResultEnum.PHONE_NOT_EXISTS.getBizCode());
            result.setMessage(String.format(UserBizResultEnum.PHONE_NOT_EXISTS.getBizFormateMessage(),mobile));
            return result;
        }
        String codeKey = String.format(CommonConstant.PREFIX_LOGIN_CODE,mobile);
        if(redisUtil.hasKey(codeKey)){
            result.setBizCode(UserBizResultEnum.LOGIN_CODE_EXISTS.getBizCode());
            result.setMessage(UserBizResultEnum.LOGIN_CODE_EXISTS.getBizMessage());
            return result;
        }
        String msgId = IDCreater.newID32();
        String content = ConvertUtils.randomNext();
        Map<String,Object> sendMap = Maps.newHashMap();
        sendMap.put("mobile",mobile);
        sendMap.put("content",content);
        sendMap.put("busCode", BusCodeEnum.LOGIN_VERI_CODE.getCode());
        sendMap.put("msgId",msgId);
        String sendMsg = JSON.toJSONString(sendMap);
        kafkaProducers.msgProducer("topicSms",sendMsg,o->{
            msgLogService.addMsgLog(msgId,0,true, MsgTypeEnum.SMS,sendMsg);
        },o -> {
            msgLogService.addMsgLog(msgId,0,false, MsgTypeEnum.SMS,sendMsg);
        });
        //设置超时时间
        redisUtil.set(codeKey,content);
        redisUtil.expire(codeKey, 60);
        return result;
    }


    /****
     * 修改用户信息
     * @param httpServletRequest
     * @param sysUserDto
     * @return
     */
    @RequestMapping(value = "/up-user", method = RequestMethod.POST)
    @ApiOperation("修改用户信息")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.LOGIN_NOT_USER_ERROR_MSG),
            @ApiResponse(code = 200, response = LoginVo.class, message = Result.LOGIN_MSG)
    })
    public Result<LoginVo> upSysUser(HttpServletRequest httpServletRequest,
                                     @RequestBody org.smartwork.comm.model.SysUserDto sysUserDto) {
        Result<LoginVo> result = new Result<LoginVo>();
        try {
            if(ConvertUtils.isNotEmpty(sysUserDto.getEmail())
                    && !ConvertUtils.validateEmail(sysUserDto.getEmail())){
                result.setBizCode(UserBizResultEnum.EMAIL_FORMAT.getBizCode());
                result.setMessage(UserBizResultEnum.EMAIL_FORMAT.getBizMessage());
                return result;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getGender())
                    && !GenderEnum.existsCode(sysUserDto.getGender())){
                result.setBizCode(UserBizResultEnum.GENDER_NOT_EXISTS.getBizCode());
                result.setMessage(UserBizResultEnum.GENDER_NOT_EXISTS.getBizMessage());
                return result;
            }
            String token = httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN);
            String userName = JwtUtil.getUsername(token);
            org.forbes.comm.model.SysUser tsysUser = UserCache.getSysUser(userName);
            /******/
            boolean isUp = false;
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<SysUser>();
            updateWrapper.set("update_time",new Date());
            if(ConvertUtils.isNotEmpty(sysUserDto.getAvatar())){
                updateWrapper.set("avatar",sysUserDto.getAvatar());
                tsysUser.setAvatar(sysUserDto.getAvatar());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getEmail())){
                updateWrapper.set("email",sysUserDto.getEmail());
                tsysUser.setEmail(sysUserDto.getEmail());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getGender())){
                updateWrapper.set("gender",sysUserDto.getGender());
                tsysUser.setGender(sysUserDto.getGender());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getBirthday())){
                updateWrapper.set("birthday",sysUserDto.getBirthday());
                tsysUser.setBirthday(sysUserDto.getBirthday());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getDomicileAddress())){
                updateWrapper.set("domicile_address",sysUserDto.getDomicileAddress());
                tsysUser.setDomicileAddress(sysUserDto.getDomicileAddress());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getAddress())){
                updateWrapper.set("address",sysUserDto.getAddress());
                tsysUser.setAddress(sysUserDto.getAddress());
                isUp = true;
            }
            if(ConvertUtils.isNotEmpty(sysUserDto.getChiSaid())){
                updateWrapper.set("chi_said",sysUserDto.getChiSaid());
                tsysUser.setChiSaid(sysUserDto.getChiSaid());
                isUp = true;
            }
            sysUserService.update(updateWrapper.eq("id",tsysUser.getId()));
            /***更新缓存***/
            if(isUp){
                UserCache.setUserCashe(userName,tsysUser);
                String key = String.format(CommonConstant.PREFIX_USER, userName);
                redisUtil.set(key, tsysUser);
            }
        } catch (Exception e) {
            result.error500(e.getMessage());
            log.error(String.valueOf(CommonConstant.SC_INTERNAL_SERVER_ERROR_500), e);
        }
        return result;
    }


    /***
     * 手机号登录
     * @param loginApiUserDto
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation("手机号登录")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(value = "mobile", name = "手机号"),
            @ApiImplicitParam(value = "mobileCode", name = "手机验证码")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.LOGIN_NOT_USER_ERROR_MSG),
            @ApiResponse(code = 200, response = LoginVo.class, message = Result.LOGIN_MSG)
    })
    public Result<LoginVo> login(@RequestBody @Valid LoginApiUserDto loginApiUserDto) {
        Result<LoginVo> result = new Result<LoginVo>();
        try {
            String mobile = loginApiUserDto.getMobile();
            String mobileCode = loginApiUserDto.getMobileCode();
            SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>()
                    .eq("phone",mobile));
            if(ConvertUtils.isEmpty(sysUser)){
                result.setBizCode(UserBizResultEnum.PHONE_NOT_EXISTS.getBizCode());
                result.setMessage(String.format(UserBizResultEnum.PHONE_NOT_EXISTS.getBizFormateMessage(),mobile));
                return result;
            }
            String codeKey = String.format(CommonConstant.PREFIX_LOGIN_CODE,mobile);
            if(!redisUtil.hasKey(codeKey)){
                result.setBizCode(UserBizResultEnum.LOGIN_CODE_NOT_EXISTS.getBizCode());
                result.setMessage(UserBizResultEnum.LOGIN_CODE_NOT_EXISTS.getBizMessage());
                return result;
            }
            String codeVal = redisUtil.get(codeKey).toString();
            if(!mobileCode.equalsIgnoreCase(codeVal)){
                result.setBizCode(UserBizResultEnum.LOGIN_CODE_ERROR.getBizCode());
                result.setMessage(UserBizResultEnum.LOGIN_CODE_ERROR.getBizMessage());
                return result;
            }
            this.autoLogin(sysUser,result);
        } catch (Exception e) {
            result.error500(e.getMessage());
            log.error(String.valueOf(CommonConstant.SC_INTERNAL_SERVER_ERROR_500), e);
        }
        return result;
    }

    /***
     * 手机号注册
     * @param registUserDto
     *      * @param realname
     *      * @param mobileCode
     * @return
     */
    @RequestMapping(value = "/regist-mobile", method = RequestMethod.POST)
    @ApiOperation("手机号注册")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(value = "mobile", name = "手机号"),
            @ApiImplicitParam(value = "realname", name = "姓名/昵称",required = false),
            @ApiImplicitParam(value = "mobileCode", name = "手机验证码")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = Result.LOGIN_NOT_USER_ERROR_MSG),
            @ApiResponse(code = 200, response = LoginVo.class, message = Result.LOGIN_MSG)
    })
    public Result<LoginVo> registMobile(@RequestBody @Valid RegistUserDto registUserDto) {
        Result<LoginVo> result = new Result<LoginVo>();
        try {
            String mobile = registUserDto.getMobile();
            String mobileCode = registUserDto.getMobileCode();
            String realname = registUserDto.getRealname();
            SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>()
                    .eq("phone",mobile));
            if(ConvertUtils.isNotEmpty(sysUser)){
                result.setBizCode(UserBizResultEnum.PHONE_EXISTS.getBizCode());
                result.setMessage(String.format(UserBizResultEnum.PHONE_EXISTS.getBizFormateMessage(),mobile));
                return result;
            }
            String codeKey = String.format(CommonConstant.PREFIX_AUTH_CODE,mobile);
            if(!redisUtil.hasKey(codeKey)){
                result.setBizCode(UserBizResultEnum.LOGIN_CODE_NOT_EXISTS.getBizCode());
                result.setMessage(UserBizResultEnum.LOGIN_CODE_NOT_EXISTS.getBizMessage());
                return result;
            }
            String codeVal = redisUtil.get(codeKey).toString();
            if(!mobileCode.equalsIgnoreCase(codeVal)){
                result.setBizCode(UserBizResultEnum.LOGIN_CODE_ERROR.getBizCode());
                result.setMessage(UserBizResultEnum.LOGIN_CODE_ERROR.getBizMessage());
                return result;
            }
            /**增加用户**/
            sysUser = new SysUser();
            if(ConvertUtils.isNotEmpty(realname)){
                sysUser.setRealname(realname);
            }
            sysUser.setUsername(mobile);
            sysUser.setPhone(mobile);
            sysUser.setAdminFlag(AdminFlagEnum.ORDINARY.getCode());
            String salt = ConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            sysUser.setPassword(CommonConstant.DEFAULT_PASSWD);
            String passwordEncode = PasswordUtil.encrypt(mobile, mobile, salt);
            sysUser.setPassword(passwordEncode);
            sysUser.setStatus(UserStausEnum.NORMAL.getCode());
            sysUserService.registMobile(sysUser);
            this.autoLogin(sysUser,result);
        } catch (Exception e) {
            result.error500(e.getMessage());
            log.error(String.valueOf(CommonConstant.SC_INTERNAL_SERVER_ERROR_500), e);
        }
        return result;
    }


    /***
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("退出登录")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (ConvertUtils.isNotEmpty(token)) {
            //清空用户Token缓存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
            //清空用户角色缓存
            String userName = JwtUtil.getUsername(token);
            redisUtil.del(CommonConstant.PREFIX_USER_ROLE + userName);
            log.info("退出成功");
        }
        return Result.ok(Result.LOGOUT_SUCCESS_MSG);
    }


    /***
     * 自动登录
     * @param sysUser
     * @param result
     */
    private void autoLogin(SysUser sysUser,Result<LoginVo> result){
        String username = sysUser.getUsername();
        String syspassword = sysUser.getPassword();
        //生成token
        String token = JwtUtil.sign(username, syspassword);
        String key = CommonConstant.PREFIX_USER_TOKEN + token;
        redisUtil.set(key, token);
        //设置超时时间
        redisUtil.expire(key, JwtUtil.EXPIRE_TIME / 1000);
        LoginVo obj = new LoginVo();
        obj.setToken(token);
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUser, sysUserVo);
        obj.setUserInfo(sysUserVo);
        result.setResult(obj);
        result.success(Result.LOGIN_MSG);
        /**设置用户**/
        String usernameKey = String.format(CommonConstant.PREFIX_USER, username);
        if (!UserCache.existsUser(username)
                && !redisUtil.hasKey(usernameKey)) {
            redisUtil.set(usernameKey, sysUserVo);
        }
    }
}
