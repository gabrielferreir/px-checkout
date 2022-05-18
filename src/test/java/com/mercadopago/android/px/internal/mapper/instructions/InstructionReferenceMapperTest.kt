package com.mercadopago.android.px.internal.mapper.instructions

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionReference
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.InstructionReferenceMapper
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.model.Instruction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InstructionReferenceMapperTest {

    @Test
    fun whenInstructionDoesNotContainReferencesMapShouldReturnNull() {
        val instruction = JsonUtil.fromJson("""{
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        Assert.assertNull(result)
    }

    @Test
    fun whenInstructionHasOneReferenceThatHasOnlyALabelItShouldMapThatLabelOnly() {
        val instruction = JsonUtil.fromJson("""{
            "references": [
                {
                    "label": "Concepto"
                }
            ]
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, "", null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasOneReferenceThatHasCommentItShouldMapCommentField() {
        val instruction = JsonUtil.fromJson("""{
            "references": [
                {
                    "label": "Concepto",
                    "comment": "Comment"
                }
            ]
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, "", first().comment)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasOneReferenceThatHasSeparatorButNoFieldValuesReferenceShouldBeEmpty() {
        val instruction = JsonUtil.fromJson("""{
            "references": [
                {
                    "label": "Concepto",
                    "separator": "-"
                }
            ]
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, "", null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasOneReferenceWithFieldValuesAndEmptySeparatorReferenceShouldBeJoinedWithNoSpaces() {
        val fieldValues = listOf("7123", "999", "492")
        val instruction = JsonUtil.fromJson("""{
            "references": [
                {
                    "label": "Concepto",
                    "separator": "",
                    "field_value": [
                        """ + fieldValues[0] + """,
                        """ + fieldValues[1] + """,
                        """ + fieldValues[2] + """
                    ]
                }
            ]
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, fieldValues.joinToString(""), null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasOneReferenceWithFieldValuesAndNonEmptySeparatorReferenceShouldBeJoinedWithSeparator() {
        val fieldValues = listOf("7123", "999", "492")
        val instruction = JsonUtil.fromJson("""{
            "references": [
                {
                    "label": "Concepto",
                    "separator": " ",
                    "field_value": [
                        """ + fieldValues[0] + """,
                        """ + fieldValues[1] + """,
                        """ + fieldValues[2] + """
                    ]
                }
            ]
            }""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, fieldValues.joinToString(" "), null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionContainsMultipleReferencesItShouldMapAllReferences(){
        val instruction = JsonUtil.fromJson("""{ "info": [
                "Necesitarás estos datos:"
            ],
            "references": [
            {
                "label": "Código de Link Pagos",
                "field_value": [
                "0",
                "1",
                "9",
                "1",
                "9",
                "4",
                ],
                "separator": " ",
                "comment": null
            },
            {
                "label": "Concepto",
                "field_value": [
                "MPAGO: COMPRA"
                ],
                "separator": "",
                "comment": null
            },
            {
                "label": "Referencia para abonar",
                "field_value": [
                "71523",
                "4"
                ],
                "separator": null,
                "comment": null
            }]}""".trimIndent(), Instruction::class.java)
        with (instruction!!) {
            val result = InstructionReferenceMapper().map(this)
            with (references!!) {
                val firstRef = get(0)
                val expectedFirstRef = InstructionReference.Model(null, firstRef.label,
                    firstRef.fieldValue!!.joinToString(firstRef.separator ?: TextUtil.EMPTY), firstRef.comment)

                Assert.assertTrue(ReflectionEquals(expectedFirstRef).matches(result?.get(0)))
            }
        }
    }

    @Test
    fun whenInstructionHasInfoWithNoSpacesTitleShouldBeNull(){
        val instruction = JsonUtil.fromJson("""{ "info": [
                "Necesitarás estos datos:"
            ],
            "references": [
                {
                    "label": "Código de Link Pagos"
                }
            ]}""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, "", null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasInfoWithOneSpaceTitleShouldBeNull(){
        val instruction = JsonUtil.fromJson("""{ "info": [
                "Necesitarás estos datos:",
                ""
            ],
            "references": [
                {
                    "label": "Código de Link Pagos"
                }
            ]}""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model(null, first().label, "", null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }

    @Test
    fun whenInstructionHasInfoWithMoreThanOneSpaceTitleShouldBeTheNextTextAfterLastSpace(){
        val instruction = JsonUtil.fromJson("""{ "info": 
            [
                "Necesitarás estos datos:",
                "",
                "",
                "Title"
            ],
            "references": [
                {
                    "label": "Código de Link Pagos"
                }
            ]}""".trimIndent(), Instruction::class.java)
        val result = with(instruction!!) {
            InstructionReferenceMapper().map(this)
        }
        val expectedResult = with(instruction.references!!) {
            InstructionReference.Model("Title", first().label, "", null)
        }
        result!!.size.assertEquals(1)
        Assert.assertTrue(ReflectionEquals(expectedResult).matches(result[0]))
    }
}