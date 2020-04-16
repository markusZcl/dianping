package com.markus.dianping.service.impl;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.Common.EmBusinessError;
import com.markus.dianping.dal.UserModelMapper;
import com.markus.dianping.model.UserModel;
import com.markus.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/8 23:15
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserModelMapper userModelMapper;
    @Override
    public UserModel getUser(Integer id) {
        return userModelMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public UserModel registerUser(UserModel userModel) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        userModel.setCreateAt(new Date());
        userModel.setUpdateAt(new Date());
        userModel.setPassword(encodeByMD5(userModel.getPassword()));
        try{
            userModelMapper.insertSelective(userModel);
        }catch (DuplicateKeyException e){
            throw new BusinessException(EmBusinessError.REGISTER_DUP_ERROR);
        }
        return getUser(userModel.getId());
    }

    @Override
    public UserModel login(String telphone, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        UserModel userModel = userModelMapper.selectByTelphoneAndPassword(telphone,encodeByMD5(password));
        if(userModel==null){
            throw new BusinessException(EmBusinessError.LOGIN_FAIL);
        }
        return userModel;
    }

    @Override
    public Integer countAllUser() {
        return userModelMapper.countAllUser();
    }

    private String encodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));

    }
}
