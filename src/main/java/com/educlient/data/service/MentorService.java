package com.educlient.data.service;

import com.educlient.data.entity.Mentor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

;

@Service
public class MentorService {

    private final MentorRepository repository;

    @Autowired
    public MentorService(MentorRepository repository) {
        this.repository = repository;
    }

    public Optional<Mentor> get(Long id) {
        return repository.findById(id);
    }

    public Mentor update(Mentor entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Mentor> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
