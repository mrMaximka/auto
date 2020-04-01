package com.mrmaximka.autoexpert.ui.quest.lists

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.model.QuestSymbol
import kotlinx.android.synthetic.main.item_quest_symbol.view.*

class LettersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var model: QuestSymbol
    private lateinit var listener: LettersAdapter.Listener
    private var width: Int = 0

    fun bind(
        questSymbol: QuestSymbol,
        position: Int,
        listener: LettersAdapter.Listener,
        width: Int
    ) {
        this.model = questSymbol
        this.listener = listener
        this.width = width

        itemView.symbol_container.layoutParams.width = width / 9
        itemView.symbol_container.layoutParams.height = width / 9
        itemView.symbol_container.setPadding(width / 100, width / 100, width / 100, width / 100)

        if (model.getAnswer() == null){
            itemView.symbol_card_view.visibility = View.VISIBLE
            itemView.quest_symbol.text = model.getName()
        }else{
            itemView.symbol_card_view.visibility = View.GONE
        }

        itemView.setOnClickListener {
            if (itemView.symbol_card_view.visibility == View.VISIBLE){
                listener.onSymbolClick(model, position)
            }
        }
    }
}