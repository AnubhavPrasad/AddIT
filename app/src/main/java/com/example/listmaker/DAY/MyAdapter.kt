package com.example.listmaker.DAY

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.MainTab.DatabaseHelper
import com.example.listmaker.MainTab.TabbedFragmentDirections
import com.example.listmaker.Month.MonthAdapter
import com.example.listmaker.Month.dia_alldays
import com.example.listmaker.Month.monthrecycler
import com.example.listmaker.R

lateinit var items: MutableList<ItemData>
var action_mode: ActionMode? = null
var mActionMode: ActionMode? = null

class MyAdapter(
    var list: MutableList<Data>,
    var dialog: AlertDialog.Builder

) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(), ActionMode.Callback {
    lateinit var monthdel: String

    lateinit var itemsdia: Dialog
    lateinit var itemrec: RecyclerView
    lateinit var multiple_del: MutableList<MultipleDel>


    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var value = itemview.findViewById<TextView>(R.id.text)
        var bt = itemview.findViewById<ImageView>(R.id.bt_delete)
        val date = itemview.findViewById<TextView>(R.id.date)
        val checkBox = itemview.findViewById<CheckBox>(R.id.bt_check)
        val pie = itemview.findViewById<ImageView>(R.id.pie_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        itemsdia = Dialog(parent.context)
        itemsdia.setContentView(R.layout.all_items)
        itemrec = itemsdia.findViewById(R.id.items_recycler)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (list.size == 0) {
            dayitemrec.visibility = View.GONE
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

        if (mActionMode == null) {
            holder.bt.visibility = View.VISIBLE
            holder.itemView.isSelected = false
            holder.checkBox.isChecked = false
            holder.checkBox.visibility = View.GONE
        } else {
            holder.bt.visibility = View.INVISIBLE
            holder.checkBox.visibility = View.VISIBLE
        }
        holder.value.text = "\u20B9 " + list[position].value
        holder.date.text = list[position].date
        if (list[position].value.toInt() > limit.daywise_limit) {
            holder.value.setTextColor(Color.parseColor("#E22323"))
        } else {
            holder.value.setTextColor(Color.BLACK)
        }
        holder.bt.setOnClickListener {
            multiple_del = mutableListOf()
            multiple_del.add(
                MultipleDel(
                    list[position].date,
                    list[position].month,
                    list[position].value
                )
            )

            dialog.show()
        }
        holder.pie.setOnClickListener {
            if (mActionMode == null) {
                items = db.readitems(holder.date.text.toString())
                holder.itemView.findNavController()
                    .navigate(TabbedFragmentDirections.actionTabbedFragmentToPieFragment2(holder.date.text.toString()))
            }
        }
        holder.itemView.setOnLongClickListener {
            multiple_del= mutableListOf()
            val onlong=false
            if(mActionMode==null){
                mActionMode=holder.itemView.startActionMode(this)
                notifyDataSetChanged()
            }
            onlong
        }
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                multiple_del.add(
                    MultipleDel(
                        list[position].date,
                        list[position].month,
                        list[position].value
                    )
                )
                holder.itemView.isSelected = true
            } else {
                multiple_del.remove(
                    MultipleDel(
                        list[position].date,
                        list[position].month,
                        list[position].value
                    )
                )
                holder.itemView.isSelected = false
            }
        }
        holder.itemView.setOnClickListener {
            items = db.readitems(holder.date.text.toString())
            if (mActionMode != null && !holder.itemView.isSelected) {
                holder.checkBox.isChecked = true
                holder.itemView.isSelected = true
            } else if (mActionMode != null && holder.itemView.isSelected) {
                holder.checkBox.isChecked = false
                holder.itemView.isSelected = false
            } else {
                itemrec.layoutManager = LinearLayoutManager(holder.itemView.context)
                itemrec.adapter = ItemsAdapter(
                    items,
                    holder.date.text.toString(),
                    list[position].value,
                    list[position].month
                )
                itemsdia.show()
            }
        }

        dialog.setPositiveButton("YES") { dialog, _ ->
            for (i in monthlist) {
                var sum = 0
                for (j in multiple_del) {
                    if (i.month == j.month) {
                        sum += j.value.toInt()
                    }
                }
                db.deductmonth(i.monthvalue, sum.toString(), i.month)
            }

            monthrecycler.adapter = MonthAdapter(
                dia_alldays
            )
            for (i in multiple_del) {
                db.itemdel(i.date)
                db.deletespec(i.date)
            }
            list = db.readdata()
            notifyDataSetChanged()
            db.close()
            if (list.size == 0) {
                dayitemrec.visibility = View.GONE
                add_txt.visibility = View.VISIBLE
            }
            dialog.dismiss()
            if (action_mode != null) {
                action_mode?.finish()
                action_mode = null
                mActionMode = null
            }
        }
        dialog.setNegativeButton("CLOSE") { dialog, _ ->
            dialog.dismiss()
        }

    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sel_del -> {
                dialog.show()

            }
        }
        return true
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.contextual_menu, menu)
        action_mode = mode
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        mActionMode = null
        multiple_del = mutableListOf()
        notifyDataSetChanged()
    }

}