package com.taiji.controller;

import com.taiji.entity.Student;
import com.taiji.respository.StudentRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/student")//注解上没有括号
public class StudentHandler {
    @Autowired
    private StudentRespository studentRespository;
    @Value("${server.port}")
    private String port;

    @GetMapping("/findALL")
    public Collection<Student> findAll(){
        return studentRespository.findall();
    }

    @GetMapping("/findById/{id}")
    public  Student findById(@PathVariable("id") long id){
        return  studentRespository.findById(id);
    }

    @PostMapping("/save")
    public void save(@RequestBody Student student){
        studentRespository.saveOrUpdate(student);
    }

    @PutMapping("/update")
    public void update(@RequestBody Student student){
        studentRespository.saveOrUpdate(student);
    }

    @DeleteMapping("/delete/{id}")
    public  void delete(@PathVariable("id") long id){
        studentRespository.deleteById(id);
    }

    @GetMapping("/index")
    public String index(){
        return "当前端口号："+this.port;
    }
}
