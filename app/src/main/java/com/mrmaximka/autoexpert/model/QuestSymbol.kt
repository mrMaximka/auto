package com.mrmaximka.autoexpert.model

class QuestSymbol(name: String) {

    private var answer: String? = null
    private var name: String? = name

    fun getAnswer() : String? {
        return answer
    }

    fun getName() : String? {
        return name
    }

    fun setAnswer(answer: String?){
        this.answer = answer
    }

    fun setName(name: String?){
        this.name = name
    }
}