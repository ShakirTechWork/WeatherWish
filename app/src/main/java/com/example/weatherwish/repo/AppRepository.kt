package com.example.weatherwish.repo

import android.content.Context
import com.example.weatherwish.api.ApiResponse
import com.example.weatherwish.api.NetworkEndpoints
import com.example.weatherwish.api.result
import com.example.weatherwish.datastore.AppDataStore
import com.example.weatherwish.datastore.AppDataStore2
import com.example.weatherwish.firebase.FirebaseManager
import com.example.weatherwish.firebase.FirebaseResponse
import com.example.weatherwish.firebase.firebaseAwaitOperationCaller
import com.example.weatherwish.firebase.firebaseOperationAwaitCaller
import com.example.weatherwish.model.SelectedTimeModel
import com.example.weatherwish.model.UserModel
import com.example.weatherwish.model.WeatherData
import com.example.weatherwish.model.WeatherForecastModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val networkEndpoints: NetworkEndpoints,
    private val appDataStore: AppDataStore,
    private val firebaseManager: FirebaseManager,
    private val applicationContext: Context
) {

    suspend fun createUserWithEmailAndPassword(email: String, password: String) =
        firebaseAwaitOperationCaller {
            firebaseManager.createUserWithEmailAndPassword(email, password)
        }

    suspend fun signInWithEmailAndPassword(email: String, password: String) =
        firebaseAwaitOperationCaller {
            firebaseManager.signInWithEmailAndPassword(email, password)
        }

    fun addUserIntoFirebase(name: String, email: String): FirebaseResponse<Boolean> {
        return firebaseManager.addUserIntoDatabase(name, email)
    }

    suspend fun getCurrentLoggedInUser(): FirebaseResponse<FirebaseUser?> {
        return firebaseManager.getCurrentLoggedInUser()
    }

    suspend fun getUserData(userId: String): FirebaseResponse<UserModel?> {
        return firebaseManager.getUserData(userId)
    }

    fun signOutCurrentUser() {
        firebaseManager.signOutCurrentUser()
    }

    fun updateUserPrimaryLocation(userId: String, primaryLocation: String): FirebaseResponse<Boolean> {
        return firebaseManager.updateUserPrimaryLocation(userId, primaryLocation)
    }

    fun updatePeriodicWeatherUpdatesData(userId: String, intervalInHours: Int, dndStartTime: Long, dndEndTime: Long): FirebaseResponse<Boolean> {
        return firebaseManager.updatePeriodicWeatherUpdatesData(userId, intervalInHours, dndStartTime, dndEndTime)
    }

    suspend fun updatePeriodicWeatherUpdatesData2(userId: String, intervalInHours: Int, dndStartTime: String, dndEndTime: String): FirebaseResponse<Boolean> {
        return firebaseManager.updatePeriodicWeatherUpdatesData2(userId, intervalInHours, dndStartTime, dndEndTime)
    }

    suspend fun updateTimelyWeatherUpdatesData(userId: String, timeList: ArrayList<SelectedTimeModel>): FirebaseResponse<Boolean> {
        return firebaseManager.updateTimelyWeatherUpdatesData(userId, timeList)
    }

    fun updateUserUnitPreference(userId: String, preferredUnit: String): FirebaseResponse<Boolean> {
        return firebaseManager.updateUserUnitPreference(userId, preferredUnit)
    }

    suspend fun saveUserPrimaryLocation(primaryLocation: String) {
        return appDataStore.savePrimaryLocation(primaryLocation)
    }

    fun getUserPrimaryLocation(): Flow<String> {
        return appDataStore.userPrimaryLocation
    }

    suspend fun deleteAllUserDataFromDatastore() {
        appDataStore.deleteAllUserData()
    }

    suspend fun getCurrentWeatherData(
        location: String,
        aqi: String
    ): Flow<ApiResponse<WeatherData?>> = result {
        networkEndpoints.getCurrentWeather("5c0b18c8dd744e858aa142154230910", location, aqi)
    }

    fun getForecastData(
        location: String,
        days: Int,
        aqi: String,
        alerts: String
    ): Flow<ApiResponse<WeatherForecastModel?>> = result {
        networkEndpoints.forecastWeather(
            "5c0b18c8dd744e858aa142154230910",
            location,
            days,
            aqi,
            alerts
        )
    }

}