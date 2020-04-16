package com.markus.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.markus.dianping.Common.AdminPermission;
import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.CommonUtil;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.model.ShopModel;
import com.markus.dianping.request.PageQuery;
import com.markus.dianping.request.ShopReq;
import com.markus.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 12:16
 */
@Controller("/admin/shop")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){
        ModelAndView modelAndView = new ModelAndView("/admin/shop/index");
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());
        List<ShopModel> shopModelList = shopService.selectAll();
        PageInfo<ShopModel> pageInfo = new PageInfo<>(shopModelList);
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","shop");
        modelAndView.addObject("ACTION_NAME","index");
        return modelAndView;
    }
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/shop/create");
        modelAndView.addObject("CONTROLLER_NAME","shop");
        modelAndView.addObject("ACTION_NAME","create");
        return modelAndView;
    }
    @RequestMapping("/create")
    @AdminPermission
    public String create(@Valid ShopReq shopReq, BindingResult bindingResult) throws BusinessException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR, CommonUtil.processErrorString(bindingResult));
        }
        ShopModel shopModel = new ShopModel();
        shopModel.setName(shopReq.getName());
        shopModel.setSellerId(shopReq.getSellerId());
        shopModel.setCategoryId(shopReq.getCategoryId());
        shopModel.setAddress(shopReq.getAddress());
        shopModel.setStartTime(shopReq.getStartTime());
        shopModel.setEndTime(shopReq.getEndTime());
        shopModel.setTags(shopReq.getTags());
        shopModel.setPricePerMan(shopReq.getPricePerMan());
        shopModel.setLongitude(shopReq.getLongitude());
        shopModel.setLatitude(shopReq.getLatitude());
        shopModel.setIconUrl(shopReq.getIconUrl());
        shopService.create(shopModel);
        return "redirect:/admin/shop/index";
    }
}
