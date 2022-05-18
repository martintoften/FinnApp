package no.martin.app

import no.martin.app.view.helpers.CurrencyHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CurrencyHelperTest {

    @ParameterizedTest
    @MethodSource("getData")
    fun ds(input: Int, expected: String) {
        val result = CurrencyHelper.formatValue(input)
        assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        private fun getData() = listOf(
            Arguments.of(0, "0 kr"),
            Arguments.of(-10000, "-10 000 kr"),
            Arguments.of(-1, "-1 kr"),
            Arguments.of(10, "10 kr"),
            Arguments.of(200, "200 kr"),
            Arguments.of(3000, "3 000 kr"),
            Arguments.of(40000, "40 000 kr"),
            Arguments.of(500000, "500 000 kr"),
            Arguments.of(6000000, "6 000 000 kr"),
            Arguments.of(70000000, "70 000 000 kr"),
            Arguments.of(800000000, "800 000 000 kr"),
        )
    }
}
