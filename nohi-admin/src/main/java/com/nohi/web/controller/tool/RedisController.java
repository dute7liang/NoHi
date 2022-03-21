package com.nohi.web.controller.tool;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@RequestMapping("/test")
@RestController
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/redis")
    public void test(){
        RedisVo redisVo = new RedisVo();
        redisVo.setDate(new Date());
        redisVo.setLocalDateTime(LocalDateTime.now());
        redisVo.setLocalDate(LocalDate.now());
        redisVo.setLocalTime(LocalTime.now());
        redisTemplate.opsForValue().set("name",redisVo);
        RedisVo name = (RedisVo)redisTemplate.opsForValue().get("name");
    }

    @Data
    public static class RedisVo{
        private Date date;
        private LocalDateTime localDateTime;
        private LocalDate localDate;
        private LocalTime localTime;
    }
}
