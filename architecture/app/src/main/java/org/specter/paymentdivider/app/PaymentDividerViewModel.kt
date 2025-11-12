package org.specter.paymentdivider.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.specter.paymentdivider.domain.model.Human
import org.specter.paymentdivider.domain.model.HumanWithPayment
import org.specter.paymentdivider.domain.model.Payment
import javax.inject.Inject

@HiltViewModel
class PaymentDividerViewModel @Inject constructor(): ViewModel() {

    private val _humanList = MutableStateFlow<List<Human>>(emptyList())

    val humanList = _humanList.asStateFlow()

    fun addHuman(name: String) {
        _humanList.update {
            it + Human(name = name)
        }
    }

    fun onDeleteHuman(human: Human) {
        _humanList.update {
            it.filter { allHuman ->  allHuman.id != human.id }
        }
    }
}