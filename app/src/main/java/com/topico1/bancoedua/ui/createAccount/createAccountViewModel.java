package com.topico1.bancoedua.ui.createAccount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class createAccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public createAccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is createAccount fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}