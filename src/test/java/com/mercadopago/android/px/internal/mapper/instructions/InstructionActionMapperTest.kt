package com.mercadopago.android.px.internal.mapper.instructions

import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionActionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.InstructionAction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InstructionActionMapperTest {

    @Test
    fun whenTagIsCopyItShouldMapToCopyAction() {
        val action = getCopyAction()

        val expectedResult = with(action!!) {
            InstructionActionModel.Copy(label, content!!)
        }
        val result = InstructionActionMapper().map(action)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenTagIsLinkItShouldMapToLinkAction() {
        val action = getLinkAction()

        val expectedResult = with(action!!) {
            InstructionActionModel.Link(label, url!!)
        }
        val result = InstructionActionMapper().map(action)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun mapListShouldFilterAndReturnOnlyLinkActions() {
        val linkAction = getLinkAction()!!
        val otherLinkAction = getLinkAction()!!
        val copyAction = getLinkAction()!!

        val actionMapper = InstructionActionMapper()
        val result = actionMapper.map(listOf(linkAction, copyAction, otherLinkAction))
        Assert.assertTrue(result.all{ r -> r is InstructionActionModel.Link })
    }

    private fun getCopyAction(): InstructionAction? {
        return JsonUtil.fromJson("""{
                "label": "label",
                "tag": "copy",
                "content": "content"
            }""".trimIndent(), InstructionAction::class.java)
    }

    private fun getLinkAction(): InstructionAction? {
        return JsonUtil.fromJson("""{
                "label": "label",
                "tag": "link",
                "url": "url"
            }""".trimIndent(), InstructionAction::class.java)
    }
}