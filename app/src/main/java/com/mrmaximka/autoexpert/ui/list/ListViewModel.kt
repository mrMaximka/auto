package com.mrmaximka.autoexpert.ui.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mrmaximka.autoexpert.model.ListSingleton
import com.mrmaximka.autoexpert.model.QuestModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListViewModel : ViewModel() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ListAdapter
    private lateinit var storageRef: StorageReference
    private var list: ArrayList<QuestModel> = ArrayList()
    private lateinit var single: ListSingleton

    var needChooseQuest = MutableLiveData<Boolean>().apply { value = false }

    fun loadElements(adapter: ListAdapter, categoryId: Int) {
        this.adapter = adapter

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        single = ListSingleton.instance


        val getData = Runnable {
            list.clear()
            single.clear()
            db.collection(getCollectionName(categoryId))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("MMV", "${document.id} => ${document.data} || ${document.data["img"]}")

                        val model = QuestModel()
                        model.id = document.id
                        model.name = document.data["name"] as String
                        model.description = document.data["description"] as String
                        model.letters = document.data["letters"] as String
                        model.image = document.data["img"] as String

                        list.add(model)
                        single.add(model)
                        adapter.setElements(list)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("MMV", "Error getting documents.", exception)
                }
        }


        if (list.isEmpty()){
            Completable.fromRunnable(getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(completableObserver)
        }else{
            adapter.setElements(list)
        }
    }

    private val completableObserver: CompletableObserver = object : CompletableObserver{
        override fun onComplete() {
            Log.d("MMV", "GetData success")
        }

        override fun onSubscribe(d: Disposable?) {}

        override fun onError(e: Throwable?) {
            Log.d("MMV", "GetData error")
        }

    }

    private fun getCollectionName(categoryId: Int) : String{
        var name = ""
        when(categoryId){
            1 -> name = "parts"
            2 -> name = "brands"
        }
        single.setCategory(name)
        return name
    }

    fun onClickQuest(){
        needChooseQuest.value = true
    }
}