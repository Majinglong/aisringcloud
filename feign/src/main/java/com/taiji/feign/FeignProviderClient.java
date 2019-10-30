package com.taiji.feign;

import com.taiji.entity.Student;
import com.taiji.feign.impl.FeignError;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@FeignClient(value = "provider",fallback = FeignError.class)//声明式不需要写实现，直接用注解。这里写要调用的服务名
public interface FeignProviderClient {
    @GetMapping("/student/findALL")//这里写要调用服务的路径
    public Collection<Student> getAll();
    @GetMapping("/student/index")
    public String index();
}
