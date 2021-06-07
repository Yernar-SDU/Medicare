package com.example.medicare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import com.example.medicare.model.LimbService
import com.example.medicare.model.Part
import com.example.medicare.model.PartExtended
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PartAdapter(val partList: MutableList<Part>, val servicesId: Int,val context: Context)
    : RecyclerView.Adapter<PartAdapter.PartViewHolder>() {
    val medicareAPI : MedicareAPI = NetworkService.medicareAPI

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val root: View = LayoutInflater.from(parent.context).inflate(R.layout.part_item,parent,false)
        return PartViewHolder(root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        holder.partId.text = partList[position].id.toString()
        holder.part_name.text = partList[position].name + " " + partList[position].code
        if(partList[position].count == 0){
            holder.part_number.setText("0")
        }
        holder.part_number.setText("${partList[position].count}")
        val limb_id = partList[position].limb_id

//        medicareAPI.
        var limbService: List<LimbService>? = null

        medicareAPI.getPartSizeByIDs(servicesId,limb_id).enqueue(object : Callback<List<LimbService>>{
            override fun onResponse(call: Call<List<LimbService>>, response: Response<List<LimbService>>) {
                if(response.isSuccessful){
                    limbService = response.body()
                    if(limbService!!.isEmpty()){
                        holder.part_size.setText("0")
                    }else{
                        holder.part_size.setText(limbService!![0].size)
                    }

                }
            }

            override fun onFailure(call: Call<List<LimbService>>, t: Throwable) {
                Log.i("hello", "onFailure: ${t.message}}")
            }
        })

        holder.deleteButton.setOnClickListener {
            partList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return partList.size
    }


    class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partId: TextView = itemView.findViewById(R.id.part_id)
        val part_name : TextView = itemView.findViewById(R.id.part_name)
        val part_number: EditText = itemView.findViewById(R.id.part_number)
        val part_size: EditText = itemView.findViewById(R.id.part_size)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)


    }
    interface OnClickListener{
        fun onClick(position: Int, part: Part)
    }
}