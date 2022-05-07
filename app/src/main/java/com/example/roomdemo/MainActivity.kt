package com.example.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.PopupBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view binding stuff
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //creating database access object's (DAO) object
        val employeeDao = (application as EmployeeApp).db.employeeDao()

        //adding event listener to add record button
        binding.addEmployee.setOnClickListener {
            addRecord(employeeDao)
        }

        //launching coroutine for getting all employees from database
        lifecycleScope.launch {
            //collecting the flow returned from getAllEmployees method using .collect method
            //@Query annotation always returns a flow
            // you don't need to worry about them just collect them inside coroutine
            //they will return the value whatever type was declared within Flow<T>
            employeeDao.getAllEmployees().collect {
                val list = ArrayList(it)
                setUpRecyclerView(list, employeeDao)
            }
        }
    }



    //taking employeeList and employeeDao object which is used to pass to
    //the callback functions given to adapter
    fun setUpRecyclerView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao) {
        if (employeeList.isNotEmpty()) {
            //assigning layout manage to recycler view
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            //passing adapter to recycler view--- with employeeList,and two callbacks
            //first for updating the data
            //second for deleting the data
            binding.recyclerView.adapter = ItemAdapter(employeeList, { updateId ->
                updateRecordDialog(updateId, employeeDao)
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

    //function for updating employee
    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        //creating out custom dialog and inflating some default layout file 'Theme_AppCompat_Dialog'
        val updateDialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        //setting that dialog can't be dismissed by clicking outside
        updateDialog.setCancelable(false)
        //using view binding for custom popup named popup.xnl
        val binding = PopupBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        //launching coroutine for getting one employee from db
        lifecycleScope.launch {
            //getting one employee from database using collect to collect from flow
            // returned by @Query annotated method
            employeeDao.fetchOneEmployee(id).collect {
                //setting data given by query method  fetchOneEmployee
                binding.editTextTextPersonName.setText(it.name)
                binding.editTextTextPersonName2.setText(it.email)
            }
        }

        //setting event handler for save button of edit popup
        binding.save.setOnClickListener {
            //view binding stuff
            val name = binding.editTextTextPersonName.text.toString()
            val email = binding.editTextTextPersonName2.text.toString()

            //checking if data entered by use is not incomplete i.e. any field is not empty
            //we launch the coroutine for updating employee data
            if (name.isNotEmpty() && email.isNotEmpty()) {
                //launching the coroutine for updating employee
                lifecycleScope.launch {
                    //calling the update method of employeeDao for updating record
                    employeeDao.updateEmployee(EmployeeEntity(id, name, email))
                    //showing a useless toast
                    Toast.makeText(applicationContext, "record updated", Toast.LENGTH_LONG).show()
                }
            } else {
                //showing a useless toast if user data is incomplete
                Toast.makeText(applicationContext, "please enter name or email", Toast.LENGTH_LONG)
                    .show()
            }
        }

        //setting event listener for cancel button
        binding.cancel.setOnClickListener {
            //on click dismissing the dialog
            updateDialog.dismiss()
        }

        //showing the created dialog
        updateDialog.show()
    }

    //function for managing deletion of employee
    private fun deleteRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        //creating positive button in alert dialog
        builder.setPositiveButton("yes") { dialogInterface, _ ->
            //launching coroutine for deleting employee from database
            lifecycleScope.launch {
                //calling delete function for deleting employee -- important
                employeeDao.deleteEmployee(EmployeeEntity(id))
                //useless toast
                Toast.makeText(applicationContext, "record deleted successfully", Toast.LENGTH_LONG)
                    .show()
            }
            //closing the dialog opened for delete confirmation
            dialogInterface.dismiss()
        }

        //creating negative button in alert dialog
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        //creating the dialog
        val alert: AlertDialog = builder.create()
        //setting that dialog can't be dismissed by clicking outside
        alert.setCancelable(false)

        //showing the delete confirm dialog
        alert.show()
    }

    //function to manage addition of employee
    private fun addRecord(employeeDao: EmployeeDao) {
        //here position is actually email :-) silly me
        val position = binding.position.text.toString()
        val name = binding.name.text.toString()

        //checking if name is not empty and email is not empty
        //then we launch coroutine for adding employee
        if (name.isNotEmpty() && position.isNotEmpty()) {
            //launching a coroutine to insert the employee
            lifecycleScope.launch {
                //calling employeeDao (Dao)'s insert function to add employee
                employeeDao.insertEmployee(EmployeeEntity(name = name, email = position))

                //clearing input boxes
                binding.name.text.clear()
                binding.position.text.clear()
            }
        } else {
            //if any input box is empty we show is toast and showing it
            Snackbar.make(this, binding.container, "something is empty", Snackbar.LENGTH_LONG)
                .show()
        }
    }

}