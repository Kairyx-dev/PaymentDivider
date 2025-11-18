package org.specter.paymentdivider.app.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.specter.paymentdivider.domain.model.Human
import org.specter.paymentdivider.domain.model.HumanWithPayment
import org.specter.paymentdivider.domain.model.Payment
import org.specter.paymentdivider.domain.model.PaymentType
import javax.inject.Inject

@HiltViewModel
class PaymentCalculateScreenViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val maxAmount = 100_000_000
    private val _uiState = MutableStateFlow(PaymentCalculateScreenState())
    val uiState = _uiState.asStateFlow()

    private lateinit var humans: List<Human>


    fun setHumans(humans: List<Human>) {
        this.humans = humans
        _uiState.update {
            it.copy(
                customHumanList = humans.map { human -> CustomHuman(human, true) },
                resultList = humans.map { human -> DivideResult(human, 0) }
            )
        }
    }

    fun setPaymentNameInput(name: String) {
        _uiState.update { it.copy(paymentNameInput = name) }
    }

    fun setPaymentAmountInput(amount: String) {
        val toAmount = amount.toAmount()
        if (toAmount > maxAmount) return

        _uiState.update { it.copy(paymentAmountInput = toAmount) }
    }

    fun addPaymentAmount(amount: Int) {
        _uiState.update { it.copy(paymentAmountInput = it.paymentAmountInput + amount) }
    }

    fun onSelectCustomHuman(changedHuman: CustomHuman, selected: Boolean) {
        _uiState.update {
            it.copy(customHumanList = it.customHumanList.map { customHuman ->
                if (changedHuman.human.id == customHuman.human.id) {
                    customHuman.copy(selected = selected)
                } else {
                    customHuman
                }
            })
        }
    }

    fun onSelectPayer(changedHuman: CustomHuman, selected: Boolean) {
        _uiState.update {
            it.copy(customHumanList = it.customHumanList.map { customHuman ->
                if (changedHuman.human.id == customHuman.human.id) {
                    customHuman.copy(isPayer = selected, selected = true)
                } else {
                    customHuman.copy(isPayer = false)
                }
            })
        }
    }

    fun onCustomAmountChange(changedHuman: CustomHuman, amount: String) {
        _uiState.update {
            it.copy(customHumanList = it.customHumanList.map { customHuman ->
                val toAmount = amount.toAmount()
                if (changedHuman.human.id == customHuman.human.id && toAmount <= maxAmount) {
                    customHuman.copy(customAmount = toAmount)
                } else {
                    customHuman
                }
            })
        }
    }

    fun setSelectType(type: PaymentType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onClickAddPayment() {
        viewModelScope.launch(Dispatchers.Default) {
            val current = _uiState.value

            if (current.paymentNameInput.isBlank()) {
                showToast("지출 이름을 입력하세요")
                return@launch
            }

            if (current.paymentAmountInput == 0) {
                showToast("지출 금액을 입력하세요")
                return@launch
            }

            val payer = current.customHumanList.find { it.isPayer }

            if (payer == null) {
                showToast("결제 여부를 선택하세요")
                return@launch
            }

            val payment = Payment(
                name = current.paymentNameInput,
                totalAmount = current.paymentAmountInput,
                option = current.selectedType
            )

            // 저장용
            val paymentWithHuman = current.customHumanList.map {
                HumanWithPayment(
                    payment = payment,
                    human = it.human,
                    customAmount = it.customAmount,
                    enabled = it.selected,
                    isPayer = it.isPayer
                )
            }

            val resultList = current.resultList

            val result = calculate(payment, paymentWithHuman, resultList)

            _uiState.update {
                it.copy(
                    paymentList = it.paymentList + payment,
                    resultList = result,
                    paymentNameInput = "",
                    paymentAmountInput = 0,
                    customHumanList = humans.map { human -> CustomHuman(human, true) }
                )
            }
        }
    }

    private fun calculate(
        payment: Payment,
        paymentWithHuman: List<HumanWithPayment>,
        resultList: List<DivideResult>
    ): List<DivideResult> {
        return if (payment.option == PaymentType.N_DIVIDE) {
            calculateNDivide(paymentWithHuman, payment, resultList)
        } else {
            calculateCustomDivide(payment, paymentWithHuman, resultList)
        }
    }

    private fun calculateNDivide(
        paymentWithHuman: List<HumanWithPayment>,
        payment: Payment,
        resultList: List<DivideResult>
    ): List<DivideResult> {
        val payer = paymentWithHuman.find { it.isPayer }!!
        val enabledHuman = paymentWithHuman.filter { it.enabled && !it.isPayer }

        val nDivideSize = if (payer.enabled) {
            enabledHuman.size + 1
        } else {
            enabledHuman.size
        }

        // N빵 가격 = 전체 가격 / (포함된 사람 + 결제자)
        val nDividePrice = payment.totalAmount / (nDivideSize)

        return resultList.map { divideResult ->
            when (divideResult.human.id) {
                // 결제자
                payer.human.id -> divideResult.copy(
                    amount = divideResult.amount + (if (payer.enabled) {
                        nDividePrice
                    } else {
                        0
                    } - payment.totalAmount)
                )

                // 포함된 사람들
                in enabledHuman.map { it.human.id } -> divideResult.copy(amount = divideResult.amount + nDividePrice)

                // 포함되지 않은 사람들
                else -> divideResult
            }
        }
    }

    private fun calculateCustomDivide(
        payment: Payment,
        paymentWithHuman: List<HumanWithPayment>,
        resultList: List<DivideResult>
    ): List<DivideResult> {
        var remainAmount = payment.totalAmount
        val (customHuman, nDivideHuman) = paymentWithHuman.partition { it.customAmount > 0 && it.enabled }

        val customMapped = resultList.map { divideResult ->
            customHuman.find { it.human.id == divideResult.human.id }?.let {
                remainAmount -= it.customAmount

                if (it.isPayer) {
                    // 커스텀 가격 지정된 결제자
                    divideResult.copy(amount = divideResult.amount + (it.customAmount - payment.totalAmount))
                } else {
                    // 커스텀 가격 지정된 사람
                    divideResult.copy(amount = divideResult.amount + it.customAmount)
                }
            } ?: divideResult
        }

        val nDividePrice = remainAmount / nDivideHuman.size
        return customMapped.map { divideResult ->
            nDivideHuman.find { it.human.id == divideResult.human.id }?.let {
                when {
                    it.isPayer && it.enabled -> divideResult.copy(amount = divideResult.amount + (nDividePrice - payment.totalAmount))
                    it.isPayer -> divideResult.copy(amount = divideResult.amount - payment.totalAmount)
                    it.enabled -> divideResult.copy(amount = divideResult.amount + nDividePrice)
                    else -> divideResult
                }
            } ?: divideResult
        }
    }

    private fun String.toAmount(): Int = replace(",", "").toIntOrNull() ?: 0

    private suspend fun showToast(text: String) = coroutineScope {
        withContext(Dispatchers.Main){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}