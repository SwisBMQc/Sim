package com.sy.im.util;

import com.sy.im.model.Person;

/**
 * @Author：sy
 * @Date：2023/11/21
 */
object FillEmptyUtil {
    fun setEmpty(person:Person ):Person{

        if (person.userId == null){
            person.userId = ""
        }
        if (person.nickname == null){
            person.nickname = ""
        }
        if (person.gender == null){
            person.gender = ""
        }
        if (person.imgUrl == null){
            person.imgUrl = ""
        }
        if (person.remark == null){
            person.remark = ""
        }
        if (person.signature == null){
            person.signature = ""
        }
        return person;
    }
}
