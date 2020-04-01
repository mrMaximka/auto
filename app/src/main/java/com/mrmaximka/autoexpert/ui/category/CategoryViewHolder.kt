package com.mrmaximka.autoexpert.ui.category

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.model.CategoryModel
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var model: CategoryModel
    private lateinit var viewClick: CategoryAdapter.OnClick

    fun bind(
        categoryModel: CategoryModel,
        viewClick: CategoryAdapter.OnClick,
        position: Int
    ) {
        this.model = categoryModel
        this.viewClick = viewClick
        if (position == 0){
            itemView.category_image.setImageResource(R.drawable.part3)
        }else{
            itemView.category_image.setImageResource(R.drawable.car_logo)
        }
        itemView.category_name.text = model.name

        itemView.setOnClickListener {
            viewClick.onViewClick(model.id)
        }
    }
}