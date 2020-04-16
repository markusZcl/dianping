package com.markus.dianping.Common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/9 12:15
 */
public class CommonUtil {
    public static String processErrorString(BindingResult bindingResult){
        if(!bindingResult.hasErrors()){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getDefaultMessage()+",");
        }
        String str = stringBuilder.substring(0,stringBuilder.length()-1);
        return str;
    }
}
