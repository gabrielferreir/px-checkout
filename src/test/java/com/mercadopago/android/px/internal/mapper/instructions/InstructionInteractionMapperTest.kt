package com.mercadopago.android.px.internal.mapper.instructions

import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInteraction
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionActionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionInteractionMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.Interaction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InstructionInteractionMapperTest {

    @Test
    fun whenOnlyTitleSpecifiedThenInteractionShouldOnlyContainTitle() {
        val interaction = JsonUtil.fromJson("""{
            "title": "title"
        }""".trimIndent(), Interaction::class.java)

        val expectedResult = with(interaction!!) {
            InstructionInteraction.Model(title, null, null, null)
        }
        val result = InstructionInteractionMapper(InstructionActionMapper()).map(interaction)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenContentSpecifiedThenInteractionShouldContainContent() {
        val interaction = JsonUtil.fromJson("""{
            "title": "title",
            "content": "content"
        }""".trimIndent(), Interaction::class.java)

        val expectedResult = with(interaction!!) {
            InstructionInteraction.Model(title, content, null, null)
        }
        val result = InstructionInteractionMapper(InstructionActionMapper()).map(interaction)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenShowMultilineSpecifiedThenInteractionShouldContainShowMultiline() {
        val interaction = JsonUtil.fromJson("""{
            "title": "title",
            "show_multiline_content": true
        }""".trimIndent(), Interaction::class.java)

        val expectedResult = with(interaction!!) {
            InstructionInteraction.Model(title, null, showMultilineContent, null)
        }
        val result = InstructionInteractionMapper(InstructionActionMapper()).map(interaction)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }


    @Test
    fun whenCopyActionSpecifiedThenInteractionShouldContainCopyAction() {
        val interaction = JsonUtil.fromJson("""{
            "title": "title",
            "action": {
                "label": "label",
                "tag": "copy",
                "content": "content"
            }
        }""".trimIndent(), Interaction::class.java)

        val expectedResult = with(interaction!!) {
            InstructionInteraction.Model(title, content,
                showMultilineContent, with(interaction.action!!) { InstructionActionModel.Copy(label, content!!) })
        }
        val result = InstructionInteractionMapper(InstructionActionMapper()).map(interaction)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }

    @Test
    fun whenLinkActionSpecifiedThenInteractionShouldContainLinkAction() {
        val interaction = JsonUtil.fromJson("""{
            "title": "title",
            "action": {
                "label": "label",
                "url": "testUrl",
                "tag": "link"
            }
        }""".trimIndent(), Interaction::class.java)

        val expectedResult = with(interaction!!) {
            InstructionInteraction.Model(title, content,
                showMultilineContent, with(interaction.action!!) { InstructionActionModel.Link(label, url!!) })
        }
        val result = InstructionInteractionMapper(InstructionActionMapper()).map(interaction)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result))
    }
}