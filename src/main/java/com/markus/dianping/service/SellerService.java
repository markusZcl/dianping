package com.markus.dianping.service;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.model.SellerModel;

import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 22:23
 */
public interface SellerService {
    public SellerModel create(SellerModel sellerModel);
    public SellerModel get(Integer id);
    public List<SellerModel> getAllSeller();
    public SellerModel changeStatus(Integer id,Integer disabledFlag) throws BusinessException;
    public Integer sellerCountAll();
}
