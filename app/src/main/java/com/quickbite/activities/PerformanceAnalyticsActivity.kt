package com.quickbite.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.quickbite.databinding.ActivityPerformanceAnalyticsBinding

class PerformanceAnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerformanceAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerformanceAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Performance Analytics"

        setupMostOrderedItemsChart()
        setupPeakHoursChart()
        loadPerformanceData()
    }

    private fun setupMostOrderedItemsChart() {
        binding.mostOrderedItemsChart.setUsePercentValues(true)
        binding.mostOrderedItemsChart.description.isEnabled = false
        binding.mostOrderedItemsChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.mostOrderedItemsChart.dragDecelerationFrictionCoef = 0.95f
        binding.mostOrderedItemsChart.isDrawHoleEnabled = true
        binding.mostOrderedItemsChart.holeColor = Color.WHITE
        binding.mostOrderedItemsChart.transparentCircleRadius = 61f
        binding.mostOrderedItemsChart.legend.isEnabled = false
    }

    private fun setupPeakHoursChart() {
        binding.peakHoursChart.description.isEnabled = false
    }

    private fun loadPerformanceData() {
        // TODO: Fetch performance data from Firestore
        val mostOrderedEntries = ArrayList<PieEntry>()
        mostOrderedEntries.add(PieEntry(30f, "Item 1"))
        mostOrderedEntries.add(PieEntry(20f, "Item 2"))
        mostOrderedEntries.add(PieEntry(50f, "Item 3"))

        val mostOrderedDataSet = PieDataSet(mostOrderedEntries, "Most Ordered Items")
        mostOrderedDataSet.sliceSpace = 3f
        mostOrderedDataSet.selectionShift = 5f
        mostOrderedDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val mostOrderedData = PieData(mostOrderedDataSet)
        mostOrderedData.setValueFormatter(PercentFormatter())
        mostOrderedData.setValueTextSize(10f)
        mostOrderedData.setValueTextColor(Color.WHITE)
        binding.mostOrderedItemsChart.data = mostOrderedData
        binding.mostOrderedItemsChart.invalidate()

        // TODO: Load data for peak hours chart
        binding.tvAverageOrderValue.text = "Average Order Value: $15.50"
        binding.tvCustomerRetentionRate.text = "Customer Retention Rate: 85%"
    }
}