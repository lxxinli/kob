package com.kob.matchingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        IpAddressMatcher hasIpAddress = new IpAddressMatcher("127.0.0.1");
        http.csrf(csrf -> csrf.disable()) // 禁用 CSRF 保护
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 配置无状态会话
                .authorizeHttpRequests(auth -> auth // 配置授权请求
                        .requestMatchers("/player/add/", "/player/remove/").access((authentication, context) ->
                                new AuthorizationDecision(hasIpAddress.matches(context.getRequest()))) // 使用 SpEL 表达式限制 IP 地址
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // 允许所有 OPTIONS 请求
                        .anyRequest().authenticated()); // 其他请求需要身份验证

        return http.build();
    }
}



