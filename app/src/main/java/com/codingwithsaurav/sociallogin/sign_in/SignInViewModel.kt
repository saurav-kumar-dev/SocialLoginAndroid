package com.codingwithsaurav.sociallogin.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableLiveData(SignInState())
    val state:LiveData<SignInState> = _state

    fun onSignInResult(result: SignInResult) {
        _state.postValue(SignInState(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ))
    }

    fun resetState() {
        _state.postValue(SignInState())
    }
}