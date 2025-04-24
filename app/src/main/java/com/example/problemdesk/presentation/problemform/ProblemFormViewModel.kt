package com.example.problemdesk.presentation.problemform

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.problemdesk.data.models.CreateRequestRequest
import com.example.problemdesk.data.models.CreateRequestResponse
import com.example.problemdesk.data.repository.DeskRepositoryImplementation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProblemFormViewModel : ViewModel() {


	private val _successStatus = MutableLiveData<Boolean>()
	val successStatus: LiveData<Boolean> get() = _successStatus

	//TODO user's inputs should be remembered through app destroy??

	suspend fun createRequest(request: CreateRequestRequest) {
		val repository = DeskRepositoryImplementation()
		var createRequestResponse: CreateRequestResponse

		//TODO test with 0 area (invalid input)
//        viewModelScope.launch {
//            val response = repository.createRequest(request)
//            if (response.message=="Request created successfully") {
//                success = true
//                return@launch success
//            }
//            else {
//                success = false
//            }
//        }
//        return

		//coroutineScope is more suitable in this case
		CoroutineScope(Dispatchers.IO).launch {
			try {
				createRequestResponse = repository.createRequest(request)
				Log.i("!--{{{CREATE REQUEST}}}--!", createRequestResponse.toString())
				_successStatus.postValue((createRequestResponse.requestId.toString() == "Request created successfully"))
			} catch (e: Exception) {
				Log.i("!--{{{CREATE REQUEST}}}--!", e.toString())
				_successStatus.postValue(false)
			}
		}
	}
}