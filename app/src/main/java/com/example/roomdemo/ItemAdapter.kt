package com.example.roomdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ItemsRowBinding

class ItemAdapter(
    private val items: ArrayList<EmployeeEntity>,
    private val updateListener: (id: Int) -> Unit,
    private val deleteListener: (id: Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val row = binding.listItem
        val personName = binding.name
        val email = binding.email
        val delete = binding.deleteBtn
        val edit = binding.editBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.personName.text = item.name
        holder.email.text = item.email

        if(position%2==0){
            holder.row.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.edit.setOnClickListener{
            //invoke function is used to call callback functions
            updateListener.invoke(item.id)
        }

        holder.delete.setOnClickListener{
            deleteListener.invoke(item.id)
        }
    }

    override fun getItemCount(): Int {
        return  items.size
    }
}