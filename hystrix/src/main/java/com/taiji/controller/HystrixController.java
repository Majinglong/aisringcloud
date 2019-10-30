package com.taiji.controller;

import com.taiji.entity.Student;
import com.taiji.feign.FeignProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @Autowired
    private FeignProviderClient client;

    @GetMapping("/findAll")
    public Collection<Student> findAll(){
        return client.getAll();
    }

    @GetMapping("/index")
    public String index(){
        return client.index();
    }

}
