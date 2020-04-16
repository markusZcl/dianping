package com.markus.dianping.service;

import com.markus.dianping.Common.BusinessException;
import com.markus.dianping.model.UserModel;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/8 23:14
 */
public interface UserService {
    public UserModel getUser(Integer id);
    public UserModel registerUser(UserModel userModel) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;
    public UserModel login(String telphone,String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException;
    public Integer countAllUser();
}
