package com.taiji.controller;

import com.taiji.entity.Student;
import com.taiji.feign.FeignProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/feign")
public class FeignHandler {

    @Autowired
    private FeignProviderClient client;

    @GetMapping("/findAll")
    public Collection<Student> getAll(){
        return  client.getAll();
    }
    @GetMapping("/index")
    public String index(){
        return client.index();
    }
}
