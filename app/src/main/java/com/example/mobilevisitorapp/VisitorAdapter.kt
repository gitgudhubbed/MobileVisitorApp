package com.example.mobilevisitorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.visitor_item.view.*

class VisitorAdapter(var visitorListItems : List<FirebaseRepo.Visitor>, val clickListener: (FirebaseRepo.Visitor) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class VisitorViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var linearLayout : LinearLayout = itemView.findViewById(R.id.visitor_list_linear_layout)
        var expandableLayout : RelativeLayout = itemView.findViewById(R.id.visitor_list_expandable)

        fun bind (visitor : FirebaseRepo.Visitor, clickListener : (FirebaseRepo.Visitor) -> Unit){
            itemView.visitor_list_name.text = visitor.name

            itemView.setOnClickListener {
                clickListener(visitor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visitor_item, parent, false)
        return VisitorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return visitorListItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VisitorViewHolder).bind(visitorListItems[position], clickListener)

        //Expandable view bind
        val isExpandable : Boolean = visitorListItems[position].expandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.linearLayout.setOnClickListener {
            val event = visitorListItems[position]
            event.expandable = !event.expandable
            notifyItemChanged(position)
        }
    }
}