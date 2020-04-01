package com.mrmaximka.autoexpert.model

class UserModel {
    private lateinit var name: String
    private lateinit var image: String
    private lateinit var coins: String
    private lateinit var uid: String

    private var parts: ArrayList<Int> = ArrayList()

    fun getUid(): String{
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun setCoins(coins: String){
        this.coins = coins
    }

    fun getCoins(): Int{
        return coins.toInt()
    }

    fun getPartValue(part: Int): Int{
        return parts[part]
    }

    fun setParts(list: ArrayList<Int>){
        parts = list
    }

    companion object{
        val instance = UserModel()
    }
}