package com.educlient.data.service;

import com.educlient.data.entity.Subject;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

}