package com.refengSGL.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("/test")
@RestController
public class HelloController {
    /**
     * 测试服务器是否正常启动
     */
    @GetMapping("/helloWorld")
    public Object sayHelloWorld() {
        return "Hello World";
    }
}
