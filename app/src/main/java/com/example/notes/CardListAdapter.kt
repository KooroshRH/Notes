package com.example.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.notes.model.Card

class CardListAdapter(context: Context, resource: Int) : ArrayAdapter<Card>(context, resource) {
    private var cardList: ArrayList<Card> = ArrayList()

    class CardViewHolder(
        var titleText: TextView,
        var descriptionText: TextView
    )

    override fun add(`object`: Card?) {
        if (`object` != null) {
            cardList.add(`object`)
        }
        super.add(`object`)
    }

    override fun getCount(): Int {
        return cardList.size
    }

    override fun getItem(position: Int): Card {
        return cardList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row: View? = convertView
        val viewHolder: CardViewHolder
        if (row == null)
        {
            val inflater: LayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(R.layout.main_list_card, parent, false)
            viewHolder = CardViewHolder(row.findViewById(R.id.titleText), row.findViewById(R.id.descriptionText))
            row.tag = viewHolder
        }
        else
        {
            viewHolder = row.tag as CardViewHolder
        }
        val card: Card? = getItem(position)
        if (card != null) {
            viewHolder.titleText.text = card.title
            viewHolder.descriptionText.text = card.description
            val folderOuterIcon: ImageView? = row?.findViewById(R.id.cardFolderModeIconOuter)
            val folderInnerIcon: ImageView? = row?.findViewById(R.id.cardFolderModeIconInner)
            val noteOuterIcon: ImageView? = row?.findViewById(R.id.cardNoteModeIconOuter)
            val noteInnerIcon: ImageView? = row?.findViewById(R.id.cardNoteModeIconInner)
            if (card.type == Card.CardType.NOTE)
            {
                folderOuterIcon?.visibility ?: View.INVISIBLE
                folderInnerIcon?.visibility ?: View.INVISIBLE
                noteInnerIcon?.visibility ?: View.VISIBLE
                noteOuterIcon?.visibility ?: View.VISIBLE
            }
            else
            {
                folderOuterIcon?.visibility ?: View.VISIBLE
                folderOuterIcon?.visibility ?: View.VISIBLE
                noteInnerIcon?.visibility ?: View.INVISIBLE
                noteOuterIcon?.visibility ?: View.INVISIBLE
            }
        }
        return row!!
    }
}