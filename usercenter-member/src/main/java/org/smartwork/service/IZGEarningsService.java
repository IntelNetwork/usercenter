package org.smartwork.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.forbes.comm.exception.ForbesException;
import org.smartwork.dal.entity.ZGEarnings;

public interface IZGEarningsService extends IService<ZGEarnings> {


    /***
     * 注册收益账号
     * @param earnings
     * @throws ForbesException
     */
    public void registEarnings(ZGEarnings earnings) throws ForbesException;
}