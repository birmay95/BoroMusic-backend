package com.baravenski.musicplatform.core.audit;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@NullMarked
@Configuration
@EnableJpaAuditing
public class AuditConfig {
}
