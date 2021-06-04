package com.example.notes.model

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class Folder (
    @Id var id: Long = 0,
    var title: String? = null
) {
    @Backlink(to = "folder")
    lateinit var notes: ToMany<Note>
}