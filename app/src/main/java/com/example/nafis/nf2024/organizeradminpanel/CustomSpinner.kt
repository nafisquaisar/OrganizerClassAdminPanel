package com.example.customspinners

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

class CustomSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.spinnerStyle,
    mode: Int = MODE_DIALOG,
    popupTheme: Resources.Theme? = null
) : AppCompatSpinner(context, attrs, defStyleAttr, mode, popupTheme) {

    interface OnSpinnerEventsListener {
        fun onPopupWindowOpened(spinner: Spinner)
        fun onPopupWindowClosed(spinner: Spinner)
    }

    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    override fun performClick(): Boolean {
        val spinnerHeight = this.height
        val spinnerWidth = this.width
        val measuredHeight = this.measuredHeight
        android.util.Log.d("CustomSpinner", "Spinner Height: $spinnerHeight, Measured Height: $measuredHeight, Width: $spinnerWidth")

        mOpenInitiated = true
        mListener?.onPopupWindowOpened(this)
        this.dropDownVerticalOffset = spinnerHeight
        return super.performClick()
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    fun setSpinnerEventsListener(listener: OnSpinnerEventsListener?) {
        mListener = listener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    fun performClosedEvent() {
        mOpenInitiated = false
        mListener?.onPopupWindowClosed(this)
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    fun hasBeenOpened(): Boolean = mOpenInitiated
}
