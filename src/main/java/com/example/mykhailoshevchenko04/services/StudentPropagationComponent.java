package com.example.mykhailoshevchenko04.services;

import com.example.mykhailoshevchenko04.entity.StudentEntity;
import com.example.mykhailoshevchenko04.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudentPropagationComponent {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertPropagationRequired(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StudentEntity findByIdPropagationRequired(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void insertPropagationSupports(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public StudentEntity findByIdPropagationSupports(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void insertPropagationMandatory(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public StudentEntity findByIdPropagationMandatory(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertPropagationRequiresNew(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StudentEntity findByIdPropagationRequiresNew(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertPropagationNotSupported(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public StudentEntity findByIdPropagationNotSupported(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    @Transactional(propagation = Propagation.NEVER)
    public void insertPropagationNever(StudentEntity student, boolean isCorrect) {
        studentRepository.save(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    public StudentEntity findByIdPropagationNever(Long id, boolean isCorrect) {
        StudentEntity res = studentRepository.findById(id).orElse(null);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

    // use entity manager, because jpa does not support nested
    @Transactional(propagation = Propagation.NESTED)
    public void insertPropagationNested(StudentEntity student, boolean isCorrect) {
        entityManager.persist(student);
        if(!isCorrect) {
            throw new RuntimeException();
        }
    }

    // use entity manager, because jpa does not support nested
    @Transactional(propagation = Propagation.NESTED)
    public StudentEntity findByIdPropagationNested(Long id, boolean isCorrect) {
        StudentEntity res = entityManager.find(StudentEntity.class, id);
        if(!isCorrect) {
            throw new RuntimeException();
        }
        return res;
    }

}
