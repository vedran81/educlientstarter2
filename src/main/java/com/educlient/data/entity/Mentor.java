package com.educlient.data.entity;

import javax.persistence.Entity;

@Entity
public class Mentor extends AbstractEntity {

    private String lastName;
    private String firstName;
    private String email;

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
