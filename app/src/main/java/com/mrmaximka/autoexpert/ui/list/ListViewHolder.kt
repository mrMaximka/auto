package com.mrmaximka.autoexpert.ui.list

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.QuestModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.item_quest_symbol.view.*

class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var model: QuestModel
    private lateinit var elementClick: ListAdapter.OnClick
    private lateinit var context: Context

    fun bind(
        quest: QuestModel,
        elementClick: ListAdapter.OnClick,
        width: Int,
        position: Int,
        modelId: Int,
        context: Context
    ) {
        this.model = quest
        this.elementClick = elementClick
        this.context = context
        if (modelId == 1){
            val color = this.context.resources.getColor(R.color.colorAccent)
            itemView.element_card.setCardBackgroundColor(color)
        }else if (modelId == 2){
            val color = this.context.resources.getColor(R.color.darkRed)
            itemView.element_card.setCardBackgroundColor(color)
        }
        Picasso.get().load(model.image).into(itemView.element_image)
        itemView.element_list_container.layoutParams.width = width/4
        itemView.element_list_container.layoutParams.height = width/4
        itemView.element_list_container.setPadding(
            width / 100,
            width / 100,
            width / 100,
            width / 100)
        itemView.setOnClickListener { elementClick.onElementClick(position) }
    }

}