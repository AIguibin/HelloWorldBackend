package com.aiguibin.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@MappedSuperclass
@GenericGenerator(name = "UUID", strategy = "UUID")
@Data
public class BaseEntity {
    /**
     * JPA原生主键生成策略写法
     * @GeneratedValue(strategy = GenerationType.AUTO)
     * Hibernate原生主键生成策略写法
     * @GeneratedValue(generator = "paymentableGenerator")
     * @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
     * JPA与Hibernate结合的使用写法
     * @GenericGenerator(name = "UUID", strategy = "UUID")
     * @GeneratedValue(generator = "UUID")
     */
    @Id
    @GeneratedValue(generator="UUID")
    @Column(name = "ID", nullable = false, length = 40)
    private String id;

    @Column(name = "CJSJ", nullable = false)
    private Long createdTime = -1L;
    @Column(name = "GXSJ", nullable = false)
    private Long updatedTime = -1L;
    @Column(name = "CZZ", nullable = false, length = 40)
    private String operator;
    @Column(name = "YXX", nullable = false)
    private Boolean valid = true;
}
