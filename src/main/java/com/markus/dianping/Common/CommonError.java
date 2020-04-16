package com.markus.dianping.Common;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 9:35
 */
public class CommonError {
    //错误码
    private Integer errCode;
    //错误信息
    private String errMsg;

    public CommonError(Integer errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    public CommonError(EmBusinessError emBusinessError){
        this.errCode = emBusinessError.getErrCode();
        this.errMsg = emBusinessError.getErrMsg();
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
