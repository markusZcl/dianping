package com.markus.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.markus.dianping.Common.*;
import com.markus.dianping.model.SellerModel;
import com.markus.dianping.request.PageQuery;
import com.markus.dianping.request.SellerCreateReq;
import com.markus.dianping.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 22:31
 */
@Controller("/admin/seller")
@RequestMapping("/admin/seller")
public class SellerController {
    @Autowired
    private SellerService sellerService;
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());
        List<SellerModel> allSeller = sellerService.getAllSeller();
        PageInfo<SellerModel> pageInfo = new PageInfo<>(allSeller);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","seller");
        modelAndView.addObject("ACTION_NAME","index");
        return modelAndView;
    }
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/seller/create");
        modelAndView.addObject("CONTROLLER_NAME","seller");
        modelAndView.addObject("ACTION_NAME","create");
        return modelAndView;
    }
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String create(@Valid SellerCreateReq sellerCreateReq, BindingResult bindingResult) throws BusinessException {
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR,CommonUtil.processErrorString(bindingResult));
        }
        SellerModel sellerModel = new SellerModel();
        sellerModel.setName(sellerCreateReq.getName());
        sellerService.create(sellerModel);
        return "redirect:/admin/seller/index";
    }
    @RequestMapping(value = "/down",method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public CommonRes down(Integer id) throws BusinessException {
        SellerModel sellerModel = sellerService.changeStatus(id,1);
        return CommonRes.create(sellerModel);
    }
    @RequestMapping(value = "/up",method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public CommonRes up(Integer id) throws BusinessException {
        SellerModel sellerModel = sellerService.changeStatus(id,0);
        return CommonRes.create(sellerModel);
    }
}
