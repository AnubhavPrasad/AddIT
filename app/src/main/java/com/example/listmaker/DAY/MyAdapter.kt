package com.example.listmaker.DAY

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.Month.MonthAdapter
import com.example.listmaker.Month.dia_alldays
import com.example.listmaker.Month.monthrecycler
import com.example.listmaker.R
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.bottomsheet.BottomSheetDialog
lateinit var items:MutableList<ItemData>
class MyAdapter(
    var list: MutableList<Data>,
    var dialog: AlertDialog.Builder

) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    lateinit var del: String
    lateinit var itemsdia:Dialog
    lateinit var itemrec:RecyclerView
    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var value = itemview.findViewById<TextView>(R.id.text)
        var bt = itemview.findViewById<ImageView>(R.id.bt_delete)
        val date = itemview.findViewById<TextView>(R.id.date)
        val pie=itemview.findViewById<ImageView>(R.id.pie_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        itemsdia= Dialog(parent.context)
        itemsdia.setContentView(R.layout.all_items)
        itemrec=itemsdia.findViewById(R.id.items_recycler)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (list.size == 0) {
            itemrec.visibility = View.GONE
            add_txt.visibility = View.VISIBLE
        }
        return list.size
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db =
            DatabaseHelper(holder.itemView.context)
        datelist = db.readdata()
        val limit = db.limitread()
        holder.value.text = "\u20B9 " + list[position].value
        holder.date.text = list[position].date
        if (list[position].value.toInt() > limit.daywise_limit) {
            holder.value.setTextColor(Color.parseColor("#E22323"))
        } else {
            holder.value.setTextColor(Color.BLACK)
        }

        holder.bt.setOnClickListener {
            del = holder.date.text.toString()
            dialog.show()
        }
        holder.itemView.setOnClickListener {
            items=db.readitems(holder.date.text.toString())
            itemrec.layoutManager=LinearLayoutManager(holder.itemView.context)
            itemrec.adapter=ItemsAdapter(items,holder.date.text.toString(),list[position].value,list[position].month)
            itemsdia.show()
        }
        holder.pie.setOnClickListener {
            items=db.readitems(holder.date.text.toString())
            holder.itemView.findNavController().navigate(R.id.action_tabbedFragment_to_pieFragment2)
        }
        dialog.setPositiveButton("YES") { dialog, _ ->
            for (i in monthlist) {
                if (i.month == list[position].month) {
                    db.deductmonth(i.monthvalue, list[position].value, i.month)

                }
            }
            monthlist = db.monthread()
            monthrecycler.adapter = MonthAdapter(
                dia_alldays
            )
            db.itemdel(del)
            db.readitems(holder.date.text.toString())
            db.deletespec(del)
            list = db.readdata()
            notifyDataSetChanged()
            db.close()
            dialog.dismiss()
        }
        dialog.setNegativeButton("CLOSE") { dialog, _ ->
            dialog.dismiss()
        }

    }

}