package com.taiji.feign.impl;

import com.taiji.entity.Student;
import com.taiji.feign.FeignProviderClient;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class FeignError implements FeignProviderClient {
    @Override
    public Collection<Student> getAll() {
        return null;
    }
    @Override
    public String index() {
        return "服务器维护中";
    }
}
