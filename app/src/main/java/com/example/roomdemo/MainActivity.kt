package com.example.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.ItemsRowBinding
import com.example.roomdemo.databinding.PopupBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val employeeDao = (application as EmployeeApp).db.employeeDao()

        binding.addEmployee.setOnClickListener {
            addRecord(employeeDao)
        }
        lifecycleScope.launch {
            employeeDao.getAllEmployees().collect {
                val list = ArrayList(it)
                setUpRecyclerView(list, employeeDao)
            }
        }
    }

    fun addRecord(employeeDao: EmployeeDao) {
        val position = binding.position.text.toString()
        val name = binding.name.text.toString()

        if (name.isNotEmpty() && position.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insertEmployee(EmployeeEntity(name = name, email = position))

                binding.name.text.clear()
                binding.position.text.clear()
            }
        } else {
            Snackbar.make(this, binding.container, "something is empty", Snackbar.LENGTH_LONG)
                .show()
        }
    }


    fun setUpRecyclerView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao) {
        if (employeeList.isNotEmpty()) {
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = ItemAdapter(employeeList, { updateId ->
                updateRecondDialog(updateId, employeeDao)
            },
                { deleteId ->
                    deleteRecordDialog(deleteId, employeeDao)
                }
            )
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.GONE
        }

    }

    private fun updateRecondDialog(id: Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        updateDialog.setCancelable(false)
        val binding = PopupBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchOneEmployee(id).collect {
                binding.editTextTextPersonName.setText(it.name)
                binding.editTextTextPersonName2.setText(it.email)
            }
        }

        binding.save.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString()
            val email = binding.editTextTextPersonName2.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.updateEmployee(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "reocrd updated", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "please enter name or email", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.cancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }


    private fun deleteRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.deleteEmployee(EmployeeEntity(id))
                Toast.makeText(applicationContext, "record deleted successfully", Toast.LENGTH_LONG)
                    .show()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.setCancelable(false)

        alert.show()
    }
}