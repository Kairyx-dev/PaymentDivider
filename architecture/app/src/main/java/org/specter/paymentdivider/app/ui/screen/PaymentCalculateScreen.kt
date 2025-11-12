package org.specter.paymentdivider.app.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.specter.paymentdivider.app.NavigationOption
import org.specter.paymentdivider.app.PaymentDividerViewModel
import org.specter.paymentdivider.app.ui.component.NumberCommaTransformation
import org.specter.paymentdivider.domain.model.Human
import org.specter.paymentdivider.domain.model.Payment
import org.specter.paymentdivider.domain.model.PaymentType


data class PaymentCalculateScreenState(
    val paymentNameInput: String = "",
    val paymentAmountInput: Int = 0,
    val customHumanList: List<CustomHuman> = emptyList(),
    val paymentList: List<Payment> = emptyList(),
    val selectedType: PaymentType = PaymentType.N_DIVIDE,
    val resultList: List<DivideResult> = emptyList()
)

data class CustomHuman(
    val human: Human,
    val selected: Boolean,
    val customAmount: Int = 0,
)

data class DivideResult(
    val human: Human,
    val amount: Int
)

private val PaymentRoundShape = RoundedCornerShape(10.dp)

@Composable
fun PaymentCalculateScreen(
    paymentViewModel: PaymentDividerViewModel = hiltViewModel(),
    screenViewModel: PaymentCalculateScreenViewModel = hiltViewModel(),
    navigate: (NavigationOption) -> Unit
) {
    val humans by paymentViewModel.humanList.collectAsState()
    val uiState by screenViewModel.uiState.collectAsState()

    LaunchedEffect(humans) {
        screenViewModel.setHumans(humans)
    }

    PaymentCalculateScreen(
        uiState = uiState,
        onPaymentNameChange = screenViewModel::setPaymentNameInput,
        onPaymentAmountChange = screenViewModel::setPaymentAmountInput,
        onAddPaymentAmount = screenViewModel::addPaymentAmount,
        onClickAddPayment = screenViewModel::onClickAddPayment,
        onSelectType = screenViewModel::setSelectType,
        onSelectCustomHuman = screenViewModel::onSelectCustomHuman,
        onCustomAmountChange = screenViewModel::onCustomAmountChange,
    )
}


