package com.example.scan_machine_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController                      // lớp này trả JSON/chuỗi cho client
public class PingController {

    @GetMapping("/api/ping")         // GET http://.../api/ping sẽ gọi hàm này
    public String ping() {
        return "ok";
    }
}