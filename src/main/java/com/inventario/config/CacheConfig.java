package com.inventario.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Configuración de caché habilitada mediante @EnableCaching
    // La configuración específica está en application.yml y ehcache.xml
}
