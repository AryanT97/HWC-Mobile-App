package org.piramalswasthya.cho.ui.commons.personal_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

import org.piramalswasthya.cho.model.BenHealthIdDetails
import org.piramalswasthya.cho.model.PatientDisplay
import org.piramalswasthya.cho.repositories.PatientRepo
import org.piramalswasthya.cho.utils.filterBenList
import javax.inject.Inject

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */

@HiltViewModel
class PersonalDetailsViewModel @Inject constructor(
    private val patientRepo: PatientRepo,
) : ViewModel() {
    private val filter = MutableStateFlow("")

    var patientList : Flow<List<PatientDisplay>>? =patientRepo.getPatientListFlow().combine(filter){
        list, filter -> filterBenList(list, filter)
}
    var count : Int = 0
    private val _abha = MutableLiveData<String?>()
    val abha: LiveData<String?>
        get() = _abha

    private val _benId = MutableLiveData<Long?>()
    val benId: LiveData<Long?>
        get() = _benId

    private val _benRegId = MutableLiveData<Long?>()
    val benRegId: LiveData<Long?>
        get() = _benRegId



    enum class NetworkState {
        IDLE,
        LOADING,
        SUCCESS,
        FAILURE
    }

    private val _patientObserver = MutableLiveData(NetworkState.IDLE)
    val patientObserver: LiveData<NetworkState>
        get() = _patientObserver

    init {
        getPatientList()
    }

    fun getPatientList() {
        viewModelScope.launch {
            _patientObserver.value = NetworkState.SUCCESS
        }
    }

    fun filterText(text: String) {
        viewModelScope.launch {
            filter.emit(text)
        }

    }

    fun fetchAbha(benId: Long) {
        _abha.value = null
        _benRegId.value = null
        _benId.value = benId
        viewModelScope.launch {
            patientRepo.getBenFromId(benId)?.let {
                val result = it.beneficiaryRegID?.let { it1 -> patientRepo.getBeneficiaryWithId(it1) }
                if (result != null) {
                    _abha.value = result.healthIdNumber
                    it.healthIdDetails = BenHealthIdDetails(result.healthId, result.healthIdNumber)
                    patientRepo.updateRecord(it)
                } else {
                    _benRegId.value = it.beneficiaryRegID
                }
            }
        }
    }

    fun resetBenRegId() {
        _benRegId.value = null
    }


}
