package com.markus.dianping.service.impl;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.dal.CategoryModelMapper;
import com.markus.dianping.model.CategoryModel;
import com.markus.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 9:57
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryModelMapper categoryModelMapper;
    @Override
    @Transactional
    public CategoryModel create(CategoryModel categoryModel) {
        categoryModel.setCreateAt(new Date());
        categoryModel.setUpdateAt(new Date());
        try{
            categoryModelMapper.insertSelective(categoryModel);
        }catch (DuplicateKeyException ex){
            new BusinessException(EmBusinessError.CATEGORY_NAME_DUPLICATED);
        }
        return get(categoryModel.getId());
    }

    @Override
    public CategoryModel get(Integer id) {
        return categoryModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CategoryModel> selectAll() {
        return categoryModelMapper.selectAll();
    }

    @Override
    public Integer categoryCountAll() {
        return categoryModelMapper.categoryCountAll();
    }
}
