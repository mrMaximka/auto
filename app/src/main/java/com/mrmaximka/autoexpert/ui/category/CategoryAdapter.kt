package com.mrmaximka.autoexpert.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.CategoryModel

class CategoryAdapter(private val viewClick: OnClick): RecyclerView.Adapter<CategoryViewHolder>() {

    private var categories: ArrayList<CategoryModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], viewClick, position)
    }

    fun setElements(list: ArrayList<CategoryModel>) {
        this.categories = list
        notifyDataSetChanged()
    }

    interface OnClick{
        fun onViewClick(categoryId: Int)
    }
}