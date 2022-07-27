package com.educlient.data.entity;

import javax.persistence.Entity;

@Entity
public class Subject extends AbstractEntity {

    private String name;
    private Integer year;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }

}
