package com.markus.dianping.controller.admin;

import com.markus.dianping.Common.AdminPermission;
import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.CommonRes;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.model.ShopModel;
import com.markus.dianping.service.CategoryService;
import com.markus.dianping.service.SellerService;
import com.markus.dianping.service.ShopService;
import com.markus.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 15:12
 */
@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {
    @Value("${admin.email}")
    private String email;
    @Value("${admin.encryptPassword}")
    private String encryptPassword;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private SellerService sellerService;
    public static final String CURRENT_ADMIN_SESSION="currentAdminSession";
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/index");
        int count = userService.countAllUser();
        modelAndView.addObject("userCount",count);
        modelAndView.addObject("categoryCount",categoryService.categoryCountAll());
        modelAndView.addObject("shopCount",shopService.shopCountAll());
        modelAndView.addObject("sellerCount",sellerService.sellerCountAll());
        modelAndView.addObject("CONTROLLER_NAME","admin");
        modelAndView.addObject("ACTION_NAME","index");
        return modelAndView;
    }
    @RequestMapping("/loginpage")
    public ModelAndView loginPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/login");
        return modelAndView;
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String email,String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,"邮箱和密码不能为空");
        }
        if (!email.equals(this.email) || !encodeByMD5(password).equals(this.encryptPassword)){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,"用户名密码不正确");
        }
        httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION,email);
        return "redirect:/admin/admin/index";
    }
    private String encodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));

    }
}
