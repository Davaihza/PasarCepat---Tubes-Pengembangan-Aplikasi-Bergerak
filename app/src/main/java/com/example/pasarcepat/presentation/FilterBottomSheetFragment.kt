package com.example.pasarcepat.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.pasarcepat.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import java.text.NumberFormat
import java.util.Locale

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var sliderPrice: RangeSlider
    private lateinit var tvMinPrice: TextView
    private lateinit var tvMaxPrice: TextView
    private lateinit var btnApply: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnClose: View
    
    private lateinit var cbAll: CheckBox
    private lateinit var cbHandphone: CheckBox
    private lateinit var cbComputer: CheckBox
    private lateinit var cbLaptop: CheckBox

    private var onApplyListener: ((Float, Float, List<String>) -> Unit)? = null

    fun setOnApplyListener(listener: (Float, Float, List<String>) -> Unit) {
        onApplyListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_filter_sorting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
    }

    private fun initViews(view: View) {
        sliderPrice = view.findViewById(R.id.sliderPrice)
        tvMinPrice = view.findViewById(R.id.tvMinPrice)
        tvMaxPrice = view.findViewById(R.id.tvMaxPrice)
        btnApply = view.findViewById(R.id.btnApply)
        btnReset = view.findViewById(R.id.btnReset)
        btnClose = view.findViewById(R.id.btnClose)
        
        cbAll = view.findViewById(R.id.cbAllSubCategories)
        cbHandphone = view.findViewById(R.id.cbHandphone)
        cbComputer = view.findViewById(R.id.cbComputer)
        cbLaptop = view.findViewById(R.id.cbLaptop)
        
        // Initial text update
        updatePriceText(sliderPrice.values[0], sliderPrice.values[1])
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { dismiss() }
        
        sliderPrice.addOnChangeListener { slider, value, fromUser ->
            val values = slider.values
            updatePriceText(values[0], values[1])
        }
        
        btnReset.setOnClickListener {
            sliderPrice.setValues(50000f, 100000f)
            cbAll.isChecked = false
            cbHandphone.isChecked = true
            cbComputer.isChecked = false
            cbLaptop.isChecked = false
        }

        btnApply.setOnClickListener {
            val values = sliderPrice.values
            val minPrice = values[0]
            val maxPrice = values[1]
            
            val selectedCategories = mutableListOf<String>()
            if (cbAll.isChecked) selectedCategories.add("All")
            if (cbHandphone.isChecked) selectedCategories.add("Handphone")
            if (cbComputer.isChecked) selectedCategories.add("Computer")
            if (cbLaptop.isChecked) selectedCategories.add("Laptop")
            
            if (selectedCategories.isEmpty() && !cbAll.isChecked) {
                // If nothing selected but not all, maybe default to All or just send empty?
                // For now, let's treat empty as All or strictly nothing.
                selectedCategories.add("All") 
            }

            onApplyListener?.invoke(minPrice, maxPrice, selectedCategories)
            dismiss()
        }
        
        // Checkbox logic (optional: if All checked, uncheck others etc)
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbHandphone.isChecked = false
                cbComputer.isChecked = false
                cbLaptop.isChecked = false
            }
        }
        
        val subCategoryListener = { _: View, isChecked: Boolean ->
            if (isChecked) cbAll.isChecked = false
        }
        
        cbHandphone.setOnCheckedChangeListener(subCategoryListener)
        cbComputer.setOnCheckedChangeListener(subCategoryListener)
        cbLaptop.setOnCheckedChangeListener(subCategoryListener)
    }

    private fun updatePriceText(min: Float, max: Float) {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvMinPrice.text = format.format(min)
        tvMaxPrice.text = format.format(max)
    }

    companion object {
        const val TAG = "FilterBottomSheetFragment"
    }
}
