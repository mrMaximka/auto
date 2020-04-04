package com.mrmaximka.autoexpert.model

class ListSingleton {

    private var list: ArrayList<QuestModel> = ArrayList()
    private lateinit var category: String

    fun add(model: QuestModel){
        list.add(model)
    }

    fun clear(){
        list.clear()
    }

    fun getModel(position: Int) : QuestModel{
        return list[position]
    }

    fun getSize() : Int{
        return list.size
    }

    fun getCategory() : String{
        return category
    }

    fun setCategory(category: String){
        this.category = category
    }

    companion object{
        val instance = ListSingleton()
    }
}