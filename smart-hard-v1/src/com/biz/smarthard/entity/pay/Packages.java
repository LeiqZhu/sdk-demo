package com.biz.smarthard.entity.pay;

public class Packages {
    /**
     * 购买数量
     */
    private int count;
    /**
     * 是否选中
     */
    private boolean select;
    /**
     * 套餐id
     */
    private String package_id;
    /**
     * 套餐名称
     */
    private String package_name;
    /**
     * 套餐简介
     */
    private String description;
    /**
     * 价格
     */
    private int price;
    /**
     * 有效时间
     */
    private int validDays;
    /**
     * 总计高速流量（MB）
     */
    private Long totalHighFlow;
    /**
     * 月高速流量（MB）。对于多月套餐有效
     */
    private Long totalMonthFlow;
    /**
     * 高速流量超限后，是否可限速使用
     */
    private boolean unlimit;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getValidDays() {
        return validDays;
    }

    public void setValidDays(int validDays) {
        this.validDays = validDays;
    }

    public Long getTotalHighFlow() {
        return totalHighFlow;
    }

    public void setTotalHighFlow(Long totalHighFlow) {
        this.totalHighFlow = totalHighFlow;
    }

    public Long getTotalMonthFlow() {
        return totalMonthFlow;
    }

    public void setTotalMonthFlow(Long totalMonthFlow) {
        this.totalMonthFlow = totalMonthFlow;
    }

    public boolean isUnlimit() {
        return unlimit;
    }

    public void setUnlimit(boolean unlimit) {
        this.unlimit = unlimit;
    }
}
