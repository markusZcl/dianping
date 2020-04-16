package com.markus.dianping.service;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.model.ShopModel;
import org.apache.ibatis.annotations.Param;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/10 11:36
 */
public interface ShopService {
    public ShopModel create(ShopModel shopModel) throws BusinessException;
    public ShopModel get(Integer id);
    public List<ShopModel> selectAll();
    public Integer shopCountAll();
    public List<ShopModel> recommend(BigDecimal longitude,BigDecimal latitude);
    public List<Map<String,Object>> searchGroupByTags(String keyword,Integer categoryId,String tags);
    List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword,Integer orderBy,Integer categoryId,String tags);
    Map<String,Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword,Integer orderBy,Integer categoryId,String tags) throws IOException;
}
