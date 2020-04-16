package com.markus.dianping.controller;

import com.markus.dianping.Common.*;
import com.markus.dianping.model.UserModel;
import com.markus.dianping.request.LoginReq;
import com.markus.dianping.request.RegisterReq;
import com.markus.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/8 22:15
 */
@Controller("/user")
@RequestMapping("/user")
public class UserController {
    private static final String CURRENT_USER_SESSION="currentUserSession";
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private UserService userService;
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }
    @RequestMapping("/index")
    public ModelAndView index(){
        String userName = "markus";
        ModelAndView modelAndView = new ModelAndView("/index.html");
        modelAndView.addObject("name",userName);
        return modelAndView;
    }
    @RequestMapping("/get")
    @ResponseBody
    public CommonRes get(@RequestParam("id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUser(id);
        if(userModel==null){
            //return CommonRes.create(new CommonError(EmBusinessError.NOT_FOUND_OBJECT),"fail");
            throw new BusinessException(EmBusinessError.NOT_FOUND_OBJECT);
        }else{
            return CommonRes.create(userModel);
        }
    }
    @RequestMapping("/register")
    @ResponseBody
    public CommonRes register(@Valid @RequestBody RegisterReq registerReq,BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR, CommonUtil.processErrorString(bindingResult));
        }
        UserModel registerUser = new UserModel();
        registerUser.setTelphone(registerReq.getTelphone());
        registerUser.setGender(registerReq.getGender());
        registerUser.setNickName(registerReq.getNickName());
        registerUser.setPassword(registerReq.getPassword());
        UserModel userModel = userService.registerUser(registerUser);
        return CommonRes.create(userModel);
    }
    @RequestMapping("/login")
    @ResponseBody
    public CommonRes login(@RequestBody @Valid LoginReq loginReq,BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,CommonUtil.processErrorString(bindingResult));
        }
        UserModel userModel = userService.login(loginReq.getTelphone(),loginReq.getPassword());
        httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION,userModel);
        return CommonRes.create(userModel);
    }
    @RequestMapping("/logout")
    @ResponseBody
    public CommonRes logout(){
        httpServletRequest.getSession().invalidate();//使当前session失效
        return CommonRes.create(null);
    }
    //获取当前用户的信息
    @RequestMapping("/getcurrentuser")
    @ResponseBody
    public CommonRes getCurrentUser(){
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);
        return CommonRes.create(userModel);
    }
}
