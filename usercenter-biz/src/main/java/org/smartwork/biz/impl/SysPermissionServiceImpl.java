package org.smartwork.biz.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.smartwork.biz.ISysPermissionService;
import org.forbes.comm.constant.DataColumnConstant;
import org.forbes.comm.constant.PermsCommonConstant;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.enums.YesNoEnum;
import org.forbes.comm.exception.ForbesException;
import org.forbes.comm.utils.ConvertUtils;
import org.smartwork.dal.entity.SysPermission;
import org.smartwork.dal.mapper.SysPermissionMapper;
import org.smartwork.dal.mapper.ext.SysPermissionExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Autowired
    SysPermissionExtMapper sysPermissionExtMapper;

    /***增加权限
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SysPermission entity) {
        if (ConvertUtils.isEmpty(entity.getParentId())) {
            entity.setParentId(0L);
        }
        boolean retBool = SqlHelper.retBool(baseMapper.insert(entity));
        Long parentId = entity.getParentId();
        noLeafParent(parentId);
        return retBool;
    }

    /***
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SysPermission entity) {
        if (ConvertUtils.isEmpty(entity.getParentId())) {
            entity.setParentId(0L);
        }
        SysPermission oldSysPermission = baseMapper.selectById(entity.getId());
        Long parentId = entity.getParentId();
        Long oldParentId = oldSysPermission.getParentId();
        if (0 != parentId
                && parentId != oldParentId) {
            noLeafParent(parentId);
            yesLeafParent(oldParentId);
        }
        boolean retBool = SqlHelper.retBool(baseMapper.updateById(entity));
        return retBool;
    }


    /***
     * noLeafParent方法慨述:设置为父级
     * @param parentId void
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:03:19
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void noLeafParent(Long parentId) {
        if (0 != parentId) {
            SysPermission parentSysPermission = baseMapper.selectById(parentId);
            if (YesNoEnum.YES.getCode()
                    .equalsIgnoreCase(parentSysPermission.getIsLeaf())) {
                parentSysPermission.setIsLeaf(YesNoEnum.NO.getCode());
                baseMapper.updateById(parentSysPermission);
            }
        }
    }


    /***
     * yesLeafParent方法慨述:设置为子集
     * @param parentId void
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:10:16
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void yesLeafParent(Long parentId) {
        if (0 != parentId) {
            int childCount = baseMapper.selectCount(new QueryWrapper<SysPermission>().eq(DataColumnConstant.PARENT_ID, parentId));
            if (0 == childCount) {
                SysPermission parentSysPermission = baseMapper.selectById(parentId);
                parentSysPermission.setIsLeaf(YesNoEnum.YES.getCode());
                baseMapper.updateById(parentSysPermission);
            }
        }
    }


    /***
     * removeById方法慨述:
     * @param id
     * @return boolean
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:30:59
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        SysPermission sysPermission = baseMapper.selectById(id);
        Long parentId = sysPermission.getParentId();
        boolean delBool = SqlHelper.retBool(baseMapper.deleteById(id));
        yesLeafParent(parentId);
        return delBool;
    }


    /****
     * removeByIds方法慨述:删除权限同时删除其子权限
     * @param idList
     * @return boolean
     * @创建人 huanghy
     * @创建时间 2019年12月10日 下午2:32:30
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        idList.forEach(id -> {
            int childCount = baseMapper.selectCount(new QueryWrapper<SysPermission>().eq(PermsCommonConstant.PARENT_ID, id));
            if (childCount > 0) {
                throw new ForbesException(BizResultEnum.PERMISSION_CHILD_EXISTS.getBizCode(), BizResultEnum.PERMISSION_CHILD_EXISTS.getBizMessage());
            }
        });
        boolean delBool = SqlHelper.retBool(baseMapper.deleteBatchIds(idList));
        return delBool;
    }


    /****
     * searchPersByRoleIds方法慨述:根据角色IDS查询权限
     * @param roleIds
     * @return List<PermissionVo>
     * @创建人 huanghy
     * @创建时间 2019年12月10日 上午11:34:09
     * @修改人 (修改了该文件 ， 请填上修改人的名字)
     * @修改日期 (请填上修改该文件时的日期)
     */
    public List<SysPermission> searchPersByRoleIds(List<Long> roleIds) {
        return sysPermissionExtMapper.searchPersByRoleIds(roleIds);
    }
}
