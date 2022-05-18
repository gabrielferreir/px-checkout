package com.mercadopago.android.px.internal.view.instructions

import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertGone
import com.mercadopago.android.px.assertVisible
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInfo
import com.mercadopago.android.px.internal.view.MPTextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InstructionInfoTest : BasicRobolectricTest() {
    private lateinit var instructionInfo: InstructionInfo

    val title = "Test"
    val content = "Content"

    @Before
    fun setUp() {
        val context = Mockito.spy(getContext())
        instructionInfo = InstructionInfo(context)
    }

    @Test
    fun whenNoTitleSpecifiedTitleShouldBeGone(){
        val model = InstructionInfo.Model(null, content)
        instructionInfo.init(model)
        with (instructionInfo){
            findViewById<MPTextView>(R.id.title).assertGone()
        }
    }

    @Test
    fun whenNoContentSpecifiedContentShouldBeGone(){
        val model = InstructionInfo.Model(title, null)
        instructionInfo.init(model)
        with (instructionInfo){
            findViewById<MPTextView>(R.id.content).assertGone()
        }
    }

    @Test
    fun whenTitleAndContentSpecifiedBothShouldBeVisible(){
        val model = InstructionInfo.Model(title, content)
        instructionInfo.init(model)
        with (instructionInfo){
            findViewById<MPTextView>(R.id.title).assertVisible()
            findViewById<MPTextView>(R.id.content).assertVisible()
        }
    }
}