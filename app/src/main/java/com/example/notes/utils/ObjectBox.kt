package com.example.notes.utils

import android.content.Context
import com.example.notes.model.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    lateinit var store: BoxStore
    var isInitialized: Boolean = false

    fun init(context: Context) {
        if (!isInitialized)
        {
            store = MyObjectBox.builder()
                .androidContext(context.applicationContext)
                .build()
            isInitialized = true
        }
    }
}