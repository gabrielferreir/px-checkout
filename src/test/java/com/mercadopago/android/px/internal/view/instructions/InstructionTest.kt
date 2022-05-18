package com.mercadopago.android.px.internal.view.instructions

import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.*
import com.mercadopago.android.px.internal.features.payment_result.instruction.Instruction
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInfo
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInteraction
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionReference
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionActionAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.view.AdapterLinearLayout
import com.mercadopago.android.px.internal.view.MPTextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InstructionTest : BasicRobolectricTest() {

    private lateinit var instructions: Instruction

    @Mock
    private lateinit var listener: InstructionActionAdapter.Listener

    @Before
    fun setUp() {
        val context = Mockito.spy(getContext())
        instructions = Instruction(context)
    }

    @Test
    fun initWithEmptyModelShouldSetAllViewsToGone() {
        instructions.init(Instruction.Model(null, null, null, null, null,
            null, null, null, null), listener)
        with(instructions) {
            assertGone(R.id.instruction_subtitle)
            assertGone(R.id.instruction_info)
            assertGone(R.id.instruction_interactions)
            assertGone(R.id.instruction_references)
            assertGone(R.id.instruction_actions)
            assertGone(R.id.instruction_secondary_info)
            assertGone(R.id.instruction_tertiary_info)
            assertGone(R.id.instruction_accreditation_comments)
            assertGone(R.id.instruction_accreditation_time)
        }
    }

    @Test
    fun initWithSubtitleShouldSetSubtitleAndItShouldBeVisible() {
        val text = "Test subtitle"
        instructions.init(Instruction.Model(text, null, null, null, null,
            null, null, null, null), listener)
        with(instructions) {
            assertVisible(R.id.instruction_subtitle)
            findViewById<MPTextView>(R.id.instruction_subtitle).assertText(text)
        }
    }

    @Test
    fun initWithInfoShouldSetInfoAndItShouldBeVisible() {
        val title = "Test subtitle"
        val content = "Content"
        instructions.init(Instruction.Model(null, InstructionInfo.Model(title, content), null, null, null,
            null, null, null, null), listener)
        with(instructions) {
            assertVisible(R.id.instruction_info)
            with(findViewById<InstructionInfo>(R.id.instruction_info)!!) {
                findViewById<MPTextView>(R.id.title).assertText(title)
                findViewById<MPTextView>(R.id.content).assertText(content)
            }
        }
    }

    @Test
    fun initWithInteractionsShouldSetInteractionsAndTheyShouldBeVisible() {
        val noAction = InstructionInteraction.Model("Link", "content 2", null, null)
        val copyAction = InstructionInteraction.Model("Copy", "content 1", null, InstructionActionModel.Copy("Copy", "Test"))
        val linkAction = InstructionInteraction.Model("Link", "content 2", null, InstructionActionModel.Link("Link", "Test"))
        instructions.init(Instruction.Model(null, null, listOf(noAction, copyAction, linkAction),
            null, null, null, null, null, null),
            listener)
        with(instructions) {
            assertVisible(R.id.instruction_interactions)
            with(findViewById<AdapterLinearLayout>(R.id.instruction_interactions)!!) {
                assertChildCount<InstructionInteraction>(3)
                for (childIndex in 0 until childCount) {
                    getChildAt(childIndex).assertVisible()
                }
            }
        }
    }

    @Test
    fun initWithReferenceShouldSetReferenceAndItShouldBeVisible() {
        val title = "Test subtitle"
        val reference = "Reference"
        val label = "Label"
        val comment = "Comment"
        val firstRef = InstructionReference.Model(title, label, reference, comment)
        val secondRef = InstructionReference.Model(null, label, reference, null)
        instructions.init(Instruction.Model(null, null, null, listOf(firstRef, secondRef), null,
            null, null, null, null), listener)
        with(instructions) {
            assertVisible(R.id.instruction_references)
            with(findViewById<AdapterLinearLayout>(R.id.instruction_references)!!) {
                assertChildCount<InstructionReference>(2)
                for (childIndex in 0 until childCount) {
                    getChildAt(childIndex).assertVisible()
                }
            }
        }
    }

    @Test
    fun initWithActionsShouldSetActionsAndTheyShouldBeVisible() {
        val copyAction = InstructionActionModel.Copy("Copy", "content")
        val linkAction = InstructionActionModel.Link("Link", "content")
        instructions.init(Instruction.Model(null, null, null, null,
            listOf(copyAction, linkAction), null, null, null,
            null), listener)
        with(instructions) {
            assertVisible(R.id.instruction_actions)
            with(findViewById<AdapterLinearLayout>(R.id.instruction_actions)!!) {
                assertChildCount<MeliButton>(2)
                for (childIndex in 0 until childCount) {
                    getChildAt(childIndex).assertVisible()
                }
            }
        }
    }

    @Test
    fun initWithSecondaryAndTertiaryInfoShouldSetSecondaryAndTertiaryInfoAndTheyShouldBeVisible() {
        val secondary = "Test secondary info"
        val tertiary = "Test secondary info"
        instructions.init(Instruction.Model(null, null, null, null, null,
            secondary, tertiary, null, null), listener)
        with(instructions) {
            assertVisible(R.id.instruction_secondary_info)
            assertVisible(R.id.instruction_tertiary_info)
            findViewById<MPTextView>(R.id.instruction_secondary_info).assertText(secondary)
            findViewById<MPTextView>(R.id.instruction_tertiary_info).assertText(tertiary)
        }
    }

    @Test
    fun initWithAccreditationInfoShouldSetSAccreditationInfoAndItShouldBeVisible() {
        val comment = "Test secondary info"
        val time = "Test secondary info"
        instructions.init(Instruction.Model(null, null, null, null, null,
            null, null, comment, time), listener)
        with(instructions) {
            assertVisible(R.id.instruction_accreditation_comments)
            assertVisible(R.id.instruction_accreditation_time)
            findViewById<MPTextView>(R.id.instruction_accreditation_comments).assertText(comment)
            findViewById<MPTextView>(R.id.instruction_accreditation_time).assertText(time)
        }
    }
}