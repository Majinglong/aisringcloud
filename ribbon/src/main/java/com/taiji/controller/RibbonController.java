package com.taiji.controller;

import com.netflix.discovery.converters.Auto;
import com.taiji.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@RestController
@RequestMapping("/ribbon")
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findAll")
    public Collection<Student> findAll(){
        return restTemplate.getForObject("http://provider/student/findALL",Collection.class);
    }

    @GetMapping("/index")
    public String index(){
        return  restTemplate.getForObject("http://provider/student/index",String.class);
    }
}
