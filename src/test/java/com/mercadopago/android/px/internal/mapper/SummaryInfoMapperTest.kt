package com.mercadopago.android.px.internal.mapper

import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.internal.mappers.SummaryInfoMapper
import com.mercadopago.android.px.model.internal.AdditionalInfo
import com.mercadopago.android.px.model.internal.SummaryInfo
import com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubBuilderOneItemAndPayer
import com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubBuilderOneItemAndPayerWithAdditionalInfo
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SummaryInfoMapperTest {

    @Test
    fun whenMapCheckoutPreferenceToSummaryInfoWithAdditionalInfo() {
        val pref = stubBuilderOneItemAndPayerWithAdditionalInfo().build()
        val expected = AdditionalInfo.newInstance(pref.additionalInfo)?.summaryInfo
        val actual = SummaryInfoMapper().map(pref)

        assertTrue(ReflectionEquals(expected).matches(actual))
    }

    @Test
    fun whenMapCheckoutPreferenceToSummaryInfoWithItems() {
        val pref = stubBuilderOneItemAndPayer().build()
        val expected = pref.items[0].run { SummaryInfo(description.orIfEmpty(title), pictureUrl) }
        val actual = SummaryInfoMapper().map(pref)

        assertTrue(ReflectionEquals(expected).matches(actual))
    }

}