package com.sy.im.common.model.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {
    String userId;
    String nickname;
    String imgUrl;
    String gender;
    String remark;
    String signature;

}
