package com.markus.dianping.request;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 10:35
 */
public class CategoryReq {
    private String name;
    private String iconUrl;
    private Integer sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
