package com.markus.dianping.Common;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 9:49
 */
//@ControllerAdvice
public class BusinessException extends Exception {
    private CommonError commonError;
    public BusinessException(EmBusinessError emBusinessError){
        super();
        this.commonError = new CommonError(emBusinessError);
    }
    public BusinessException(EmBusinessError emBusinessError,String errMsg){
        super();
        this.commonError = new CommonError(emBusinessError);
        this.commonError.setErrMsg(errMsg);
    }
    public CommonError getCommonError() {
        return commonError;
    }

    public void setCommonError(CommonError commonError) {
        this.commonError = commonError;
    }
}
