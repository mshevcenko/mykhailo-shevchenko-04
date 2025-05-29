package com.example.mykhailoshevchenko04.services;

import com.example.mykhailoshevchenko04.entity.StudentEntity;
import com.example.mykhailoshevchenko04.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudentIsolationComponent {

    @Autowired
    private StudentRepository studentRepository;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void insertIsolationReadUncommitted(StudentEntity student, long millis1, long millis2, boolean isCorrect) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        studentRepository.save(student);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Pair<StudentEntity, StudentEntity> findByIdIsolationReadUncommitted(Long id, long millis1, long millis2) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        StudentEntity student1 = studentRepository.findById(id).orElse(null);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        StudentEntity student2 = studentRepository.findById(id).orElse(null);
        return new Pair<>(student1, student2);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertIsolationReadCommitted(StudentEntity student, long millis1, long millis2, boolean isCorrect) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        studentRepository.save(student);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Pair<StudentEntity, StudentEntity> findByIdIsolationReadCommitted(Long id, long millis1, long millis2) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        StudentEntity student1 = studentRepository.findById(id).orElse(null);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        StudentEntity student2 = studentRepository.findById(id).orElse(null);
        return new Pair<>(student1, student2);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void insertIsolationRepeatableRead(StudentEntity student, long millis1, long millis2, boolean isCorrect) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        studentRepository.save(student);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Pair<StudentEntity, StudentEntity> findByIdIsolationSerializable(Long id, long millis1, long millis2) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        StudentEntity student1 = studentRepository.findById(id).orElse(null);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        StudentEntity student2 = studentRepository.findById(id).orElse(null);
        return new Pair<>(student1, student2);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void insertIsolationSerializable(StudentEntity student, long millis1, long millis2, boolean isCorrect) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        studentRepository.save(student);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Pair<StudentEntity, StudentEntity> findByIdIsolationRepeatableRead(Long id, long millis1, long millis2) {
        try {
            Thread.sleep(millis1);
        }
        catch (Exception e) {}
        StudentEntity student1 = studentRepository.findById(id).orElse(null);
        try {
            Thread.sleep(millis2);
        }
        catch (Exception e) {}
        StudentEntity student2 = studentRepository.findById(id).orElse(null);
        return new Pair<>(student1, student2);
    }

}
