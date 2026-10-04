package com.example

import com.example.domain.model.ToolRegistry
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class LifeHubUnitTest {

    @Test
    fun testToolRegistryIntegrity() {
        assertEquals(10, ToolRegistry.categories.size)
        assertEquals(100, ToolRegistry.allTools.size)

        // Check each category has exactly 10 tools
        ToolRegistry.categories.forEach { category ->
            val count = ToolRegistry.allTools.count { it.categoryId == category.id }
            assertEquals("Category ${category.name} should have 10 tools", 10, count)
        }
    }

    @Test
    fun testGSTCalculation() {
        val amount = 1000.0
        val rate = 18.0
        val gstAmount = (amount * rate) / 100.0
        val total = amount + gstAmount
        assertEquals(180.0, gstAmount, 0.001)
        assertEquals(1180.0, total, 0.001)
    }

    @Test
    fun testEMICalculation() {
        val p = 100000.0
        val annualRate = 12.0
        val n = 12.0 // 1 year
        val r = (annualRate / 12) / 100
        val emi = (p * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        assertTrue(emi > 8800.0 && emi < 8900.0)
    }

    @Test
    fun testMileageCalculation() {
        val distanceKm = 400.0
        val fuelLiters = 20.0
        val mileage = distanceKm / fuelLiters
        assertEquals(20.0, mileage, 0.001)
    }

    @Test
    fun testUnitConverterLength() {
        val meters = 10.0
        val feet = meters / 0.3048
        assertEquals(32.8084, feet, 0.01)
    }
}
