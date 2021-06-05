package com.example.notes

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.model.Note
import com.example.notes.utils.NoteHolder
import com.example.notes.utils.ObjectBox
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class TextEditorActivity : AppCompatActivity() {
    lateinit var backButton: ImageButton
    lateinit var titleTextEditor: EditText
    lateinit var descriptionTextEditor: EditText
    lateinit var dateText: TextView
    lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        backButton = findViewById(R.id.textEditorBackButton)
        titleTextEditor = findViewById(R.id.titleTextEditor)
        descriptionTextEditor = findViewById(R.id.descriptionTextEditor)
        dateText = findViewById(R.id.dateText)
        note = NoteHolder.note
        setupView()
    }

    private fun setupView()
    {
        backButton.setOnClickListener { onBackPressed() }
        titleTextEditor.setText(note.title?.trim(), TextView.BufferType.EDITABLE)
        descriptionTextEditor.setText(note.text?.trim(), TextView.BufferType.EDITABLE)
        val now = Calendar.getInstance()
        dateText.text = now.get(Calendar.DAY_OF_MONTH).toString() + "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.YEAR)
    }

    override fun onBackPressed() {
        if (titleTextEditor.text.toString().trim().isNotEmpty())
        {
            note.title = titleTextEditor.text.toString()
            note.text = descriptionTextEditor.text.toString()
            ObjectBox.store.boxFor(Note::class.java).put(note)
        }
        super.onBackPressed()
    }
}