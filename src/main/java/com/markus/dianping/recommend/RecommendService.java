package com.markus.dianping.recommend;

import com.markus.dianping.dal.RecommendDOMapper;
import com.markus.dianping.model.RecommendDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/17 12:36
 */
@Service
public class RecommendService implements Serializable {
    @Autowired
    private RecommendDOMapper recommendDOMapper;
    public List<Integer> recall(Integer id){
        RecommendDO recommendDO = recommendDOMapper.selectByPrimaryKey(id);
        if(recommendDO == null){
            recommendDO = recommendDOMapper.selectByPrimaryKey(9999999);
        }
        String[] shopIdArr = recommendDO.getRecommend().split(",");
        List<Integer> shopIdList = new ArrayList<>();
        for(int i=0;i<shopIdArr.length;i++){
            shopIdList.add(Integer.valueOf(shopIdArr[i]));
        }
        return shopIdList;
    }
}
