package com.markus.dianping.request;

import javax.validation.constraints.NotBlank;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 23:18
 */
public class SellerCreateReq {
    @NotBlank(message = "商家名称不能为空")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
