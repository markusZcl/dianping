package com.markus.dianping.Common;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 9:38
 */
public enum EmBusinessError {
    //错误码通常从10000开始
    NOT_FOUND_OBJECT(10001,"请求对象不存在"),
    UNKNOWN_ERROR(10002,"未知错误"),
    NO_HANDLER_FOUND_ERROR(10003,"找不到执行路径操作"),
    BIND_EXCEPTION_ERROR(10004,"请求参数错误"),
    VALID_PARAMETER_ERROR(10005,"请求参数校验失败"),

    //用户错误码从20000开始
    REGISTER_DUP_ERROR(20001,"手机号重复,用户已存在"),
    LOGIN_FAIL(20002,"手机号或密码错误"),
    //admin错误
    ADMIN_SHOULD_LOGIN(30001,"管理员需要先登录"),
    //品类相关错误
    CATEGORY_NAME_DUPLICATED(40001,"品类名不能重复");
    private Integer errCode;
    private String errMsg;

    EmBusinessError(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
