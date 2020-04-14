package com.example.listmaker.Month

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.DAY.Data
import com.example.listmaker.DAY.datelist
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.R

class AllDaysAdapter(var list: List<Data>) :
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
        val db=
            DatabaseHelper(holder.itemView.context)
       // Log.i("all",list[position].value)
        //Log.i("all", datelist[position].value)
        val limit=db.limitread()
        holder.date.text = list[position].date
        if(list[position].value.toInt()>limit.daywise_limit ){
            holder.datevalue.setTextColor(Color.parseColor("#E22323"))
        }else{
            holder.date.setTextColor(Color.BLACK)
        }
        holder.datevalue.text = "₹ " + list[position].value

    }
}