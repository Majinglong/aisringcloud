package com.taiji.respository;

import com.taiji.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StudentRespository {
    public Collection<Student> findall();
    public Student findById(long id);
    public void saveOrUpdate(Student studnet   );
    public  void deleteById(long id);
}
