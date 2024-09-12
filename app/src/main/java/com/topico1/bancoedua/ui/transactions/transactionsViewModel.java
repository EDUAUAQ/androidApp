package com.topico1.bancoedua.ui.transactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class transactionsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public transactionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is transactions fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}