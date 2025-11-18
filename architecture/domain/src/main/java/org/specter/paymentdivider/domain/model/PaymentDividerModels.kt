package org.specter.paymentdivider.domain.model

import java.util.UUID

data class Human(
    val id: UUID = UUID.randomUUID(),
    val name: String,
)

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val totalAmount: Int,
    val option: PaymentType
)

enum class PaymentType {
    N_DIVIDE,
    CUSTOM,
}

data class HumanWithPayment(
    val payment: Payment,
    val human: Human,
    val customAmount: Int,
    val enabled: Boolean,
    val isPayer: Boolean,
)