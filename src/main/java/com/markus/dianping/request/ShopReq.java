package com.markus.dianping.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 11:52
 */
public class ShopReq {
    @NotBlank(message = "服务名不能为空")
    private String name;
    @NotNull(message = "人均价格不能为空")
    private Integer pricePerMan;
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;
    @NotNull(message = "精度不能为空")
    private BigDecimal longitude;
    @NotNull(message = "服务类目不能为空")
    private Integer categoryId;
    @NotNull(message = "服务商户不能为空")
    private Integer sellerId;
    private String tags;
    @NotBlank(message = "营业开始时间不能为空")
    private String startTime;
    @NotBlank(message = "营业结束时间不能为空")
    private String endTime;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotBlank(message = "图标不能为空")
    private String iconUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPricePerMan() {
        return pricePerMan;
    }

    public void setPricePerMan(Integer pricePerMan) {
        this.pricePerMan = pricePerMan;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}