package com.quickbite.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.quickbite.databinding.ActivitySalesReportsBinding

class SalesReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Sales Reports"

        setupDateRangeSpinner()
        setupChart()
        loadSalesData()

        binding.btnExport.setOnClickListener {
            exportReport()
        }
    }

    private fun setupDateRangeSpinner() {
        val dateRanges = arrayOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateRanges)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDateRange.adapter = adapter
        binding.spinnerDateRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadSalesData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupChart() {
        binding.salesChart.description.isEnabled = false
        binding.salesChart.setDrawGridBackground(false)
        binding.salesChart.setDrawBarShadow(false)
        binding.salesChart.isHighlightFullBarEnabled = false

        val xAxis = binding.salesChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawLabels(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("", "Jan", "Feb", "Mar")) // Replace with your labels

        val leftAxis = binding.salesChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.setDrawAxisLine(true)
        leftAxis.axisMinimum = 0f

        binding.salesChart.axisRight.isEnabled = false
        binding.salesChart.legend.isEnabled = true
    }

    private fun loadSalesData() {
        // TODO: Fetch sales data from Firestore based on the selected date range
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 100f))
        entries.add(BarEntry(2f, 200f))
        entries.add(BarEntry(3f, 150f))

        val dataSet = BarDataSet(entries, "Sales")
        dataSet.color = Color.BLUE
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        binding.salesChart.data = barData
        binding.salesChart.invalidate()
    }

    private fun exportReport() {
        // TODO: Implement PDF/Excel export functionality
        Toast.makeText(this, "Exporting report...", Toast.LENGTH_SHORT).show()
    }
}