@Composable
private fun PaymentCalculateScreen(
    uiState: PaymentCalculateScreenState = PaymentCalculateScreenState(),
    onPaymentNameChange: (String) -> Unit,
    onPaymentAmountChange: (String) -> Unit,
    onAddPaymentAmount: (Int) -> Unit,
    onClickAddPayment: () -> Unit,
    onSelectType: (PaymentType) -> Unit,
    onSelectCustomHuman: (CustomHuman, Boolean) -> Unit,
    onCustomAmountChange: (CustomHuman, String) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
            .scrollable(scrollState, orientation = Orientation.Vertical),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Title("지출 추가")
        PaymentInput(
            paymentNameInput = uiState.paymentNameInput,
            paymentAmountInput = uiState.paymentAmountInput,
            selectedType = uiState.selectedType,
            customHumanList = uiState.customHumanList,
            onPaymentNameChange = onPaymentNameChange,
            onPaymentAmountChange = onPaymentAmountChange,
            onAddPaymentAmount = onAddPaymentAmount,
            onClickAdd = onClickAddPayment,
            onSelectType = onSelectType,
            onSelectCustomHuman = onSelectCustomHuman,
            onCustomAmountChange = onCustomAmountChange
        )

        Spacer(modifier = Modifier.height(20.dp))
        Title("내역")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PaymentRoundShape)
                .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = PaymentRoundShape)
        ) {
            uiState.paymentList.forEachIndexed { index, it ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(it.name, modifier = Modifier.weight(7f), textAlign = TextAlign.Center)
                    VerticalDivider(thickness = 1.dp)
                    Text("%,d₩".format(it.totalAmount), modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
                }

                if (index < uiState.paymentList.lastIndex) {
                    HorizontalDivider(thickness = 1.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Title("정산")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PaymentRoundShape)
                .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = PaymentRoundShape)
        ) {
            uiState.resultList.forEachIndexed { index, it ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(it.human.name, modifier = Modifier.weight(7f), textAlign = TextAlign.Center)
                    VerticalDivider(thickness = 1.dp)
                    Text("%,d₩".format(it.amount), modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
                }

                if (index < uiState.resultList.lastIndex) {
                    HorizontalDivider(thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun PaymentInput(
    paymentNameInput: String,
    paymentAmountInput: Int,
    selectedType: PaymentType,
    customHumanList: List<CustomHuman> = emptyList(),
    onPaymentNameChange: (String) -> Unit,
    onPaymentAmountChange: (String) -> Unit,
    onAddPaymentAmount: (Int) -> Unit,
    onClickAdd: () -> Unit,
    onSelectType: (PaymentType) -> Unit,
    onSelectCustomHuman: (CustomHuman, Boolean) -> Unit,
    onCustomAmountChange: (CustomHuman, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PaymentRoundShape)
            .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = PaymentRoundShape)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = paymentNameInput,
                onValueChange = onPaymentNameChange,
                placeholder = {
                    Text("지출 이름")
                }
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = paymentAmountInput.toString(),
                onValueChange = {
                    onPaymentAmountChange(it)
                },
                placeholder = {
                    Text("금액")
                },
                visualTransformation = NumberCommaTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            )

            OutlinedButton(onClick = onClickAdd) {
                Text("추가")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AddMoneyChipButton(5_000) {
                onAddPaymentAmount(5_000)
            }
            Spacer(Modifier.width(4.dp))
            AddMoneyChipButton(10_000) {
                onAddPaymentAmount(10_000)
            }
            Spacer(Modifier.width(4.dp))
            AddMoneyChipButton(50_000) {
                onAddPaymentAmount(50_000)
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedType == PaymentType.N_DIVIDE,
                onClick = { onSelectType(PaymentType.N_DIVIDE) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("N빵하기")
            }
            SegmentedButton(
                selected = selectedType == PaymentType.CUSTOM,
                onClick = { onSelectType(PaymentType.CUSTOM) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("커스텀")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PaymentRoundShape)
                .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = PaymentRoundShape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(5f), text = "이름", textAlign = TextAlign.Center)
                VerticalDivider(thickness = 1.dp)

                if (selectedType == PaymentType.CUSTOM) {
                    Text(modifier = Modifier.weight(2.5f), text = "금액 지정", textAlign = TextAlign.Center)
                    VerticalDivider(thickness = 1.dp)
                }

                Text(modifier = Modifier.weight(2.5f), text = "포함 여부", textAlign = TextAlign.Center)
            }

            HorizontalDivider(thickness = 1.dp)
            customHumanList.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modifier = Modifier.weight(5f), text = it.human.name, textAlign = TextAlign.Center)
                    VerticalDivider(thickness = 1.dp)

                    if (selectedType == PaymentType.CUSTOM) {
                        BasicTextField(
                            modifier = Modifier.weight(2.5f),
                            value = it.customAmount.toString(),
                            textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center),
                            onValueChange = { value -> onCustomAmountChange(it, value) },
                            visualTransformation = NumberCommaTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                        )
                        VerticalDivider(thickness = 1.dp)
                    }


                    Box(modifier = Modifier.weight(2.5f), contentAlignment = Alignment.Center) {
                        Checkbox(checked = it.selected, onCheckedChange = { checked -> onSelectCustomHuman(it, checked) })
                    }
                }
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun AddMoneyChipButton(
    amount: Int,
    onClick: () -> Unit
) {
    InputChip(
        selected = false,
        onClick = onClick,
        leadingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
        label = { Text("%,d₩".format(amount)) }
    )
}

@Composable
private fun Title(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(text)
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewPaymentCalculateScreen() {
    PaymentCalculateScreen(
        uiState = PaymentCalculateScreenState(
            paymentNameInput = "밥",
            paymentAmountInput = 50_000,
            customHumanList = listOf(CustomHuman(Human(name = "Kshull"), true, 10_000)),
            paymentList = listOf(
                Payment(
                    name = "카페",
                    totalAmount = 30_000,
                    option = PaymentType.N_DIVIDE
                )
            ),
            resultList = listOf(DivideResult(Human(name = "Kshull"), 20_000)),
        ),
        onPaymentNameChange = {},
        onPaymentAmountChange = {},
        onAddPaymentAmount = {},
        onClickAddPayment = {},
        onSelectType = {},
        onSelectCustomHuman = { _, _ -> },
        onCustomAmountChange = { _, _ -> },
    )
}