package com.aiguibin.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "EMPLOYEE")
@Data
public class Employee extends BaseEntity{
    @Column(name = "NAME", nullable = false, length = 40)
    private String employeeName;
    @Column(name = "AGE", nullable = false, length = 3)
    private Integer employeeAge;
    @Column(name = "SR" )
    private Date employeeSR;
    @Column(name = "SSBM", nullable = false, length = 40)
    private Department department;

}
