package com.taiji.controller;

import com.netflix.discovery.converters.Auto;
import com.taiji.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findAll")
    public Collection<Student> findAll() {
        return restTemplate.getForObject("http://localhost:8010/student/findALL", Collection.class);
    }
    @GetMapping("/findAll2")
    public Collection<Student> findAll2() {
        return restTemplate.getForEntity("http://localhost:8010/student/findALL", Collection.class).getBody();
    }
    @GetMapping("/findById/{id}")
    public Student findById(@PathVariable("id") long id){
        return  restTemplate.getForObject("http://localhost:8010/student/findById/{id}",Student.class,id);
    }
    @GetMapping("/findById2/{id}")
    public Student findById2(@PathVariable("id") long id){
        return  restTemplate.getForEntity("http://localhost:8010/student/findById/{id}",Student.class,id).getBody();
    }
    @PostMapping("/save")
    public void save(@RequestBody Student student){
        restTemplate.postForEntity("http://localhost:8010/student/save",student,null);
    }
    @PostMapping("/save2")
    public void save2(@RequestBody Student student){
        restTemplate.postForObject("http://localhost:8010/student/save",student,null);
    }
    @PutMapping("/update")
    public void update(@RequestBody Student student){
        restTemplate.put("http://localhost:8010/student/update",student);
    }
    @PutMapping("/update2")
    public void update2(@RequestBody Student student){
        restTemplate.put("http://localhost:8010/student/update",student);
    }
    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable("id") long id ){
        restTemplate.delete("http://localhost:8010/student/delete/{id}",id);
    }
}
