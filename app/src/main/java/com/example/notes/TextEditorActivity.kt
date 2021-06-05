package com.example.notes

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.model.Note
import com.example.notes.utils.NoteHolder
import com.example.notes.utils.ObjectBox
import java.util.*

class TextEditorActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var titleTextEditor: EditText
    private lateinit var descriptionTextEditor: EditText
    private lateinit var dateText: TextView
    private lateinit var optionsButton: ImageButton
    private lateinit var note: Note
    private var isOptionMenuOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        backButton = findViewById(R.id.textEditorBackButton)
        titleTextEditor = findViewById(R.id.titleTextEditor)
        descriptionTextEditor = findViewById(R.id.descriptionTextEditor)
        dateText = findViewById(R.id.dateText)
        optionsButton = findViewById(R.id.textEditorOptionButton)
        note = NoteHolder.note
        setupView()
    }

    private fun setupView()
    {
        backButton.setOnClickListener { onBackPressed() }
        optionsButton.setOnTouchListener { _, p1 ->
            if (!isOptionMenuOpened) openNoteOptionsMenu(p1.x, p1.y)
            true
        }
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

    private fun openNoteOptionsMenu(x: Float, y: Float) {
        isOptionMenuOpened = true
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.option_popup, null)
        val popupWindow = PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(backButton.rootView, Gravity.NO_GRAVITY, x.toInt() + 30, y.toInt() + 80)
        popupWindow.contentView.findViewById<FrameLayout>(R.id.renameButton).visibility = View.GONE
        popupWindow.contentView.findViewById<FrameLayout>(R.id.deleteButton).setOnClickListener {
            titleTextEditor.setText("")
            deleteNote(note)
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener { isOptionMenuOpened = false }
    }

    private fun deleteNote(note: Note)
    {
        ObjectBox.store.boxFor(Note::class.java).remove(note)
        onBackPressed()
    }
}