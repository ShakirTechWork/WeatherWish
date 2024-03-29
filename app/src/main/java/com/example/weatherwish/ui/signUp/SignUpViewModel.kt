package com.example.weatherwish.ui.signUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwish.exceptionHandler.ExceptionErrorCodes
import com.example.weatherwish.exceptionHandler.ExceptionErrorMessages
import com.example.weatherwish.firebase.FirebaseResponse
import com.example.weatherwish.repo.AppRepository
import com.example.weatherwish.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SignUpViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _resultMutableLiveData = MutableLiveData<FirebaseResponse<Boolean>>()

    val resultMutableLiveData: MutableLiveData<FirebaseResponse<Boolean>>
        get() = _resultMutableLiveData

    suspend fun createUserWithEmailAndPassword(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _resultMutableLiveData.postValue(FirebaseResponse.Loading)
            val result = appRepository.createUserWithEmailAndPassword(email, password)
            if (result is FirebaseResponse.Success) {
                val token = result.data?.user?.getIdToken(true)?.await()?.token
                if (token != null && token.isNotBlank()) {
                    Utils.printDebugLog("Adding_User_In_DB :: Loading")
                    val response = addUserIntoFirebase(name, email)
                    if (response is FirebaseResponse.Success) {
                        Utils.printDebugLog("Adding_User_In_DB :: Success")
                        _resultMutableLiveData.postValue(FirebaseResponse.Success(true))
                    } else if (response is FirebaseResponse.Failure) {
                        Utils.printDebugLog("Adding_User_In_DB :: Failure")
                        _resultMutableLiveData.postValue(
                            FirebaseResponse.Failure(
                                "300",
                                "Soemthing went wrong",
                                response.exception
                            )
                        )
                    }
                } else {
                    _resultMutableLiveData.postValue(
                        FirebaseResponse.Failure(
                            "300",
                            "Soemthing went wrong",
                            Exception()
                        )
                    )
                }
            } else if (result is FirebaseResponse.Failure) {
                _resultMutableLiveData.postValue(
                    FirebaseResponse.Failure(
                        ExceptionErrorCodes.FIREBASE_EXCEPTION,
                        ExceptionErrorMessages.FIREBASE_EXCEPTION,
                        result.exception
                    )
                )
            }
        }
    }

    fun addUserIntoFirebase(name: String, email: String): FirebaseResponse<Boolean> {
        return appRepository.addUserIntoFirebase(name, email)
    }

}