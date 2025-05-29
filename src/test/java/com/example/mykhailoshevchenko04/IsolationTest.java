package com.example.mykhailoshevchenko04;

import com.example.mykhailoshevchenko04.entity.StudentEntity;
import com.example.mykhailoshevchenko04.repository.StudentRepository;
import com.example.mykhailoshevchenko04.services.Pair;
import com.example.mykhailoshevchenko04.services.StudentIsolationComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

// Use Mysql because Postgres does not support READ_UNCOMMITTED
@SpringBootTest(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:tc:mysql:8.0.33:///test"
})
public class IsolationTest {

    @Autowired
    private StudentIsolationComponent isolationComponent;

    @Autowired
    private StudentRepository studentRepository;
    @AfterEach
    public void truncateTables() {
        studentRepository.truncate();
    }

    //test to read uncommitted changes
    @Test
    public void testIsolationReadUncommitted1() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationReadUncommitted(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationReadUncommitted(student, 200, 200, true);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(student, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read uncommitted changes with rollback
    @Test
    public void testIsolationReadUncommitted2() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationReadUncommitted(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationReadUncommitted(student, 200, 200, false);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(student, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(0, studentsFromDB.size());
    }

    //test to read uncommitted changes
    @Test
    public void testIsolationReadCommitted1() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationReadCommitted(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationReadCommitted(student, 200, 200, true);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(null, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read committed changes, results are different within the same transaction
    @Test
    public void testIsolationReadCommitted2() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationReadCommitted(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationReadCommitted(student, 200, 0, true);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(student, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read uncommitted changes
    @Test
    public void testIsolationRepeatableRead1() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationRepeatableRead(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationRepeatableRead(student, 200, 200, true);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(null, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read committed changes, third thread shows that data changes but findByIdIsolationRepeatableRead gives the same results
    @Test
    public void testIsolationRepeatableRead2() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students1 = new Pair<>();
        Pair<StudentEntity, StudentEntity> students2 = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationRepeatableRead(1L, 100, 200);
            students1.setFirst(res.first);
            students1.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationRepeatableRead(student, 200, 0, true);
        });
        Thread thread3 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
            students2.setFirst(studentRepository.findById(1L).orElse(null));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }
            students2.setSecond(studentRepository.findById(1L).orElse(null));
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        Assertions.assertEquals(null, students1.first);
        Assertions.assertEquals(null, students1.second);
        Assertions.assertEquals(null, students2.first);
        Assertions.assertEquals(student, students2.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read uncommitted changes
    @Test
    public void testIsolationSerializable1() throws InterruptedException {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationSerializable(1L, 100, 200);
            students.setFirst(res.first);
            students.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationSerializable(student, 200, 200, true);
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assertions.assertEquals(null, students.first);
        Assertions.assertEquals(null, students.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

    //test to read committed changes, third thread shows that the data is not changed because findByIdIsolationSerializable makes concurrent calls sequentially
    @Test
    public void testIsolationSerializable2() throws InterruptedException {
        StudentEntity student = new StudentEntity(2L, "test1@gmail.com", "test1", "test1");
        Pair<StudentEntity, StudentEntity> students1 = new Pair<>();
        Pair<StudentEntity, StudentEntity> students2 = new Pair<>();
        Thread thread1 = new Thread(() -> {
            Pair<StudentEntity, StudentEntity> res = isolationComponent.findByIdIsolationSerializable(2L, 100, 200);
            students1.setFirst(res.first);
            students1.setSecond(res.second);
        });
        Thread thread2 = new Thread(() -> {
            isolationComponent.insertIsolationSerializable(student, 200, 0, true);
        });
        Thread thread3 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
            students2.setFirst(studentRepository.findById(2L).orElse(null));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }
            students2.setSecond(studentRepository.findById(2L).orElse(null));
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        Assertions.assertEquals(null, students1.first);
        Assertions.assertEquals(null, students1.second);
        Assertions.assertEquals(null, students2.first);
        Assertions.assertEquals(null, students2.second);
        List<StudentEntity> studentsFromDB = studentRepository.findAll();
        Assertions.assertEquals(1, studentsFromDB.size());
    }

}
