package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.PayerCompliance

// We keep this even when it's empty to avoid using AbstractLocalRepository where we need this
internal interface PayerComplianceRepository : LocalRepository<PayerCompliance?>
