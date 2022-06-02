package com.pinzhikeji;

import java.io.Serializable;
import java.util.Date;

/**
 * 饿了么外卖信息转发表
 * @author dkq
 * @date 2019-03-23 14:09
 */
public class ElemeInfo implements Serializable {

    private int id;
    /**
     * 外卖门店编码
     */
    private long shopid;
    /**
     * 商户id
     */
    private int merchantid;
    /**
     * 商户地址
     */
    private String serverip;
    /**
     * 推送消息内容
     */
    private String orderinfo;
    /**
     * 是否推送商户 0：未推送  1：已推送
     */
    private int status;
    /**
     * 推送商户次数
     */
    private int sendcount;
    /**
     * 订单号
     */
    private String outorderid;
    /**
     * 订单状态
     */
    private int type;

    private Date createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getShopid() {
        return shopid;
    }

    public void setShopid(long shopid) {
        this.shopid = shopid;
    }

    public int getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(int merchantid) {
        this.merchantid = merchantid;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public String getOrderinfo() {
        return orderinfo;
    }

    public void setOrderinfo(String orderinfo) {
        this.orderinfo = orderinfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSendcount() {
        return sendcount;
    }

    public void setSendcount(int sendcount) {
        this.sendcount = sendcount;
    }

    public String getOutorderid() {
        return outorderid;
    }

    public void setOutorderid(String outorderid) {
        this.outorderid = outorderid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
