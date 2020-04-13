package com.example.listmaker.DAY

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.listmaker.R
import com.example.listmaker.databinding.FragmentPieBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate


class PieFragment : Fragment() {
    lateinit var binding: FragmentPieBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pie, container, false)
        val pieChart = binding.pieChart
        val pientries = ArrayList<Entry>()
        val xval=ArrayList<String>()
        val args= arguments?.let { PieFragmentArgs.fromBundle(it) }
        if (args != null) {
            pieChart.setDescription("Money Invested:${args.date}")
        }
        pieChart.setDescriptionTextSize(15f)
        pieChart.holeRadius=8f
        pieChart.setHoleColor(Color.WHITE)
        pieChart.transparentCircleRadius=10f
        for(i in 0 until items.size){
            pientries.add(Entry(items[i].itemprice.toFloat(),i))
            xval.add(i,items[i].item)
        }

        val pieDataSet=PieDataSet(pientries,"")
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        val piedata=PieData(xval,pieDataSet)
        piedata.setValueTextColor(Color.WHITE)
        pieDataSet.sliceSpace=3f
        piedata.setValueTextSize(13f)
        pieChart.data=piedata
        pieDataSet.sliceSpace=2f
        pieChart.invalidate()
        return binding.root
    }

}
