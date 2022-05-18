package com.mercadopago.android.px.internal.view.instructions

import android.view.View
import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.*
import com.mercadopago.android.px.internal.actions.CopyAction
import com.mercadopago.android.px.internal.actions.LinkAction
import com.mercadopago.android.px.internal.features.payment_result.instruction.InstructionInteraction
import com.mercadopago.android.px.internal.features.payment_result.instruction.adapter.InstructionActionAdapter
import com.mercadopago.android.px.internal.features.payment_result.instruction.model.InstructionActionModel
import com.mercadopago.android.px.internal.view.AdapterLinearLayout
import com.mercadopago.android.px.internal.view.MPTextView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.argumentCaptor
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class InstructionInteractionTest : BasicRobolectricTest() {

    private lateinit var instructionInteraction: InstructionInteraction

    @Mock
    private lateinit var listener: InstructionActionAdapter.Listener

    @Before
    fun setUp() {
        val context = Mockito.spy(getContext())
        instructionInteraction = InstructionInteraction(context)
    }

    @Test
    fun initWithTitleShouldSetTitleAndOnlyTitleShouldBeVisible() {
        val title = "Test"
        val interaction = InstructionInteraction.Model(title, null, null, null)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            // Should only show title
            assertVisible(R.id.title)
            findViewById<MPTextView>(R.id.title).assertText(title)

            // All other views should be gone or empty
            assertGone(R.id.content) // Should not show content because its empty
            findViewById<AdapterLinearLayout>(R.id.actions).assertChildCount<View>(0) // Should have no actions
        }
    }

    @Test
    fun initWithContentShouldSetContentAndItShouldBeVisible() {
        val title = "Test"
        val content = "Content"
        val interaction = InstructionInteraction.Model(title, content, null, null)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            // Should show content
            assertVisible(R.id.content)
            findViewById<MPTextView>(R.id.content).assertText(content)

            // Actions should be empty
            findViewById<AdapterLinearLayout>(R.id.actions).assertChildCount<View>(0) // Should have no actions
        }
    }

    @Test
    fun initDefaultContentMaxLinesShouldBeGreaterThanOneWhenShowMultilineIsNull() {
        val title = "Test"
        val content = "Content"
        val interaction = InstructionInteraction.Model(title, content, null, null)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            val maxLines = findViewById<MPTextView>(R.id.content).maxLines
            Assert.assertTrue(maxLines > 1)
        }
    }

    @Test
    fun initShouldSetMaxLinesToBeGreaterThanOneWhenShowMultilineIsTrue() {
        val title = "Test"
        val content = "Content"
        val interaction = InstructionInteraction.Model(title, content, true, null)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            val maxLines = findViewById<MPTextView>(R.id.content).maxLines
            Assert.assertTrue(maxLines > 1)
        }
    }

    @Test
    fun initShouldSetMaxLinesToBeOneWhenShowMultilineIsFalse() {
        val title = "Test"
        val content = "Content"
        val interaction = InstructionInteraction.Model(title, content, false, null)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            val maxLines = findViewById<MPTextView>(R.id.content).maxLines
            maxLines.assertEquals(1)
        }
    }

    @Test
    fun initWithLinkActionShouldSetOnlyOneActionAndItShouldBeVisible() {
        val actionLbl = "Test"
        val url = "http://www.google.com.ar"
        val action = InstructionActionModel.Link(actionLbl, url)
        val interaction = InstructionInteraction.Model("Test", "Content", null, action)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction) {
            assertVisible(R.id.actions)
            with(findViewById<AdapterLinearLayout>(R.id.actions)) {
                assertChildCount<View>(1)
                with(getChildAt(0) as MeliButton) {
                    text.assertEquals(actionLbl)
                }
            }
        }
    }

    @Test
    fun clickingLinkActionShouldCallListener() {
        val actionLbl = "Test"
        val url = "http://www.google.com.ar"
        val action = InstructionActionModel.Link(actionLbl, url)
        val interaction = InstructionInteraction.Model("Test", "Content", null, action)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction.findViewById<AdapterLinearLayout>(R.id.actions).getChildAt(0) as MeliButton) {
            hasOnClickListeners().assertEquals(true)
            performClick()
            val captor = argumentCaptor<LinkAction>()
            Mockito.verify(listener).onLinkAction(captor.capture())
        }
    }

    @Test
    fun clickingCopyActionShouldCallListener() {
        val actionLbl = "Test"
        val url = "http://www.google.com.ar"
        val action = InstructionActionModel.Copy(actionLbl, url)
        val interaction = InstructionInteraction.Model("Test", "Content", null, action)
        instructionInteraction.init(interaction, listener)
        with(instructionInteraction.findViewById<AdapterLinearLayout>(R.id.actions).getChildAt(0) as MeliButton) {
            hasOnClickListeners().assertEquals(true)
            performClick()
            val captor = argumentCaptor<CopyAction>()
            Mockito.verify(listener).onCopyAction(captor.capture())
        }
    }
}
