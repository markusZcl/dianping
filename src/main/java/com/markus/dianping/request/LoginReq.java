package com.markus.dianping.request;

import javax.validation.constraints.NotBlank;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 13:08
 */
public class LoginReq {
    @NotBlank(message = "手机号不能为空")
    private String telphone;
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
