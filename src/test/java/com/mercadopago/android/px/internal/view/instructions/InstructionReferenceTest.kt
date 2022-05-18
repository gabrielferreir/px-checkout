package com.mercadopago.android.px.internal.view.instructions

import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.R
import com.mercadopago.android.px.assertGone
import com.mercadopago.android.px.assertVisible
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionReference
import com.mercadopago.android.px.internal.view.MPTextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InstructionReferenceTest  : BasicRobolectricTest() {
    private lateinit var instructionReference : InstructionReference

    val title = "Test"
    private val label = "Label"
    private val reference = "Reference"
    val comment = "Comment"

    @Before
    fun setUp() {
        val context = Mockito.spy(getContext())
        instructionReference = InstructionReference(context)
    }

    @Test
    fun whenTitleIsNotSetItShouldBeGone(){
        val ref = InstructionReference.Model(null, label, reference, comment)
        instructionReference.init(ref)
        with (instructionReference){
            findViewById<MPTextView>(R.id.title).assertGone()
        }
    }

    @Test
    fun whenContentIsNotSetItShouldBeGone(){
        val ref = InstructionReference.Model(title, label, reference, null)
        instructionReference.init(ref)
        with (instructionReference){
            findViewById<MPTextView>(R.id.comment).assertGone()
        }
    }

    @Test
    fun whenEverythingIsSetThenViewsShouldBeVisible(){
        val ref = InstructionReference.Model(title, label, reference, comment)
        instructionReference.init(ref)
        with (instructionReference){
            findViewById<MPTextView>(R.id.title).assertVisible()
            findViewById<MPTextView>(R.id.label).assertVisible()
            findViewById<MPTextView>(R.id.reference).assertVisible()
            findViewById<MPTextView>(R.id.comment).assertVisible()
        }
    }
}