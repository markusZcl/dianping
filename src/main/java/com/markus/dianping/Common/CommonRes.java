package com.markus.dianping.Common;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 9:11
 */
public class CommonRes {
    //表明对应请求的返回结果 "success" 或者 "fail"
    private String status;
    //若status=success，那么data就是返回的对象
    //若status=fail，那么data对应的就是错误码
    private Object data;

    public static CommonRes create(Object data){
        return CommonRes.create(data,"success");
    }
    public static CommonRes create(Object data,String status){
        CommonRes commonRes = new CommonRes();
        commonRes.setData(data);
        commonRes.setStatus(status);

        return commonRes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
