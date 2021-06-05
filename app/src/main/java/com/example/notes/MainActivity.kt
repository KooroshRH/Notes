package com.example.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.example.notes.model.Card
import com.example.notes.model.Folder
import com.example.notes.model.Note
import com.example.notes.utils.ObjectBox
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mainFloatingActionButton: FloatingActionButton
    private lateinit var newFolderFloatingActionButton: FloatingActionButton
    private lateinit var newNoteFloatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ObjectBox.init(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mainFloatingActionButton = findViewById(R.id.fab)
        newFolderFloatingActionButton = findViewById(R.id.fabNewFolder)
        newNoteFloatingActionButton = findViewById(R.id.fabNewNote)
        setupView()
    }

    private fun setupView()
    {
        loadMainMenu()
        mainFloatingActionButton.setOnClickListener { openOptionsCallback() }
        newNoteFloatingActionButton.setOnClickListener { openTextEditor(Note(0, "سلام", "adsf", 0))}
    }

    private fun loadMainMenu()
    {
        val cardListAdapter = CardListAdapter(applicationContext, R.layout.main_list_card)
        for (note in ObjectBox.store.boxFor(Note::class.java).all)
        {
            cardListAdapter.add(Card("" + (Calendar.getInstance().time.time - note.lastTimeModified) / 60 + " دقیقه پیش", note.title, Card.CardType.NOTE))
        }
        for (folder in ObjectBox.store.boxFor(Folder::class.java).all)
        {
            cardListAdapter.add(Card("حاوی " + folder.notes.size + " یادداشت", folder.title, Card.CardType.FOLDER))
        }
        findViewById<ListView>(R.id.mainList).adapter = cardListAdapter
    }

    private fun openOptionsCallback()
    {
        newFolderFloatingActionButton.visibility = View.VISIBLE
        newNoteFloatingActionButton.visibility = View.VISIBLE
        mainFloatingActionButton.setOnClickListener { closeOptionsCallback() }
    }

    private fun closeOptionsCallback()
    {
        newFolderFloatingActionButton.visibility = View.INVISIBLE
        newNoteFloatingActionButton.visibility = View.INVISIBLE
        mainFloatingActionButton.setOnClickListener { openOptionsCallback() }
    }

    private fun openTextEditor(note: Note)
    {
        startActivity(Intent(this, TextEditorActivity::class.java))
    }
}
