package org.smartwork.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.forbes.comm.exception.ForbesException;
import org.smartwork.dal.entity.ZGEarnings;
import org.smartwork.dal.mapper.ZGEarningsMapper;
import org.smartwork.service.IZGEarningsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ZGEarningsServiceImpl extends ServiceImpl<ZGEarningsMapper, ZGEarnings> implements IZGEarningsService {





    /***
     * 注册收益账号
     * @param earnings
     * @throws ForbesException
     */
    @DS(value = "memberds")
    @Transactional(propagation = Propagation.MANDATORY,rollbackFor = Exception.class)
    public void registEarnings(ZGEarnings earnings)
            throws ForbesException{
        baseMapper.insert(earnings);
    }
}