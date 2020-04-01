package com.mrmaximka.autoexpert.ui.quest.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.QuestSymbol

class AnswerAdapter(val width: Int, val listener: Listener, val context: Context) : RecyclerView.Adapter<AnswerViewHolder>() {

    private var data: ArrayList<QuestSymbol> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest_symbol, parent, false)
        return AnswerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(data[position], position, listener, width, context)
    }

    fun setData(list: ArrayList<QuestSymbol>){
        this.data = list
        notifyDataSetChanged()
    }

    interface Listener{
        fun onAnswerClick(symbol: QuestSymbol, position: Int)
    }
}