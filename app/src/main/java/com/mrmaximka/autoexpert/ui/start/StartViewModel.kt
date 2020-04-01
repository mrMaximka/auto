package com.mrmaximka.autoexpert.ui.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartViewModel : ViewModel() {

    var needStart = MutableLiveData<Boolean>().apply { value = false }

    fun onStartClick() {
        needStart.value = true
    }
}