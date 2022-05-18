package com.mercadopago.android.px.internal.features.generic_modal

import com.mercadopago.android.px.model.ExpressMetadata
import com.mercadopago.android.px.model.StatusMetadata
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ActionTypeWrapperTest {

    @Test
    fun testWhenHasActiveMethods_thenReturnIndexZeroAndActionPayWithOtherMethod() {
        val expressMetadataList = mutableListOf<ExpressMetadata>().also {
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.ACTIVE).isNewCard(false).isOfflineMethod(false).build())
        }
        val actionTypeWrapper = ActionTypeWrapper(expressMetadataList)
        Assert.assertEquals(0, actionTypeWrapper.indexToReturn)
        Assert.assertEquals(ActionType.PAY_WITH_OTHER_METHOD, actionTypeWrapper.actionType)
    }

    @Test
    fun testWhenIndexOfflineMethod_GTZero_thenReturn_indexAndPayWithOfflineMethod() {
        val expressMetadataList = mutableListOf<ExpressMetadata>().also {
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.SUSPENDED).isNewCard(false).isOfflineMethod(false).build())
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.ACTIVE).isNewCard(false).isOfflineMethod(true).build())
        }
        val actionTypeWrapper = ActionTypeWrapper(expressMetadataList)
        Assert.assertEquals(1, actionTypeWrapper.indexToReturn)
        Assert.assertEquals(ActionType.PAY_WITH_OFFLINE_METHOD, actionTypeWrapper.actionType)
    }

    @Test
    fun testWhenIndexNewCard_GTZero_thenReturn_indexAndAddNewCard() {
        val expressMetadataList = mutableListOf<ExpressMetadata>().also {
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.SUSPENDED).isNewCard(false).isOfflineMethod(false).build())
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.ACTIVE).isNewCard(true).isOfflineMethod(false).build())
        }
        val actionTypeWrapper = ActionTypeWrapper(expressMetadataList)
        Assert.assertEquals(1, actionTypeWrapper.indexToReturn)
        Assert.assertEquals(ActionType.ADD_NEW_CARD, actionTypeWrapper.actionType)
    }

    @Test
    fun testWhenNoActiveMethodNoNewCardAndNoOfflineMethod_thenReturnIndexZeroAndActionPayWithOtherMethod() {
        val expressMetadataList = mutableListOf<ExpressMetadata>().also {
            it.add(ExpressMockBuilder().withStatus(StatusMetadata.Detail.SUSPENDED).isNewCard(false).isOfflineMethod(false).build())
        }
        val actionTypeWrapper = ActionTypeWrapper(expressMetadataList)
        Assert.assertEquals(0, actionTypeWrapper.indexToReturn)
        Assert.assertEquals(ActionType.PAY_WITH_OTHER_METHOD, actionTypeWrapper.actionType)
    }

    private class ExpressMockBuilder {
        private val expressMetadata: ExpressMetadata = mock(ExpressMetadata::class.java)

        fun withStatus(@StatusMetadata.Detail detail: String) = this.apply {
            val status = mock(StatusMetadata::class.java)
            `when`(status.isActive).thenReturn(detail == StatusMetadata.Detail.ACTIVE)
            `when`(expressMetadata.status).thenReturn(status)
        }

        fun isOfflineMethod(boolean: Boolean) = this.apply {
            `when`(expressMetadata.isOfflineMethods).thenReturn(boolean)
        }

        fun isNewCard(boolean: Boolean) = this.apply {
            `when`(expressMetadata.isNewCard).thenReturn(boolean)
        }

        fun build() = expressMetadata
    }
}