package com.mrmaximka.autoexpert.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mrmaximka.autoexpert.model.CategoryModel

class CategoryViewModel : ViewModel() {

    var needOpen = MutableLiveData<Boolean>().apply { value = false }

    fun loadCategories(adapter: CategoryAdapter) {
        val list: ArrayList<CategoryModel> = ArrayList()

        val model1 = CategoryModel(1, "Запчасти")
        val model2 = CategoryModel(2, "Бренды")
        list.add(model1)
        list.add(model2)

        adapter.setElements(list)
    }

    fun onCategoryClick(){
        needOpen.value = true
    }
}