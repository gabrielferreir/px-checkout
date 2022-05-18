package com.mercadopago.android.px.internal.mapper.instructions

import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInfo
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionInfoMapper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InstructionInfoMapperTest {

    @Test
    fun whenOnlyOneStringAddedThenInfoShouldOnlyContainTitle() {
        val info = listOf("Nueva info")
        val expectedResult = InstructionInfo.Model(info[0], null)
        val result = InstructionInfoMapper().map(info)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenTwoStringsAddedAndSecondOneIsNotEmptyThenInfoShouldOnlyContainContentWithMultipleLines() {
        val info = listOf("Nueva info", "Segunda nueva info")
        val expectedResult = InstructionInfo.Model(null, info[0] + "<br>" + info[1])
        val result = InstructionInfoMapper().map(info)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenTwoStringsAddedAndSecondOneIsEmptyThenInfoShouldOnlyContainTitle() {
        val info = listOf("Nueva info", "")
        val expectedResult = InstructionInfo.Model(info[0], null)
        val result = InstructionInfoMapper().map(info)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenThreeStringsAddedAndSecondOneIsEmptyThenInfoShouldContainTitleAndContentWithOnlyOneLine() {
        val info = listOf("Nueva info", "", "Content 1")
        val expectedResult = InstructionInfo.Model(info[0], info[2])
        val result = InstructionInfoMapper().map(info)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenMoreThanThreeStringsAddedAndSecondOneIsEmptyThenInfoShouldContainTitleAndContentWithMultipleLines() {
        val info = listOf("Nueva info", "", "Content 1", "Content 2")
        val expectedResult = InstructionInfo.Model(info[0], info[2] + "<br>" + info[3])
        val result = InstructionInfoMapper().map(info)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

}