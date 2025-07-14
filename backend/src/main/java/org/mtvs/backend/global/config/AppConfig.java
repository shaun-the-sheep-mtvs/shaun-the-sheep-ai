package org.mtvs.backend.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "org.mtvs.backend.aspect")
@EnableAspectJAutoProxy
public class AppConfig {
}
