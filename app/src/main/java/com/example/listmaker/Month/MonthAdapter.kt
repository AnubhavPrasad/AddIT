package com.example.listmaker.Month

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.DAY.*
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.R

class MonthAdapter(var alldaysdia: Dialog) : RecyclerView.Adapter<MonthAdapter.MyViewHolder>() {
    lateinit var dialog: AlertDialog.Builder
    lateinit var del: String

    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var month = itemview.findViewById<TextView>(R.id.date)
        var value = itemview.findViewById<TextView>(R.id.text)
        var delete = itemview.findViewById<ImageView>(R.id.bt_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.monthrecycler_item, parent, false)
        alldaysdia.findViewById<RecyclerView>(R.id.alldays_recycler).layoutManager =
            LinearLayoutManager(parent.context)
        dialog = AlertDialog.Builder(parent.context)
        dialog.setTitle("Delete")
        alldaysdia.window!!.attributes.windowAnimations = R.style.DailogScale
        dialog.setMessage("Are you Sure? Days contributing to it will also be deleted.")
        dialog.create()
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return monthlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db =
            DatabaseHelper(holder.itemView.context)
        monthlist = db.monthread()
        val limit = db.limitread()
        holder.month.text = monthlist[position].month
        holder.value.text = "\u20B9" + monthlist[position].monthvalue
        holder.delete.setOnClickListener {
            del = holder.month.text.toString()
            dialog.show()
        }
        if (monthlist[position].monthvalue.toInt() > limit.monthwise_limit) {
            holder.value.setTextColor(Color.parseColor("#E22323"))
        } else {
            holder.value.setTextColor(Color.BLACK)
        }
        dialog.setNegativeButton("CLOSE") { d1, _ ->
            d1.dismiss()
        }
        dialog.setPositiveButton("OK") { d2, _ ->
            if (action_mode != null) {
                action_mode?.finish()
                action_mode = null
                mActionMode = null
            }
            db.monthdelspec(del)
            for (i in datelist) {
                if (i.month == del) {
                    db.delspecdays(i.month)
                }
            }
            datelist = db.readdata()
            daterecycler.adapter = MyAdapter(datelist, datedialog_del)
            monthlist = db.monthread()
            notifyDataSetChanged()
            d2.dismiss()
        }
        holder.itemView.setOnClickListener {
            val list = arrayListOf<Data>()
            datelist = db.readdata()
            for (i in datelist) {
                if (i.month == monthlist[position].month) {
                    list.add(i)
                }
            }
            val sortedlist=list.sortedWith(compareBy {it.date.take(2)})
//            for (i in 0 until list.size) {
//                for (j in i until list.size) {
//                    val f = list[i].date.take(2)
//                    val s = list[j].date.take(2)
//                    if (f > s) {
//                        val t = list[i]
//                        list[i] = list[j]
//                        list[j] = t
//                    }
//                }
//            }
            alldaysdia.findViewById<RecyclerView>(R.id.alldays_recycler).adapter =
                AllDaysAdapter(sortedlist)
            alldaysdia.show()

        }
    }
}