package com.mercadopago.android.px.format

import com.mercadopago.android.px.internal.util.RateParser
import org.junit.Test

class RateParserTest {

    @Test
    fun whenLabelIsCFTAndTea() {
        val rates = RateParser.getRates(listOf("CFT_9,80%|TEA_7,50%"))
        val expectedCFT = "9,80%"
        val expectedTEA = "7,50%"

        assert(rates["CFT"] == expectedCFT)
        assert(rates["TEA"] == expectedTEA)
    }

    @Test
    fun whenLabelIsCFTNAAndTea() {
        val rates = RateParser.getRates(listOf("CFTNA_9,80%|TEA_7,50%"))
        val expectedCFT = "9,80%"
        val expectedTEA = "7,50%"

        assert(rates["CFTNA"] == expectedCFT)
        assert(rates["TEA"] == expectedTEA)
    }

    @Test
    fun whenLabelIsCFTAndCFTNAAndTea() {
        val rates = RateParser.getRates(listOf("CFTNA_9,80%|CFT_8,40%|TEA_7,50%"))
        val expectedCFTNA = "9,80%"
        val expectedCFT = "8,40%"
        val expectedTEA = "7,50%"

        assert(rates["CFT"] == expectedCFT)
        assert(rates["CFTNA"] == expectedCFTNA)
        assert(rates["TEA"] == expectedTEA)
    }

    @Test
    fun whenLabelIsEmpty() {
        assert(RateParser.getRates(listOf()).isEmpty())
    }
}