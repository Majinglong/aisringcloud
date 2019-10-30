package com.taiji.respository.impl;

import com.taiji.entity.Student;
import com.taiji.respository.StudentRespository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StudentRespositoryImpl implements StudentRespository {
    private static Map<Long,Student> studnetMap;
    static {
        studnetMap = new HashMap<>();
        studnetMap.put(1L,new Student(1L,"张三",23));
        studnetMap.put(2L,new Student(2L,"李四",22));
        studnetMap.put(3L,new Student(3L,"王五",21));
    }

    @Override
    public Collection<Student> findall() {
        return studnetMap.values();
    }

    @Override
    public Student findById(long id) {
        return studnetMap.get(id);
    }

    @Override
    public void saveOrUpdate(Student student) {
        studnetMap.put(student.getId(),student);
    }

    @Override
    public void deleteById(long id) {
        studnetMap.remove(id);
    }
}
