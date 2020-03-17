package com.example.listmaker


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.databinding.FragmentMainPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

lateinit var monthlist: MutableList<MonthData>
lateinit var datelist: MutableList<Data>
class MainPage() : Fragment() {
    lateinit var binding: FragmentMainPageBinding
    private var listvalue = ""
    @SuppressLint("ResourceAsColor", "RestrictedApi", "SimpleDateFormat")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_page, container, false)
        val dia = BottomSheetDialog(context!!)
        val db = DatabaseHelper(context!!)

        monthlist = db.monthread()
        val dialog = AlertDialog.Builder(context)
        dialog.create()
        dialog.setTitle("Delete")
        dialog.setMessage("Are you sure? It will automatically deduct from month.")
        datelist = db.readdata()
        binding.recycler.layoutManager = LinearLayoutManager(context!!)
        dia.setContentView(R.layout.dialog_layout)
        binding.recycler.adapter = MyAdapter(datelist, dialog, dia)
        binding.floatingActionButton.setOnClickListener {
            dia.findViewById<TextView>(R.id.textView)?.text="ADD"
            dia.findViewById<EditText>(R.id.et_Add)?.setText("")
            dia.findViewById<FloatingActionButton>(R.id.bt_add)?.visibility = View.VISIBLE
            dia.findViewById<FloatingActionButton>(R.id.bt_update)?.visibility = View.GONE
            dia.show()
        }

        dia.findViewById<ImageView>(R.id.close)?.setOnClickListener {
            Log.i("i", "close")
            dia.dismiss()
        }
        dia.findViewById<FloatingActionButton>(R.id.bt_add)?.setOnClickListener {
            Log.i("i", "called11122")
            listvalue = dia.findViewById<EditText>(R.id.et_Add)?.text.toString()
            if (listvalue.length > 0) {
                Log.i("i", "called111")
                datelist = db.readdata()
                monthlist = db.monthread()
                val getdate = Calendar.getInstance().time
                val dateformat = SimpleDateFormat("dd-MMM-yyyy")
                val date = dateformat.format(getdate)
                val datemonthfor = SimpleDateFormat("MMMM")
                val datemonth = datemonthfor.format(getdate)
                val monthData = MonthData(0, listvalue, datemonth)
                val data = Data(listvalue, date,datemonth)
                var j = 0
                var k = 0
                for (i in datelist) {
                    if (i.date == date) {
                        Log.i("i", "called")
                        db.updatedata2(i.value, listvalue, date)
                        j += 1
                        break
                    }
                }
                for (i in monthlist) {
                    if (i.month == datemonth) {
                        db.monthupdate(i.monthvalue, listvalue, datemonth)
                        k += 1
                        break
                    }
                }
                if (j == 0) {
                    db.insertdata(data)
                }
                if (k == 0) {
                    db.insertmonth(monthData)
                }
                datelist = db.readdata()
                monthlist=db.monthread()
                monthrecycler.adapter=MonthAdapter(dia_alldays)
                binding.recycler.adapter = MyAdapter(datelist, dialog, dia)
                dia.dismiss()
            } else {
                Toast.makeText(context, "Enter Something", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }


}
