package com.example.notes.utils

import android.content.Context
import com.example.notes.model.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    private lateinit var store: BoxStore

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}