package com.example.userservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class TestController {
    @GetMapping("")
    public String test(){
        return "Hello";
    }

    @PostMapping(value = "/specific")
    public String testAop(@RequestHeader String role){
        return "You are admin";
    }
}
