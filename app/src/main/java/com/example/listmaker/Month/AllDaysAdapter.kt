package com.example.listmaker.Month

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.DAY.Data
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.R

class AllDaysAdapter(var list: MutableList<Data>) :
    RecyclerView.Adapter<AllDaysAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date = itemView.findViewById<TextView>(R.id.date_alldays)
        var datevalue = itemView.findViewById<TextView>(R.id.alldates_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alldays_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val date = list[position].date.take(6)
        val db=
            DatabaseHelper(holder.itemView.context)
        val limit=db.limitread()
        holder.date.text = date
        if(list[position].value.toInt()>limit.daywise_limit ){
            holder.datevalue.setTextColor(Color.parseColor("#E22323"))
        }else{
            holder.date.setTextColor(Color.BLACK)
        }
        holder.datevalue.text = "₹ " + list[position].value

    }
}