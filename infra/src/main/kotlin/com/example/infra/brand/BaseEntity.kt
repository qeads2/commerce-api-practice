package com.example.infra.brand

import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
