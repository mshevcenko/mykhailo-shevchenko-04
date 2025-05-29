package com.example.mykhailoshevchenko04;
import com.example.mykhailoshevchenko04.entity.StudentEntity;
import com.example.mykhailoshevchenko04.repository.StudentRepository;
import com.example.mykhailoshevchenko04.services.StudentPropagationComponent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:tc:mysql:8.0.33:///test"
})
public class PropagationTest {

    @Autowired
    private StudentPropagationComponent propagationComponent;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @AfterEach
    public void truncateTables() {
        studentRepository.truncate();
    }

    // test that shows it executes without external transaction
    @Test
    public void testPropagationRequired1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationRequired(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationRequired creates transaction
    @Test
    public void testPropagationRequired2() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationRequired(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationRequired3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            propagationComponent.insertPropagationRequired(student1, true);
            studentRepository.save(student2);
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationRequired4() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student2);
                try {
                    propagationComponent.insertPropagationRequired(student1, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationRequired5() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                try {
                    propagationComponent.findByIdPropagationRequired(1L, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it executes without external transaction
    @Test
    public void testPropagationSupports1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationSupports(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationSupports doesn't create transaction
    @Test
    public void testPropagationSupports2() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationSupports(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationSupports3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            propagationComponent.insertPropagationSupports(student1, true);
            studentRepository.save(student2);
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationSupports4() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student2);
                try {
                    propagationComponent.insertPropagationSupports(student1, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationSupports5() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                try {
                    propagationComponent.findByIdPropagationSupports(1L, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows PropagationMandatory requires transaction
    @Test
    public void testPropagationMandatory1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            propagationComponent.insertPropagationMandatory(student, true);
        });
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationMandatory2() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            propagationComponent.insertPropagationSupports(student1, true);
            studentRepository.save(student2);
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationMandatory3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student2);
                try {
                    propagationComponent.insertPropagationSupports(student1, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows PropagationMandatory requires transaction
    @Test
    public void testPropagationMandatory4() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            propagationComponent.findByIdPropagationMandatory(1L, true);
        });
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationMandatory5() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                try {
                    propagationComponent.findByIdPropagationSupports(1L, false);
                }
                catch (Exception e) {}
                return null;
            });
        }
        catch (UnexpectedRollbackException e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it executes without external transaction
    @Test
    public void testPropagationNever1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationNever(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationNever doesn't create transaction
    @Test
    public void testPropagationNever2() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationNever(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it can't work with transaction
    @Test
    public void testPropagationNever3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                propagationComponent.insertPropagationNever(student2, true);
                return null;
            });
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it can't work with transaction
    @Test
    public void testPropagationNever4() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        Assertions.assertThrows(IllegalTransactionStateException.class, () -> {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                propagationComponent.findByIdPropagationNever(1L, true);
                return null;
            });
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it executes without external transaction
    @Test
    public void testPropagationNotSupported1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationNotSupported(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationNotSupported doesn't create transaction
    @Test
    public void testPropagationNotSupported2() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationNotSupported(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationNotSupported3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            propagationComponent.insertPropagationNotSupported(student1, true);
            studentRepository.save(student2);
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it DOESN'T use external transaction
    @Test
    public void testPropagationNotSupported4() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            studentRepository.save(student1);
            try {
                propagationComponent.insertPropagationNotSupported(student2, false);
            }
            catch (Exception e) {}
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it DOESN'T use external transaction
    @Test
    public void testPropagationNotSupported5() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        transactionTemplate.execute(status -> {
            studentRepository.save(student1);
            try {
                propagationComponent.findByIdPropagationNotSupported(1L, false);
            }
            catch (Exception e) {}
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it executes without external transaction
    @Test
    public void testPropagationRequiresNew1() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationRequiresNew(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationRequiresNew creates transaction
    @Test
    public void testPropagationRequiresNew2() {
        StudentEntity student = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationRequiresNew(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationRequiresNew3() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            propagationComponent.insertPropagationRequiresNew(student1, true);
            studentRepository.save(student2);
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows it DOESN'T use external transaction
    @Test
    public void testPropagationRequiresNew4() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        transactionTemplate.execute(status -> {
            studentRepository.save(student2);
            try {
                propagationComponent.insertPropagationRequiresNew(student1, false);
            }
            catch (Exception e) {}
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it creates new transaction
    @Test
    public void testPropagationRequiresNew5() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(2L, "test2@gmail.com", "test2", "test2");
        try {
            transactionTemplate.execute(status -> {
                studentRepository.save(student1);
                propagationComponent.insertPropagationRequiresNew(student2, true);
                throw new RuntimeException();
            });
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
        StudentEntity studentFromDb = studentRepository.findById(2L).get();
        Assertions.assertEquals(student2, studentFromDb);
    }

    // test that shows it DOESN'T use external transaction
    @Test
    public void testPropagationRequiresNew6() {
        StudentEntity student1 = new StudentEntity(1L, "test1@gmail.com", "test1", "test1");
        transactionTemplate.execute(status -> {
            studentRepository.save(student1);
            try {
                propagationComponent.findByIdPropagationRequiresNew(1L, false);
            }
            catch (Exception e) {}
            return null;
        });
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // use jdbc transactions because jpa does not support nested

    // test that shows it executes without external transaction
    @Test
    public void testPropagationNested1() {
        StudentEntity student = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        propagationComponent.insertPropagationNested(student, true);
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows PropagationNested creates transaction
    @Test
    public void testPropagationNested2() {
        StudentEntity student = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        try {
            propagationComponent.insertPropagationNested(student, false);
        }
        catch (Exception e) {}
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows it works with external transaction
    @Test
    public void testPropagationNested3() {
        StudentEntity student1 = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(null, "test2@gmail.com", "test2", "test2");
        try(Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            studentRepository.save(student1);
            propagationComponent.insertPropagationNested(student2, true);
            connection.commit();
        } catch (SQLException e) {
        }
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(2, students.size());
    }

    // test that shows its rollback does not affect external transaction
    @Test
    public void testPropagationNested4() {
        StudentEntity student1 = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(null, "test2@gmail.com", "test2", "test2");
        try(Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            studentRepository.save(student1);
            try {
                propagationComponent.insertPropagationNested(student2, false);
            }
            catch (Exception e) {}
            connection.commit();
        } catch (SQLException e) {
        }
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }

    // test that shows it uses external transaction
    @Test
    public void testPropagationNested5() {
        StudentEntity student1 = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        StudentEntity student2 = new StudentEntity(null, "test2@gmail.com", "test2", "test2");
        try(Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            entityManager.persist(student1);
            propagationComponent.insertPropagationNested(student2, true);
            throw new RuntimeException();
        } catch (Exception e) {
        }
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(0, students.size());
    }

    // test that shows its rollback does not affect external transaction
    @Test
    public void testPropagationNested6() {
        StudentEntity student1 = new StudentEntity(null, "test1@gmail.com", "test1", "test1");
        try(Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            studentRepository.save(student1);
            try {
                propagationComponent.findByIdPropagationNested(1L, false);
            }
            catch (Exception e) {}
            connection.commit();
        } catch (SQLException e) {
        }
        List<StudentEntity> students = studentRepository.findAll();
        Assertions.assertEquals(1, students.size());
    }


}
