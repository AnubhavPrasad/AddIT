package com.example.listmaker.DAY

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.Month.MonthAdapter
import com.example.listmaker.Month.dia_alldays
import com.example.listmaker.Month.monthrecycler
import com.example.listmaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemsAdapter(
    var list: MutableList<ItemData>,
    var date: String,
    var mainvalue: String,
    var month: String
) :
    RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {
    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var item = itemview.findViewById<TextView>(R.id.item_txt)
        var item_price = itemview.findViewById<TextView>(R.id.item_price_txt)
        val delete = itemview.findViewById<Button>(R.id.delete_item)
    }

    lateinit var item_price: String
    lateinit var item: String
    lateinit var dialog: AlertDialog.Builder
    var mActionMode: ActionMode? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.all_items_rec_item, parent, false)
        dialog = AlertDialog.Builder(parent.context)
        dialog.setTitle("Delete Item")
        dialog.setMessage("Are You Sure ?")
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.item.text = list[position].item
        holder.item_price.text = list[position].itemprice
        holder.itemView.setOnClickListener {
            bottom_sheetdia.show()
            val previtem = holder.item.text.toString()
            val previtemprice = holder.item_price.text.toString()
            val item = bottom_sheetdia.findViewById<EditText>(R.id.item_et)
            val item_price = bottom_sheetdia.findViewById<EditText>(R.id.itemprice_et)
            item?.setText(previtem)
            item_price?.setText(previtemprice)
            bottom_sheetdia.findViewById<FloatingActionButton>(R.id.bt_add)?.visibility = View.GONE
            bottom_sheetdia.findViewById<FloatingActionButton>(R.id.bt_update)?.visibility =
                View.VISIBLE
            bottom_sheetdia.findViewById<EditText>(R.id.itemprice_et)?.visibility=View.VISIBLE
            bottom_sheetdia.findViewById<EditText>(R.id.itemprice_add)?.visibility=View.INVISIBLE
            val bt = bottom_sheetdia.findViewById<FloatingActionButton>(R.id.bt_update)
            bottom_sheetdia.findViewById<TextView>(R.id.textView)?.text = "EDIT"
            bt?.setOnClickListener {
                updatedata(previtem, previtemprice, item, item_price, holder.itemView.context)
            }
            item_price?.setOnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
                if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    updatedata(previtem, previtemprice, item, item_price, holder.itemView.context)
                    true
                } else {
                    false
                }
            }
        }
        holder.delete.setOnClickListener {
            dialog.show()
            item_price = holder.item_price.text.toString()
            item = holder.item.text.toString()
        }
        dialog.setPositiveButton("YES") { dialog, _ ->
            val db = DatabaseHelper(holder.itemView.context)
            db.itemdelspec(item, item_price)
            list = db.readitems(date)
            notifyDataSetChanged()
            for(i in datelist){
                if(i.date==date){
                    mainvalue=i.value
                }
            }
            db.deductitem(item_price, date, mainvalue)
            datelist = db.readdata()
            daterecycler.adapter = MyAdapter(datelist, datedialog_del)
            for (i in monthlist) {
                if (i.month == month) {
                    db.editmonth(
                        i.month,
                        item_price,
                        "0",
                        i.monthvalue
                    )
                }
            }
            monthlist = db.monthread()
            monthrecycler.adapter = MonthAdapter(dia_alldays)
            dialog.dismiss()
        }
        dialog.setNegativeButton("CLOSE") { dialog, _ ->
            dialog.dismiss()
        }

    }

    fun updatedata(
        previtem: String,
        previtemprice: String,
        item: EditText?,
        item_price: EditText?,
        context: Context
    ) {
        if (item?.text.toString() != "" && item_price?.text.toString() != "") {
            val db = DatabaseHelper(context)
            db.updateitem(
                previtem,
                previtemprice,
                item_price?.text.toString(),
                item?.text.toString()
            )
            for(i in datelist){
                if(i.date==date){
                    mainvalue=i.value
                }
            }
            db.editdata(previtemprice, item_price?.text.toString(), mainvalue, date)
            datelist = db.readdata()
            for (i in monthlist) {
                if (i.month == month) {
                    db.editmonth(
                        i.month,
                        previtemprice,
                        item_price?.text.toString(),
                        i.monthvalue
                    )
                }
            }
            monthlist = db.monthread()
            monthrecycler.adapter = MonthAdapter(dia_alldays)
            daterecycler.adapter = MyAdapter(datelist, datedialog_del)
            list = db.readitems(date)
            notifyDataSetChanged()
            bottom_sheetdia.dismiss()
        } else {
            Toast.makeText(context, "Enter Something", Toast.LENGTH_SHORT)
                .show()
        }
    }  //Editing the item data
}
