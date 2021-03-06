package com.example.listmaker.MainTab

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.listmaker.DAY.*
import com.example.listmaker.MainActivity
import com.example.listmaker.Month.MonthAdapter
import com.example.listmaker.Month.dia_alldays
import com.example.listmaker.Month.monthrecycler
import com.example.listmaker.R
import com.example.listmaker.databinding.FragmentTabbedBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_main_page.*
import java.text.SimpleDateFormat

var outputdate: String = ""
var outputmonth: String = ""

class TabbedFragment : Fragment() {
    lateinit var binding: FragmentTabbedBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tabbed, container, false
        )
        binding.maintoolbar.inflateMenu(R.menu.menu)
        val dia_delmonth = AlertDialog.Builder(context)
        dia_delmonth.create()
        dia_delmonth.setTitle("Delete All")
        dia_delmonth.setMessage("Are you sure ?")
        val limit_dialog = Dialog(context!!)
        limit_dialog.setContentView(R.layout.limit_dialog)
        binding.viewpager.adapter = PagerAdapter(
            childFragmentManager
        )
        binding.tabalyout.setupWithViewPager(binding.viewpager)
        val db = DatabaseHelper(
            context!!
        )
        datelist = db.readdata()
        binding.maintoolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    dia_delmonth.show()

                }
                R.id.about -> {
                    startActivity(
                        Intent(
                            context,
                            AboutActivity::class.java
                        )
                    )
                }
                R.id.limit -> {
                    val limit = db.limitread()
                    if (limit.daywise_limit != Int.MAX_VALUE && limit.monthwise_limit != Int.MAX_VALUE) {
                        limit_dialog.findViewById<EditText>(R.id.daylimit)
                            .setText(limit.daywise_limit.toString())
                        limit_dialog.findViewById<EditText>(R.id.monthlimit)
                            .setText(limit.monthwise_limit.toString())
                    }
                    limit_dialog.show()
                }
                R.id.add_particular -> {
                    bottom_sheetdia.findViewById<EditText>(
                        R.id.item_et
                    )?.setText("")
                    bottom_sheetdia.findViewById<EditText>(R.id.itemprice_add)?.setText("")
                    bottom_sheetdia.findViewById<FloatingActionButton>(R.id.bt_update)?.visibility=View.GONE
                    bottom_sheetdia.findViewById<FloatingActionButton>(R.id.bt_add)?.visibility=View.VISIBLE
                    val datePickerDialog = DatePickerDialog(context!!)
                    datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                        val inputstr = "$dayOfMonth-${month + 1}-$year"
                        val inputfor = SimpleDateFormat("d-M-yyyy")
                        val outputfor = SimpleDateFormat("dd-MMM-yyyy")
                        val date = inputfor.parse(inputstr)
                        outputdate = outputfor.format(date)
                        val inputmonth = "${month + 1}-$year"
                        val inputform = SimpleDateFormat("M-yyyy")
                        val outform = SimpleDateFormat("MMMM-yyyy")
                        val month = inputform.parse(inputmonth)
                        outputmonth = outform.format(month)
                        bottom_sheetdia.show()
                    }
                    datePickerDialog.show()
                }
                R.id.contact -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf("anubhavprasad89@gmail.com")
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.type = "text/html"
                    intent.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(intent, "Send mail"))
                }
            }
            true
        }
        dia_delmonth.setPositiveButton("OK") { d1, _ ->
            db.deletedata()
            db.monthdel()
            db.delallitem()
            datelist = db.readdata()
            monthlist = db.monthread()
            monthrecycler.adapter = MonthAdapter(
                dia_alldays
            )
            daterecycler.adapter = MyAdapter(
                datelist,
                datedialog_del
            )
            if (datelist.size == 0) {
                dayitemrec.visibility = View.GONE
                add_text.visibility = View.VISIBLE
            }
            d1.dismiss()
        }
        dia_delmonth.setNegativeButton("CLOSE") { d2, _ ->
            d2.dismiss()
        }
        limit_dialog.findViewById<Button>(R.id.set_bt).setOnClickListener {
            if (limit_dialog.findViewById<EditText>(R.id.daylimit).text.toString() != "" && limit_dialog.findViewById<EditText>(
                    R.id.monthlimit
                ).text.toString() != ""
            ) {
                val daywise_limit =
                    limit_dialog.findViewById<EditText>(R.id.daylimit).text.toString().toInt()
                val monthwise_limit =
                    limit_dialog.findViewById<EditText>(R.id.monthlimit).text.toString().toInt()
                val limit = Limit(
                    daywise_limit,
                    monthwise_limit
                )
                db.limitinsert(limit)
                monthrecycler.adapter = MonthAdapter(
                    dia_alldays
                )
                daterecycler.adapter = MyAdapter(
                    datelist,
                    datedialog_del
                )
                limit_dialog.dismiss()
            } else {
                Toast.makeText(context, "Enter Limit", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }



}
