package com.example.notes

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.notes.model.Card
import com.example.notes.model.Folder
import com.example.notes.model.Note
import com.example.notes.utils.NoteHolder
import com.example.notes.utils.ObjectBox
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mainFloatingActionButton: FloatingActionButton
    private lateinit var newFolderFloatingActionButton: FloatingActionButton
    private lateinit var newNoteFloatingActionButton: FloatingActionButton
    private lateinit var optionsButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var fakeBkg: ImageView
    private lateinit var toolbarTitle: TextView

    private lateinit var openedFolder: Folder
    private var isFolderOpened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ObjectBox.init(this)
        setContentView(R.layout.activity_main)
        mainFloatingActionButton = findViewById(R.id.fab)
        newFolderFloatingActionButton = findViewById(R.id.fabNewFolder)
        newNoteFloatingActionButton = findViewById(R.id.fabNewNote)
        optionsButton = findViewById(R.id.mainActivityOptionButton)
        backButton = findViewById(R.id.mainBackButton)
        fakeBkg = findViewById(R.id.fakeBkg)
        toolbarTitle = findViewById(R.id.toolbarText)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        loadMainMenu()
        closeOptionsCallback()
    }

    private fun setupView()
    {
        loadMainMenu()
        closeOptionsCallback()
        mainFloatingActionButton.setOnClickListener { openOptionsCallback() }
        newNoteFloatingActionButton.setOnClickListener { openTextEditor(Note(0, "", "", Calendar.getInstance().time.time))}
        newFolderFloatingActionButton.setOnClickListener { openNewFolderPopup() }
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun loadMainMenu()
    {
        val cardListAdapter = CardListAdapter(applicationContext, R.layout.main_list_card, ::openTextEditor, ::openFolder)
        for (note in ObjectBox.store.boxFor(Note::class.java).all)
        {
            cardListAdapter.add(Card("" + (Calendar.getInstance().time.time - note.lastTimeModified) / 60000 + " دقیقه پیش", note.title, Card.CardType.NOTE, note.id))
        }
        for (folder in ObjectBox.store.boxFor(Folder::class.java).all)
        {
            cardListAdapter.add(Card("حاوی " + folder.notes.size + " یادداشت", folder.title, Card.CardType.FOLDER, folder.id))
        }
        findViewById<ListView>(R.id.mainList).adapter = cardListAdapter
    }

    private fun openOptionsCallback()
    {
        newFolderFloatingActionButton.visibility = View.VISIBLE
        newNoteFloatingActionButton.visibility = View.VISIBLE
        fakeBkg.visibility = View.VISIBLE
        mainFloatingActionButton.setOnClickListener { closeOptionsCallback() }
    }

    private fun closeOptionsCallback()
    {
        newFolderFloatingActionButton.visibility = View.INVISIBLE
        newNoteFloatingActionButton.visibility = View.INVISIBLE
        fakeBkg.visibility = View.INVISIBLE
        mainFloatingActionButton.setOnClickListener { openOptionsCallback() }
    }

    private fun openTextEditor(note: Note)
    {
        val intent = Intent(this, TextEditorActivity::class.java)
        NoteHolder.note = note
        startActivity(intent)
    }

    private fun openFolder(folder: Folder)
    {
        openedFolder = folder
        isFolderOpened = true
        toolbarTitle.text = folder.title
        backButton.visibility = View.VISIBLE
        optionsButton.visibility = View.VISIBLE
        val cardListAdapter = CardListAdapter(applicationContext, R.layout.main_list_card, ::openTextEditor, ::openFolder)
        for (note in folder.notes)
        {
            cardListAdapter.add(Card("" + (Calendar.getInstance().time.time - note.lastTimeModified) / 60000 + " دقیقه پیش", note.title, Card.CardType.NOTE, note.id))
        }
        findViewById<ListView>(R.id.mainList).adapter = cardListAdapter
    }

    override fun onBackPressed() {
        toolbarTitle.text = "یادداشت‌ها"
        isFolderOpened = false
        backButton.visibility = View.INVISIBLE
        optionsButton.visibility = View.INVISIBLE
        loadMainMenu()
    }

    private fun openNewFolderPopup()
    {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.general_popup, null)
        val popupWindow = PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(popupWindow.contentView, Gravity.CENTER, 0, 0)
        newFolderFloatingActionButton.visibility = View.INVISIBLE
        newNoteFloatingActionButton.visibility = View.INVISIBLE
        mainFloatingActionButton.visibility = View.INVISIBLE
        popupWindow.contentView.findViewById<TextView>(R.id.popupTitle).text = "پوشه جدید"
        popupWindow.contentView.findViewById<TextView>(R.id.popupDescription).text = "برای پوشه جدید عنوان بنویسید."
        val mainButton = popupWindow.contentView.findViewById<Button>(R.id.popupMainButton)
        val popupEditText = popupWindow.contentView.findViewById<EditText>(R.id.popupInput)
        popupWindow.contentView.findViewById<TextView>(R.id.cancelButton).setOnClickListener { popupWindow.dismiss() }
        mainButton.text = "ایجاد پوشه"
        mainButton.setOnClickListener {
            if (popupEditText.text.trim().isNotEmpty())
            {
                val folder = Folder(0, popupEditText.text.trim().toString())
                ObjectBox.store.boxFor(Folder::class.java).put(folder)
                popupWindow.dismiss()
                loadMainMenu()
            }
        }
        popupWindow.setOnDismissListener {
            closeOptionsCallback()
            mainFloatingActionButton.visibility = View.VISIBLE
        }
    }
}
