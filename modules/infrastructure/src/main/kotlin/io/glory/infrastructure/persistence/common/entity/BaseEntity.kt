package io.glory.infrastructure.persistence.common.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy

@MappedSuperclass
abstract class BaseEntity(
    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    var createdBy: String? = null,

    @LastModifiedBy
    @Column(name = "modified_by", length = 50)
    var modifiedBy: String? = null,
) : BaseTimeEntity()
