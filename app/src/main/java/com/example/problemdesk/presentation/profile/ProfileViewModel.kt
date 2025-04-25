package com.example.problemdesk.presentation.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.problemdesk.data.models.LogOutRequest
import com.example.problemdesk.data.models.LogOutResponse
import com.example.problemdesk.data.repository.DeskRepositoryImplementation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

	private val _logoutStatus = MutableLiveData<Boolean>()
	val logoutStatus: LiveData<Boolean> get() = _logoutStatus

	private val repository = DeskRepositoryImplementation()
	private lateinit var logoutResponse: LogOutResponse

	fun logOut(request: LogOutRequest){
		//coroutineScope is more suitable in this case
		CoroutineScope(Dispatchers.IO).launch {
			try {
				logoutResponse = repository.logout(request)
				Log.i("!--{{{LOGOUT}}}--!", logoutResponse.toString())
				_logoutStatus.postValue((logoutResponse.message.toString() == "FCM token removed successfully"))
			} catch (e: Exception) {
				Log.i("!--{{{LOGOUT}}}--!", e.toString())
				_logoutStatus.postValue(false)
			}
		}
	}
}