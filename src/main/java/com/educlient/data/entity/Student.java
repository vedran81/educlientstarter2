package com.educlient.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Student extends AbstractEntity {

    private String lastName;
    private String firstName;
    private String email;
    private LocalDate dateOfBirth;
    private String status;
    private Integer studyYear;
    private Integer mentorId;

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
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getStudyYear() {
        return studyYear;
    }
    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }
    public Integer getMentorId() {
        return mentorId;
    }
    public void setMentorId(Integer mentorId) {
        this.mentorId = mentorId;
    }

}
