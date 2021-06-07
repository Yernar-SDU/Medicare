package com.example.medicare.adapter

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import com.example.medicare.model.Category


class CategoriesAdapter(context: Context, resource: Int, list: List<Category>): ArrayAdapter<Category>(context,resource,list) {
    var resource: Int = 0
    var list: List<Category> = arrayListOf()
    lateinit var view: LayoutInflater

    init {
        this.resource = resource
        this.list = list
        this.view = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val root: View = LayoutInflater.from(parent.context).inflate(R.layout.custom_spinner_item,parent,false)
        var holder: ViewHolder
        var newView: View = view.inflate(resource,null)
        if (convertView == null){
            holder = ViewHolder(root)
            holder.id.text = list[position].id.toString()
            holder.name.text = list[position].name
            holder.parent_id.text = list[position].parent_id.toString()
            newView.tag = holder
        }
        return newView
    }

    internal class ViewHolder(itemView: View){
        val name: TextView = itemView.findViewById(R.id.name)
        val parent_id: TextView = itemView.findViewById(R.id.parent_id)
        val id: TextView = itemView.findViewById(R.id.id)
    }


}
