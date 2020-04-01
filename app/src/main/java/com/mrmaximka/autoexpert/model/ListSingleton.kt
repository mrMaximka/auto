package com.mrmaximka.autoexpert.model

class ListSingleton {

    private var list: ArrayList<QuestModel> = ArrayList()

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

    companion object{
        val instance = ListSingleton()
    }
}