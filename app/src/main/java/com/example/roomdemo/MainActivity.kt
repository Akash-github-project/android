package com.example.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.roomdemo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val employeeDao = (application as EmployeeApp).db.employeeDao()

        binding.addEmployee.setOnClickListener{
            //TODO add addEmployee wiht employeeDao
            addRecord(employeeDao)
        }

    }

    fun addRecord(employeeDao: EmployeeDao){
        val position = binding.position.text.toString()
        val name = binding.name.text.toString()

        if(name.isNotEmpty() && position.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insertEmployee(EmployeeEntity(name=name, email = position))

                binding.name.text.clear()
                binding.position.text.clear()
            }
        }
        else{
            Snackbar.make(this,binding.container,"something is empty",Snackbar.LENGTH_LONG).show()
        }
    }
}