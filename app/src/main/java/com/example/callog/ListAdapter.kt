package com.example.calllog  // Changed to match MainActivity package

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.callog.R

class ListAdapter(
    private val context: Context,
    private val list: ArrayList<com.example.callog.CallDetails>
) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_layout, parent, false)

        // Changed to a more specific ID for the row TextView
        val nameTextView = view.findViewById<TextView>(R.id.row_text_view)

        val callDetail = list[position]
        nameTextView.text = """
            Caller Name: ${callDetail.callerName}
            Phone Number: ${callDetail.phoneNumber}
            Call Duration: ${callDetail.callDuration} sec
            Call Type: ${callDetail.callType}
            Call Time: ${callDetail.callDate}
        """.trimIndent()

        return view
    }
}