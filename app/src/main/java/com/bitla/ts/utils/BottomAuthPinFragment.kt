package com.bitla.ts.utils

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bitla.ts.R
import com.bitla.ts.databinding.FragmentBottomAuthPinBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomAuthPinFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomAuthPinBinding? = null
    private val binding get() = _binding!!

    private lateinit var pinKeyboardAdapter: PinKeyboardAdapter
    private var pinInputListener: PinInputListener? = null
    private var pinSize: Int = 6
    private var currentPin: StringBuilder = StringBuilder()
    private var actionName: String = ""
    private var onDismissCallback: (() -> Unit)? = null

    companion object {
        fun newInstance(pinSize: Int = 6, actionName: String): BottomAuthPinFragment {
            return BottomAuthPinFragment().apply {
                arguments = Bundle().apply {
                    putInt("PIN_SIZE", pinSize)
                    putString("ACTION_NAME", actionName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinSize = arguments?.getInt("PIN_SIZE", 6) ?: 6
        actionName = arguments?.getString("ACTION_NAME", "") ?: ""
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dismiss()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomAuthPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupPinIndicators()
        setupKeyboard()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvActionName.text = actionName
        binding.titleTextView.text = getString(R.string.enter_pin_to_unlock)
        binding.tvTitleNext.text = getString(R.string.enter_pin_to_unlock_next)
    }

    private fun setupPinIndicators() {
        val pinIndicators = listOf(
            binding.tvPin1, binding.tvPin2, binding.tvPin3,
            binding.tvPin4, binding.tvPin5, binding.tvPin6
        )
        pinIndicators.forEachIndexed { index, indicator ->
            indicator.visibility = if (index < pinSize) View.VISIBLE else View.GONE
        }
    }

    private fun setupKeyboard() {
        pinKeyboardAdapter = PinKeyboardAdapter { digit ->
            onDigitPressed(digit)
        }
        binding.rvPinKeyboard.layoutManager = GridLayoutManager(context, 3)
        binding.rvPinKeyboard.adapter = pinKeyboardAdapter
    }

    private fun setupListeners() {
        setupTouchEffect(binding.tvZeroNumber) { onDigitPressed(0) }
        setupTouchEffect(binding.imageViewBackspace) { onBackspacePressed() }
        setupTouchEffect(binding.imageViewDone) { onDonePressed() }
        binding.tvAuthPinCancelButton.setOnClickListener { dismiss() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    fun setOnDismissCallback(callback: () -> Unit) {
        onDismissCallback = callback
    }

    private fun onDigitPressed(digit: Int) {
        if (currentPin.length < pinSize) {
            currentPin.append(digit)
            updatePinIndicators()
        }
    }

    private fun onBackspacePressed() {
        if (currentPin.isNotEmpty()) {
            currentPin.deleteCharAt(currentPin.length - 1)
            updatePinIndicators()
        }
    }

    private fun onDonePressed() {
        if (currentPin.length == pinSize) {
            pinInputListener?.onPinSubmitted(currentPin.toString())
            dismiss()
        }
    }

    private fun updatePinIndicators() {
        val pinIndicators = listOf(
            binding.tvPin1, binding.tvPin2, binding.tvPin3,
            binding.tvPin4, binding.tvPin5, binding.tvPin6
        )
        pinIndicators.forEachIndexed { index, indicator ->
            if (index < pinSize) {
                indicator.setBackgroundResource(
                    if (index < currentPin.length) R.drawable.ic_circle_filled_outline
                    else R.drawable.ic_circle_outline
                )
            }
        }
    }

    fun setPinInputListener(listener: PinInputListener) {
        this.pinInputListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchEffect(view: View, onClick: () -> Unit) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_button_border)
                    onClick()
                }
            }
            true
        }
    }

    interface PinInputListener {
        fun onPinSubmitted(pin: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onDismissCallback?.invoke()
    }
}