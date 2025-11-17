package org.specter.paymentdivider.app.ui.screen

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.specter.paymentdivider.BuildConfig
import org.specter.paymentdivider.app.NavigationOption
import org.specter.paymentdivider.app.NavigationRoute
import org.specter.paymentdivider.app.PaymentDividerViewModel
import org.specter.paymentdivider.domain.model.Human


@Composable
fun CreateHumanScreen(
    viewModel: PaymentDividerViewModel = hiltViewModel(),
    screenViewModel: CreateHumanScreenViewModel = hiltViewModel(),
    navigate: (NavigationOption) -> Unit
) {
    val nameInput by screenViewModel.nameInput.collectAsState()
    val humanList by viewModel.humanList.collectAsState()

    CreateHumanScreen(
        nameInput = nameInput,
        nextVisible = humanList.isNotEmpty(),
        onChangeNameInput = screenViewModel::onNameInputChange,
        onAddHuman = { viewModel.addHuman(nameInput) },
        humanList = humanList,
        onDeleteHuman = viewModel::onDeleteHuman,
        onClickNext = {
            navigate(NavigationOption.Screen(route = NavigationRoute.PaymentCalculator))
        }
    )
}

@Composable
private fun CreateHumanScreen(
    nameInput: String = "",
    humanList: List<Human> = emptyList(),
    nextVisible: Boolean = false,
    onChangeNameInput: (String) -> Unit,
    onAddHuman: () -> Unit,
    onDeleteHuman: (Human) -> Unit,
    onClickNext: () -> Unit
) = Column(
    Modifier
        .fillMaxSize()
        .safeDrawingPadding(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Spacer(Modifier.weight(1f))

    Text(text = "누구와 함께 정산하나요?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = onChangeNameInput,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onAddHuman()
                onChangeNameInput("")
            }),
            maxLines = 1,
        )
        Spacer(Modifier.width(10.dp))
        OutlinedButton(onClick = {
            onAddHuman()
            onChangeNameInput("")
        }) {
            Text("추가")
        }
    }

    Spacer(Modifier.height(20.dp))

    FlowRow(modifier = Modifier.weight(1f)) {
        humanList.forEach {
            InputChip(
                selected = false,
                onClick = { onDeleteHuman(it) },
                label = {
                    Text(it.name)
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            )
            Spacer(Modifier.width(10.dp))
        }
    }

    Spacer(Modifier.weight(1f))

    AnimatedVisibility(nextVisible) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onClickNext) {
                Text("다음")
            }

            Spacer(Modifier.width(10.dp))
        }
    }

    Text(text = "Version: ${BuildConfig.VERSION_NAME}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateHumanScreen() {
    CreateHumanScreen(
        nameInput = "test",
        onChangeNameInput = {},
        onAddHuman = {},
        onDeleteHuman = {},
        onClickNext = {},
        humanList = listOf(Human(name = "Kshull")),
        nextVisible = true
    )
}