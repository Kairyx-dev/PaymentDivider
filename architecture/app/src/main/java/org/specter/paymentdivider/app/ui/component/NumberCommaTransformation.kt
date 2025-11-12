package org.specter.paymentdivider.app.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class NumberCommaTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText =
            if (text.text.isNotEmpty()) text.text.toLongOrNull().formatWithComma() else ""
        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return formattedText.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }
        )
    }

    private fun Long?.formatWithComma(): String =
        NumberFormat.getNumberInstance(Locale.US).format(this ?: 0)
}