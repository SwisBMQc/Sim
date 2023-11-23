package com.sy.im.netty.service;

import com.sy.im.common.model.vo.Person;
import com.sy.im.common.result.ResultJson;

/**
 * netty service
 * @Author：sy
 * @Date：2023/11/14
 */
public interface PersonService {
    ResultJson login(String username, String password);

    ResultJson register(String username, String password);

    ResultJson updatePerson(Person person);

    Person getPerson(String fromId);

//    ResultJson cancel(String username);
}
