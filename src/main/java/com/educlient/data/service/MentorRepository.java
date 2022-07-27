package com.educlient.data.service;

import com.educlient.data.entity.Mentor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {

}