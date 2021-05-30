package com.aiguibin.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@MappedSuperclass
@Data
public class BaseEntity {
    /**
     * JPA原生主键生成策略写法
     * @GeneratedValue(strategy = GenerationType.AUTO)
     * Hibernate原生主键生成策略写法
     * @GeneratedValue(generator = "Generator")
     * @GenericGenerator(name = "Generator", strategy = "uuid")
     */
    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "uuid")
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
