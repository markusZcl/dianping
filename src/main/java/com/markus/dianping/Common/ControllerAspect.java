package com.markus.dianping.Common;

import com.markus.dianping.controller.admin.AdminController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 17:09
 */
@Aspect
@Configuration
public class ControllerAspect {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Around("execution(* com.markus.dianping.controller.admin.*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object adminControllerBeforeValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        AdminPermission adminPermission = method.getDeclaredAnnotation(AdminPermission.class);
        if(adminPermission==null){
            //公共方法，直接放行
            Object resultObject = joinPoint.proceed();
            return resultObject;
        }
        //判断是否登录
        String email = (String) httpServletRequest.getSession().getAttribute(AdminController.CURRENT_ADMIN_SESSION);
        if(email == null){//说明没有登录
            if(adminPermission.produceType().equals("text/html")){
                httpServletResponse.sendRedirect("/admin/admin/loginpage");
                return null;
            }else{
                CommonError commonError = new CommonError(EmBusinessError.ADMIN_SHOULD_LOGIN);
                return CommonRes.create(commonError,"fail");
            }
        }else{
            Object resultObject = joinPoint.proceed();
            return resultObject;
        }
    }
}
