package com.example.problemdesk.presentation.problemform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.problemdesk.data.models.CreateRequestRequest
import com.example.problemdesk.data.sharedprefs.getSharedPrefsUserId
import com.example.problemdesk.databinding.FragmentProblemFormBinding
import com.example.problemdesk.domain.models.Specialization
import com.example.problemdesk.domain.models.Workplace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO visual issues with spinners!

class ProblemFormFragment : Fragment() {

    private var _binding: FragmentProblemFormBinding? = null
    private val binding get() = _binding!!

    private val problemFormViewModel: ProblemFormViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProblemFormBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userId = context?.let { getSharedPrefsUserId(it) }

        if (userId != null) {
            setUpClickListeners(userId)
        }

        setUpObservers()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpinners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setUpSpinners() {
        val problemTypeSpinner: Spinner = binding.problemTypeSpinner
        val specializationAdapter = SpecializationAdapter(requireContext(), getSpecializationArray())
        problemTypeSpinner.adapter = specializationAdapter

        val userWorkplaceSpinner: Spinner = binding.userWorkplaceSpinner
        val workplaceAdapter = WorkplaceAdapter(requireContext(), getWorkplaceArray())
        userWorkplaceSpinner.adapter = workplaceAdapter
        //spinners should receive areas and specialisations list from backend. In ideal world


//        val problemTypeSpinner: Spinner = binding.problemTypeSpinner
//        val problemTypeAdapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                getSpecializationArray()
//            )
//        problemTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        problemTypeSpinner.adapter = problemTypeAdapter
//
//        val userWorkplaceSpinner: Spinner = binding.userWorkplaceSpinner
//        val userWorkplaceAdapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                getWorkplaceArray()
//            )
//        userWorkplaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        userWorkplaceSpinner.adapter = userWorkplaceAdapter
//
//
//        //TODO spinner choose handling   --- i forgot what i wanted here. Still actual???
//        //https://metanit.com/java/android/5.4.php
    }

    private fun setUpObservers() {
        problemFormViewModel.successStatus.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { successStatus ->
                if (successStatus) {
                    showSuccessDialog()
                    binding.problemDescription.text.clear()
                    binding.problemTypeSpinner.setSelection(0)
                    binding.userWorkplaceSpinner.setSelection(0)
                }
            }
        })

        problemFormViewModel.errorStatus.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { errorStatus ->
                showErrorDialog(errorStatus)
            }
        })
    }

    private fun setUpClickListeners(userId: Int) {
        binding.loginButton.setOnClickListener {

            val selectedSpecialization = binding.problemTypeSpinner.selectedItem as Specialization
            val selectedWorkplace = binding.userWorkplaceSpinner.selectedItem as Workplace

            val requestType: Int = selectedSpecialization.id
            val areaId: Int = selectedWorkplace.id
            val description: String = binding.problemDescription.text.toString()

            if (validate(requestType, areaId, description)) {
                val request = CreateRequestRequest(requestType, userId, areaId, description)
                showConfirmationDialog(request)
            } else {
                showNotValidatedDialog()
            }
        }
    }

    private fun validate(requestType: Int, areaId: Int, description: String): Boolean {
        return requestType != 0 && areaId != 0 && description != ""
    }

    private fun showConfirmationDialog(request: CreateRequestRequest) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Подтверждение")
            setMessage("Подтвердите вашу заявку")
            setPositiveButton("Да") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    problemFormViewModel.createRequest(request)
                } //TODO error handling - when something goes wrong (internet connection, dead server, etc)
            }
            setNegativeButton("Нет", null)
            show()
        }
    }

    private fun showErrorDialog(text: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Ошибка")
            setMessage("Произошла ошибка: \n$text")
            setNegativeButton("Ок", null)
            show()
        }
    }

    private fun showNotValidatedDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Неполные данные")
            setMessage("Пожалуйста, заполните все поля")
            setNegativeButton("Ок", null)
            show()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Заявка успешно создана")
            setMessage("Ваша заявка отправлена на рассмотрение. Благодарим!")
            setNegativeButton("Ок", null)
            show()
        }
    }
}