package com.aiguibin.entities;

import lombok.Data;

/**
 * 描述： 课程表
 *
 * @author AIguibin Date time 2018/12/23 22:32
 */
@Data
public class UserCourseEntity {
    private  Integer id;
    /**课程名*/
    private  String courseName;
    /**课程描述*/
    private  String courseDesc;
    private  Integer userId;
}
