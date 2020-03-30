package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Table: fb_zg_earnings
 */
@Data
@ApiModel(description="智工收益")
@TableName("fb_zg_earnings")
public class ZGEarnings extends BaseEntity {
    private static final long serialVersionUID = 6450314062449646587L;
    /**
     * 用户ID
     *
     * Table:     fb_zg_earnings
     * Column:    user_id
     * Nullable:  true
     */
    @ApiModelProperty(value = "用户ID",example="0")
    private Long userId;

    /**
     * 用户名
     *
     * Table:     fb_zg_earnings
     * Column:    user_name
     * Nullable:  true
     */
    @ApiModelProperty(value = "用户名",example="")
    private String userName;

    /**
     * 金额
     *
     * Table:     fb_zg_earnings
     * Column:    amount
     * Nullable:  true
     */
    @ApiModelProperty(value = "金额",example="0.00")
    private BigDecimal amount;

    /**
     * 修改之前金额
     *
     * Table:     fb_zg_earnings
     * Column:    before_amount
     * Nullable:  true
     */
    @ApiModelProperty(value = "修改之前金额",example="0.00")
    private BigDecimal beforeAmount;

    /**
     * 产生时间
     *
     * Table:     fb_zg_earnings
     * Column:    have_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "产生时间",example="")
    private Date haveTime;
}