package com.sy.im.util;

import com.sy.im.model.Person;

/**
 * @Author：sy
 * @Date：2023/11/21
 */
public class FillEmptyUtil {
    public static Person setEmpty(Person person){

        if (person.getUserId() == null){
            person.setUserId("");
        }
        if (person.getNickname() == null){
            person.setNickname("");
        }
        if (person.getImgUrl() == null){
            person.setImgUrl("");
        }
        if (person.getGender() == null){
            person.setGender("");
        }
        if (person.getRemark() == null){
            person.setRemark("");
        }
        if (person.getSignature() == null){
            person.setSignature("");
        }
        return person;
    }
}
