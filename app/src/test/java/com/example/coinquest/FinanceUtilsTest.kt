package com.example.coinquest

import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceUtilsTest {

    @Test
    fun `test health score with normal values`() {
        val income = 1000.0
        val expenses = 600.0
        // (400 / 1000) * 100 = 40%
        val result = FinanceUtils.calculateFinancialHealth(income, expenses)
        assertEquals(40, result)
    }

    @Test
    fun `test health score with zero income`() {
        val result = FinanceUtils.calculateFinancialHealth(0.0, 500.0)
        assertEquals(0, result)
    }

    @Test
    fun `test health score with expenses exceeding income`() {
        val result = FinanceUtils.calculateFinancialHealth(1000.0, 1200.0)
        assertEquals(0, result)
    }
}
