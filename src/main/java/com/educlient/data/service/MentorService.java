package com.educlient.data.service;

import com.educlient.data.entity.Mentor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MentorService {

    private final MentorRepository repository;

    @Autowired
    public MentorService(MentorRepository repository) {
        this.repository = repository;
    }

    public Optional<Mentor> get(UUID id) {
        return repository.findById(id);
    }

    public Mentor update(Mentor entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Mentor> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
