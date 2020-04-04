package com.mrmaximka.autoexpert.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.QuestModel
import com.mrmaximka.autoexpert.model.UserModel

class ListAdapter(
    private val elementClick: OnClick,
    private val width: Int,
    private val context: Context
): RecyclerView.Adapter<ListViewHolder>() {

    private var elements: ArrayList<QuestModel> = ArrayList()
    private lateinit var userModel: UserModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return elements.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(elements[position], elementClick, width, position, userModel.getPartValue(elements[position].id.toInt()), context)
    }

    fun setElements(list: ArrayList<QuestModel>){
        userModel = UserModel.instance
        this.elements = list
        notifyDataSetChanged()
    }

    interface OnClick{
        fun onElementClick(position: Int)
    }
}