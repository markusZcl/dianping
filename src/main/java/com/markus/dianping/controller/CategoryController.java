package com.markus.dianping.controller;

import com.markus.dianping.Common.CommonRes;
import com.markus.dianping.model.CategoryModel;
import com.markus.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 10:54
 */
@Controller("/category")
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/list")
    @ResponseBody
    public CommonRes list(){
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        return CommonRes.create(categoryModelList);
    }
}
