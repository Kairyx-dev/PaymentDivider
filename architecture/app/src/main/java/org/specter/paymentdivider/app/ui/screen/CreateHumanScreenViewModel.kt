package org.specter.paymentdivider.app.ui.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateHumanScreenViewModel @Inject constructor(): ViewModel() {

    private val _nameInput = MutableStateFlow("")
    val nameInput = _nameInput.asStateFlow()

    fun onNameInputChange(name: String) {
        _nameInput.update { name }
    }
}