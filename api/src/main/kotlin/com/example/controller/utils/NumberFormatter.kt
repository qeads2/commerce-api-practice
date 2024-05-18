package com.example.controller.utils

import java.text.DecimalFormat

class NumberFormatter {
    companion object {
        private val formatter = DecimalFormat("###,###")

        fun comma(number: Long): String = formatter.format(number)

        fun comma(number: Int): String = formatter.format(number)
    }
}
