package com.educlient.data.generator;

import com.educlient.data.entity.Mentor;
import com.educlient.data.entity.Student;
import com.educlient.data.entity.Subject;
import com.educlient.data.service.MentorRepository;
import com.educlient.data.service.StudentRepository;
import com.educlient.data.service.SubjectRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(StudentRepository studentRepository, MentorRepository mentorRepository,
            SubjectRepository subjectRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (studentRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Student entities...");
            ExampleDataGenerator<Student> studentRepositoryGenerator = new ExampleDataGenerator<>(Student.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            studentRepositoryGenerator.setData(Student::setLastName, DataType.LAST_NAME);
            studentRepositoryGenerator.setData(Student::setFirstName, DataType.FIRST_NAME);
            studentRepositoryGenerator.setData(Student::setEmail, DataType.EMAIL);
            studentRepositoryGenerator.setData(Student::setDateOfBirth, DataType.DATE_LAST_10_YEARS);
            studentRepositoryGenerator.setData(Student::setStatus, DataType.WORD);
            studentRepositoryGenerator.setData(Student::setStudyYear, DataType.NUMBER_UP_TO_10);
            studentRepositoryGenerator.setData(Student::setMentorId, DataType.NUMBER_UP_TO_10);
            studentRepository.saveAll(studentRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Mentor entities...");
            ExampleDataGenerator<Mentor> mentorRepositoryGenerator = new ExampleDataGenerator<>(Mentor.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            mentorRepositoryGenerator.setData(Mentor::setLastName, DataType.WORD);
            mentorRepositoryGenerator.setData(Mentor::setFirstName, DataType.WORD);
            mentorRepositoryGenerator.setData(Mentor::setEmail, DataType.WORD);
            mentorRepository.saveAll(mentorRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Subject entities...");
            ExampleDataGenerator<Subject> subjectRepositoryGenerator = new ExampleDataGenerator<>(Subject.class,
                    LocalDateTime.of(2022, 7, 27, 0, 0, 0));
            subjectRepositoryGenerator.setData(Subject::setName, DataType.WORD);
            subjectRepositoryGenerator.setData(Subject::setYear, DataType.NUMBER_UP_TO_10);
            subjectRepository.saveAll(subjectRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}