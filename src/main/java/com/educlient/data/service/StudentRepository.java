package com.educlient.data.service;

import com.educlient.data.entity.Student;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, UUID> {

}