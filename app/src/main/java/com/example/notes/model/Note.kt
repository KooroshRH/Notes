package com.example.notes.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class Note (
    @Id
    var id: Long = 0,
    var title: String? = null,
    var text: String? = null,
    var lastTimeModified: Long = 0
) {
    lateinit var folder: ToOne<Folder>
}