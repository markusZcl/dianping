package com.markus.dianping.Common;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 10:04
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonRes doError(HttpServletRequest request, HttpServletResponse response,Exception ex){
        if(ex instanceof BusinessException){
            return CommonRes.create(((BusinessException)ex).getCommonError(),"fail");
        }else if (ex instanceof NoHandlerFoundException){
            CommonError commonError = new CommonError(EmBusinessError.NO_HANDLER_FOUND_ERROR);
            return CommonRes.create(commonError,"fail");
        }else if (ex instanceof ServletRequestBindingException){
            CommonError commonError = new CommonError(EmBusinessError.BIND_EXCEPTION_ERROR);
            return CommonRes.create(commonError,"fail");
        }else{
            CommonError commonError = new CommonError(EmBusinessError.UNKNOWN_ERROR);
            return CommonRes.create(commonError,"fail");
        }
    }
}
