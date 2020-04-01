package com.mrmaximka.autoexpert.ui.quest.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.QuestSymbol

class LettersAdapter(val width: Int, val listener: Listener) : RecyclerView.Adapter<LettersViewHolder>() {

    private var data: ArrayList<QuestSymbol> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LettersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest_symbol, parent, false)
        return LettersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LettersViewHolder, position: Int) {
        holder.bind(data[position], position, listener, width)
    }

    fun setData(list: ArrayList<QuestSymbol>){
        this.data = list
        notifyDataSetChanged()
    }

    interface Listener{
        fun onSymbolClick(symbol: QuestSymbol, position: Int)
    }
}