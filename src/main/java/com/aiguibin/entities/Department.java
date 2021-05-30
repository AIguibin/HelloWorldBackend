package com.aiguibin.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DEPARTMENT")
@Data
public class Department extends BaseEntity{
    @Column(name = "NAME", nullable = false, length = 40)
    private String departmentName;
}
