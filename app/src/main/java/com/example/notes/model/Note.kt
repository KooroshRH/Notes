package com.example.notes.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Note (
    @Id
    var id: Long = 0,
    private var title: String? = null,
    private var text: String? = null
)