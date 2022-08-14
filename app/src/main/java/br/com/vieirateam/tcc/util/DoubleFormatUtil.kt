package br.com.vieirateam.tcc.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object DoubleFormatUtil {

    fun doubleToString(value: Double): String {

        val locale = Locale("pt", "BR")
        val real = DecimalFormatSymbols(locale)
        val decimalFormat = DecimalFormat("¤ ###,###,##0.00", real)
        return decimalFormat.format(value)
    }
}