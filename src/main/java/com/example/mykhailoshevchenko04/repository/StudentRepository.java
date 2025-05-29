package com.example.mykhailoshevchenko04.repository;

import com.example.mykhailoshevchenko04.entity.StudentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    //ALTER SEQUENCE students_id_seq OWNED BY students.id  RESTART IDENTITY CASCADE;
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE students;", nativeQuery = true)
    void truncate();

    StudentEntity findStudentEntityByEmail(String email);

}
