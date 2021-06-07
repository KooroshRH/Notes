package com.example.notes.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.notes.R
import com.example.notes.model.Card
import com.example.notes.model.Folder
import com.example.notes.model.Note
import com.example.notes.utils.NoteHolder
import com.example.notes.utils.ObjectBox
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
    private var isOptionMenuOpened: Boolean = false

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
        if (!isFolderOpened) loadMainMenu() else openFolder(openedFolder)
        closeOptionsCallback()
    }

    private fun setupView()
    {
        loadMainMenu()
        closeOptionsCallback()
        mainFloatingActionButton.setOnClickListener { openOptionsCallback() }
        newNoteFloatingActionButton.setOnClickListener {
            val note = Note(0, "", "", Calendar.getInstance().time.time)
            if (isFolderOpened)
            {
                note.folder.setAndPutTarget(openedFolder)
                openedFolder = ObjectBox.store.boxFor(Folder::class.java).get(openedFolder.id)
            }
            openTextEditor(note)
        }
        optionsButton.setOnTouchListener { _, p1 ->
            if (!isOptionMenuOpened) openFolderOptionsMenu(openedFolder, p1.x, p1.y)
            true
        }
        newFolderFloatingActionButton.setOnClickListener { openNewFolderPopup() }
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun loadMainMenu()
    {
        val cardListAdapter = CardListAdapter(applicationContext,
            R.layout.main_list_card, ::openTextEditor, ::openFolder)
        for (note in ObjectBox.store.boxFor(Note::class.java).all)
        {
            if (!note.folder.isNull) continue
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
        if (!isFolderOpened) newFolderFloatingActionButton.visibility = View.VISIBLE
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
        val cardListAdapter = CardListAdapter(applicationContext,
            R.layout.main_list_card, ::openTextEditor, ::openFolder)
        for (note in folder.notes)
        {
            cardListAdapter.add(Card("" + (Calendar.getInstance().time.time - note.lastTimeModified) / 60000 + " دقیقه پیش", note.title, Card.CardType.NOTE, note.id))
        }
        findViewById<ListView>(R.id.mainList).adapter = cardListAdapter
    }

    override fun onBackPressed() {
        if (!isFolderOpened) super.onBackPressed()
        toolbarTitle.text = "یادداشت‌ها"
        isFolderOpened = false
        backButton.visibility = View.INVISIBLE
        optionsButton.visibility = View.INVISIBLE
        loadMainMenu()
    }

    private fun openFolderOptionsMenu(folder: Folder, x: Float, y: Float)
    {
        isOptionMenuOpened = true
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.option_popup, null)
        val popupWindow = PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(backButton.rootView, Gravity.NO_GRAVITY, x.toInt() + 30, y.toInt() + 80)
        popupWindow.contentView.findViewById<FrameLayout>(R.id.renameButton).setOnClickListener {
            popupWindow.dismiss()
            fakeBkg.visibility = View.VISIBLE
            openRenameFolderPopup()
        }
        popupWindow.contentView.findViewById<FrameLayout>(R.id.deleteButton).setOnClickListener {
            popupWindow.dismiss()
            fakeBkg.visibility = View.VISIBLE
            mainFloatingActionButton.visibility = View.INVISIBLE
            openConfirmPopup(folder, ::deleteFolder)
        }
        popupWindow.setOnDismissListener { isOptionMenuOpened = false }
    }

    private fun openRenameFolderPopup()
    {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.general_popup, null)
        val popupWindow = PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(popupWindow.contentView, Gravity.CENTER, 0, 0)
        newFolderFloatingActionButton.visibility = View.INVISIBLE
        newNoteFloatingActionButton.visibility = View.INVISIBLE
        mainFloatingActionButton.visibility = View.INVISIBLE
        popupWindow.contentView.findViewById<TextView>(R.id.popupDescription).visibility = View.GONE
        popupWindow.contentView.findViewById<TextView>(R.id.popupTitle).text = "تغییر عنوان پوشه"
        val mainButton = popupWindow.contentView.findViewById<Button>(R.id.popupMainButton)
        val popupEditText = popupWindow.contentView.findViewById<EditText>(R.id.popupInput)
        popupWindow.contentView.findViewById<TextView>(R.id.cancelButton).setOnClickListener { popupWindow.dismiss() }
        popupEditText.setText(openedFolder.title)
        mainButton.text = "ذخیره"
        mainButton.setOnClickListener {
            if (popupEditText.text.trim().isNotEmpty())
            {
                openedFolder.title = popupEditText.text.toString()
                ObjectBox.store.boxFor(Folder::class.java).put(openedFolder)
                popupWindow.dismiss()
                openFolder(openedFolder)
            }
        }
        popupWindow.setOnDismissListener {
            closeOptionsCallback()
            mainFloatingActionButton.visibility = View.VISIBLE
        }
    }

    private fun openConfirmPopup(folder: Folder, mainCallback: (folder: Folder) -> Unit)
    {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.general_popup, null)
        val popupWindow = PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(popupWindow.contentView, Gravity.CENTER, 0, 0)
        popupWindow.contentView.findViewById<TextView>(R.id.popupDescription).text = "آیا از حذف این پوشه اطمینان دارید؟"
        popupWindow.contentView.findViewById<TextView>(R.id.popupTitle).text = "حذف پوشه"
        popupWindow.contentView.findViewById<TextView>(R.id.cancelButton).setOnClickListener { popupWindow.dismiss() }
        popupWindow.contentView.findViewById<EditText>(R.id.popupInput).visibility = View.GONE
        val mainButton = popupWindow.contentView.findViewById<Button>(R.id.popupMainButton)
        mainButton.text = "حذف"
        mainButton.setOnClickListener {
            popupWindow.dismiss()
            mainCallback(folder)
        }
        popupWindow.setOnDismissListener {
            fakeBkg.visibility = View.INVISIBLE
            mainFloatingActionButton.visibility = View.VISIBLE
        }
    }

    private fun deleteFolder(folder: Folder)
    {
        for (note in folder.notes)
        {
            ObjectBox.store.boxFor(Note::class.java).remove(note)
        }
        ObjectBox.store.boxFor(Folder::class.java).remove(folder)
        onBackPressed()
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
