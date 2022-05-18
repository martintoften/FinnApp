package no.martin.app.view.helpers

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private const val DECIMAL_FORMAT = "###,###.#"

object CurrencyHelper {
    fun formatValue(value: Number, formatString: String = DECIMAL_FORMAT): String {
        val formatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.'
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(formatString, formatSymbols)
        return formatter.format(value) + " kr"
    }
}
