package com.markus.dianping.service;

import com.markus.dianping.model.CategoryModel;

import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 9:56
 */
public interface CategoryService {
    public CategoryModel create(CategoryModel categoryModel);
    public CategoryModel get(Integer id);
    public List<CategoryModel> selectAll();
    public Integer categoryCountAll();
}
