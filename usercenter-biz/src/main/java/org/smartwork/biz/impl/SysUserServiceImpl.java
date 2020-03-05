package org.smartwork.biz.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.smartwork.biz.ISysUserService;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.constant.DataColumnConstant;
import org.forbes.comm.enums.AdminFlagEnum;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.enums.UserStausEnum;
import org.forbes.comm.exception.ForbesException;
import org.forbes.comm.model.SysUserDto;
import org.forbes.comm.model.UserRoleDto;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.PasswordUtil;
import org.forbes.comm.vo.UserVo;
import org.smartwork.dal.entity.SysUser;
import org.smartwork.dal.entity.SysUserRole;
import org.smartwork.dal.mapper.SysUserMapper;
import org.smartwork.dal.mapper.SysUserRoleMapper;
import org.smartwork.dal.mapper.ext.SysUserExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {


    @Autowired
    SysUserExtMapper sysUserExtMapper;
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    /***
     * getUserByName方法慨述:根据用户名查询用户
     * @param username
     * @return SysUser
     * @创建人 huanghy
     * @创建时间 2019年11月15日 下午2:10:16
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Override
    public SysUser getUserByName(String username) {
        return sysUserExtMapper.getUserByName(username);
    }


    /****
     * getRole方法慨述:
     * @param username
     * @return List<String>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:05:38
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Override
    public List<String> getRole(String username) {
        SysUser sysUser = sysUserExtMapper.getUserByName(username);
        if (ConvertUtils.isNotEmpty(sysUser)) {
            return sysUserExtMapper.selectRole(sysUser.getId());
        }
        return null;
    }


    /***
     * 分页查询用户
     */
    @Override
    public IPage<UserVo> pageUsers(IPage<UserVo> page, SysUserDto sysUserDto) {
        return sysUserExtMapper.pageUsers(page, sysUserDto);
    }

    /***
     * 增加用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(SysUserDto sysUserDto) {
        SysUser sysUser = new SysUser();
        BeanCopier.create(SysUserDto.class, SysUser.class, false)
                .copy(sysUserDto, sysUser, null);
        String salt = ConvertUtils.randomGen(8);
        sysUser.setSalt(salt);
        sysUser.setPassword(CommonConstant.DEFAULT_PASSWD);
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), sysUser.getPassword(), salt);
        sysUser.setPassword(passwordEncode);
        sysUser.setStatus(UserStausEnum.NORMAL.getCode());
        baseMapper.insert(sysUser);
        /**角色关联**/
        List<UserRoleDto> userRoleDtos = sysUserDto.getUserRoleDtos();
        if (ConvertUtils.isNotEmpty(userRoleDtos)) {
            Long userId = sysUser.getId();
            userRoleDtos.stream().forEach(userRoleDto -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(userRoleDto.getRoleId());
                sysUserRole.setUserId(userId);
                sysUserRoleMapper.insert(sysUserRole);
            });
        }
    }


    /***
     * 编辑用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUserWithRole(SysUserDto sysUserDto) {
        SysUser sysUser = new SysUser();
        BeanCopier.create(SysUserDto.class, SysUser.class, false)
                .copy(sysUserDto, sysUser, null);
        baseMapper.updateById(sysUser);
        //先删后加
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq(DataColumnConstant.USER_ID, sysUserDto.getId()));
        /**角色关联**/
        List<UserRoleDto> userRoleDtos = sysUserDto.getUserRoleDtos();
        if (ConvertUtils.isNotEmpty(userRoleDtos)) {
            Long userId = sysUser.getId();
            userRoleDtos.stream().forEach(userRoleDto -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(userRoleDto.getRoleId());
                sysUserRole.setUserId(userId);
                sysUserRoleMapper.insert(sysUserRole);
            });
        }
    }


    /***
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq(DataColumnConstant.USER_ID, id));
        boolean delBool = SqlHelper.retBool(baseMapper.deleteById(id));
        return delBool;
    }


    /***批量删除
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        List<SysUser> sysUsers = baseMapper.selectBatchIds(idList);
        if (ConvertUtils.isNotEmpty(sysUsers)) {
            long adminCount = sysUsers.stream().filter(tUser -> AdminFlagEnum.SUPER_ADMIN.getCode()
                    .equalsIgnoreCase(tUser.getAdminFlag())).count();
            if (adminCount > 0) {
                throw new ForbesException(BizResultEnum.ADMIN_FLAG_EXISTS.getBizCode(), BizResultEnum.ADMIN_FLAG_EXISTS.getBizMessage());
            }
        }
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().in(DataColumnConstant.USER_ID, idList));
        boolean delBool = SqlHelper.retBool(baseMapper.deleteBatchIds(idList));
        return delBool;
    }
}
