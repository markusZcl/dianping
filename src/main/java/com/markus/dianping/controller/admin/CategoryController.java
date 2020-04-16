package com.markus.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.markus.dianping.Common.AdminPermission;
import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.CommonUtil;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.model.CategoryModel;
import com.markus.dianping.request.CategoryReq;
import com.markus.dianping.request.PageQuery;
import com.markus.dianping.service.CategoryService;
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
 * Date:Create in 2020/4/10 10:25
 */
@Controller("/admin/category")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery){
        ModelAndView modelAndView = new ModelAndView("/admin/category/index");
        PageHelper.startPage(pageQuery.getPage(),pageQuery.getSize());
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        PageInfo<CategoryModel> pageInfo = new PageInfo<>(categoryModelList);
        modelAndView.addObject("data",pageInfo);
        modelAndView.addObject("CONTROLLER_NAME","category");
        modelAndView.addObject("ACTION_NAME","index");
        return modelAndView;
    }
    @RequestMapping("/createpage")
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/category/create");
        modelAndView.addObject("CONTROLLER_NAME","category");
        modelAndView.addObject("ACTION_NAME","create");
        return modelAndView;
    }
    @RequestMapping("/create")
    public String create(@Valid CategoryReq categoryReq, BindingResult bindingResult) throws BusinessException {
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR, CommonUtil.processErrorString(bindingResult));
        }
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName(categoryReq.getName());
        categoryModel.setIconUrl(categoryReq.getIconUrl());
        categoryModel.setSort(categoryReq.getSort());
        categoryService.create(categoryModel);
        return "redirect:/admin/category/index";
    }

}
