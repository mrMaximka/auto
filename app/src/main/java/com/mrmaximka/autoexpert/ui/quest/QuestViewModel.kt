package com.mrmaximka.autoexpert.ui.quest

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.mrmaximka.autoexpert.model.ListSingleton
import com.mrmaximka.autoexpert.model.QuestModel
import com.mrmaximka.autoexpert.model.QuestSymbol
import com.mrmaximka.autoexpert.model.UserModel
import com.mrmaximka.autoexpert.ui.quest.lists.AnswerAdapter
import com.mrmaximka.autoexpert.ui.quest.lists.LettersAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.RandomAccess

class QuestViewModel : ViewModel() {

    private var model = QuestModel()
    private lateinit var answerAdapter: AnswerAdapter
    private lateinit var lettersAdapter: LettersAdapter
    private lateinit var context: Context
    private lateinit var single: ListSingleton
    private lateinit var userModel: UserModel
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    var position: Int = 0
    var needNextQuest = MutableLiveData<Boolean>().apply { value = false }

    fun loadQuest(
        questPosition: Int,
        context: Context?,
        answerAdapter: AnswerAdapter,
        lettersAdapter: LettersAdapter
    ) {
        this.answerAdapter = answerAdapter
        this.lettersAdapter = lettersAdapter
        this.context = context!!
        this.position = questPosition
        single = ListSingleton.instance
        userModel = UserModel.instance
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        model = single.getModel(questPosition)


        val list: ArrayList<QuestSymbol> = ArrayList()
        val letters: String = model.letters
        val ltrToArray = letters.toCharArray()

        for (c in ltrToArray) {
            list.add(QuestSymbol(c.toString()))
        }

        val list2 = java.util.ArrayList<QuestSymbol>()
        val otv = model.name
        val strToArray = otv.toCharArray()
        for (c in strToArray) {
            list2.add(QuestSymbol(c.toString()))
        }

        model.lettersList = list
        model.answerList = list2

        addToAdapter(model, answerAdapter, lettersAdapter)

    }

    fun getImage() : String{
        return model.image
    }

    private fun addToAdapter(
        model: QuestModel,
        answerAdapter: AnswerAdapter,
        lettersAdapter: LettersAdapter
    ) {
        answerAdapter.setData(model.answerList)
        lettersAdapter.setData(model.lettersList)

    }

    fun onAnswer(symbol: QuestSymbol, position: Int) {
        val lettersList: java.util.ArrayList<QuestSymbol> = model.lettersList
        val answerList: java.util.ArrayList<QuestSymbol> = model.answerList

        if (symbol.getAnswer() != null) {
            val answer = symbol.getAnswer()
            for (i in Objects.requireNonNull(lettersList).indices) {
                if (lettersList[i].getAnswer() != null && lettersList[i].getAnswer()
                        .equals(answer)
                ) {
                    lettersList[i].setAnswer(null)
                    answerList[position].setAnswer(null)
                    symbol.setAnswer(null)
                    break
                }
            }
        }
        answerAdapter.setData(answerList)
        lettersAdapter.setData(lettersList)
    }

    fun onSymbol(symbol: QuestSymbol, position: Int) {
        val lettersList = model.lettersList
        val answerList = model.answerList

        for (i in answerList.indices) {
            if (answerList[i].getAnswer() == null) {
                answerList[i].setAnswer(symbol.getName())
                lettersList[position].setAnswer(symbol.getName())
                symbol.setAnswer(symbol.getName())
                break
            }
        }

        checkFull(answerList)
        answerAdapter.setData(answerList)
        lettersAdapter.setData(lettersList)
    }

    private fun checkFull(answerList: java.util.ArrayList<QuestSymbol>) {
        var answer: String?
        for (i in answerList.indices) {
            answer = answerList[i].getAnswer()
            if (answer == null) {
                return
            }
        }

        checkWin(answerList)

    }

    private fun checkWin(answerList: java.util.ArrayList<QuestSymbol>) {
        var answer: String?
        var fail = false
        for (i in answerList.indices) {
            answer = answerList[i].getAnswer()
            if (answer != answerList[i].getName()) {
                fail = true
                break
            }
        }

        if (fail) {
            Toast.makeText(context, "Fail", Toast.LENGTH_LONG).show()
            showFail()
        } else {
            Toast.makeText(context, "Win", Toast.LENGTH_LONG).show()
//            createDialog()
            showWin()
        }

    }

    private fun showFail() {
        myRef.child(userModel.getUid()).child("parts").child(position.toString()).setValue(2)
    }

    private fun showWin() {
        myRef.child(userModel.getUid()).child("parts").child(position.toString()).setValue(1)
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Win!")
        dialog.setMessage(model.description)
        dialog.setNegativeButton(
            "Next",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                Toast.makeText(context, "Next", Toast.LENGTH_LONG).show()
                getNextQuest()
                needNextQuest.value = true
            }
        )
        dialog.setCancelable(false)
        dialog.show()

    }

    private fun getNextQuest(){
        if ((position + 1) < single.getSize())
            position++
        else position = 0
    }

    fun addLetter() {

        if (userModel.getCoins() < 10){
            Toast.makeText(context, "Недостаточно средств", Toast.LENGTH_LONG).show()
            return
        }

        myRef.child(userModel.getUid()).child("coins").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MMV", "Coins cancelled")
                Toast.makeText(context, "Ошибка подключения к серверу", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("MMV", "Coins changed")
                val freeLetter: ArrayList<Int> = ArrayList()
                val answerList = model.answerList
                for (i in answerList.indices) {
                    if (answerList[i].getAnswer() == null) {
                        freeLetter.add(i)
                    }
                }
                val random = (0 until freeLetter.size).random()
                val index = freeLetter[random]
                answerList[index].setAnswer(answerList[index].getName())
                checkFull(answerList)
                answerAdapter.setData(answerList)
            }

        })
        myRef.child(userModel.getUid()).child("coins").setValue(userModel.getCoins() - 10)

    }

    fun delLetters() {
        if (userModel.getCoins() < 20){
            Toast.makeText(context, "Недостаточно средств", Toast.LENGTH_LONG).show()
            return
        }

        myRef.child(userModel.getUid()).child("coins").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MMV", "Coins cancelled")
                Toast.makeText(context, "Ошибка подключения к серверу", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("MMV", "Coins changed")
                var answer = model.name
                val lettersList = model.lettersList

                for (i in lettersList.indices){
                    val index = answer.indexOf(lettersList[i].getName().toString())
                    if (index != -1){
                        answer = answer.removeRange(index, index+1)
                    }else{
                        lettersList[i].setAnswer(lettersList[i].getName())
                    }
                }

                lettersAdapter.setData(lettersList)
            }

        })
        myRef.child(userModel.getUid()).child("coins").setValue(userModel.getCoins() - 20)

    }


    fun setAnswer() {
        if (userModel.getCoins() < 50){
            Toast.makeText(context, "Недостаточно средств", Toast.LENGTH_LONG).show()
            return
        }

        myRef.child(userModel.getUid()).child("coins").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MMV", "Coins cancelled")
                Toast.makeText(context, "Ошибка подключения к серверу", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("MMV", "Coins changed")
                val answerList = model.answerList
                for (i in answerList.indices) {
                    if (answerList[i].getAnswer() == null) {
                        answerList[i].setAnswer(answerList[i].getName())
                    }
                }
                answerAdapter.setData(answerList)
                checkFull(answerList)
            }

        })
        myRef.child(userModel.getUid()).child("coins").setValue(userModel.getCoins() - 50)

    }
}