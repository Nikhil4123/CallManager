package com.example.callog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ContactsAdapter(
    private val context: Context,
    private val contacts: ArrayList<Contact>
) : BaseAdapter() {

    override fun getCount(): Int = contacts.size

    override fun getItem(position: Int): Any = contacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Using the existing row_layout instead of contact_row_layout
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_layout, parent, false)

        val contact = contacts[position]

        // Find the TextView using your existing list_item ID
        val contactTextView = view.findViewById<TextView>(R.id.list_item)

        // Set the text with contact information
        contactTextView.text = """
            Name: ${contact.name}
            Phone: ${contact.phoneNumber}
        """.trimIndent()

        return view
    }
}