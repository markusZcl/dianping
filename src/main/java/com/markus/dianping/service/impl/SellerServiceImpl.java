package com.markus.dianping.service.impl;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.dal.SellerModelMapper;
import com.markus.dianping.model.SellerModel;
import com.markus.dianping.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 22:26
 */
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerModelMapper sellerModelMapper;
    @Override
    @Transactional
    public SellerModel create(SellerModel sellerModel) {
        sellerModel.setCreateAt(new Date());
        sellerModel.setUpdateAt(new Date());
        sellerModel.setRemarkScore(new BigDecimal(0));
        sellerModel.setDisabledFlag(0);
        sellerModelMapper.insertSelective(sellerModel);
        return get(sellerModel.getId());
    }

    @Override
    public SellerModel get(Integer id) {
        return sellerModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SellerModel> getAllSeller() {
        return sellerModelMapper.selectAll();
    }

    @Override
    public SellerModel changeStatus(Integer id, Integer status) throws BusinessException {
        SellerModel sellerModel = get(id);
        if(sellerModel == null){
            throw new BusinessException(EmBusinessError.VALID_PARAMETER_ERROR);
        }
        sellerModel.setDisabledFlag(status);
        sellerModelMapper.updateByPrimaryKey(sellerModel);
        return sellerModel;
    }

    @Override
    public Integer sellerCountAll() {
        return sellerModelMapper.sellerCountAll();
    }
}
