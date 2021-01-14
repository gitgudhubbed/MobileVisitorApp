package com.example.mobilevisitorapp


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.roll_call_item.view.*
import kotlinx.android.synthetic.main.visitor_item.view.*

class RollCallAdapter (var visitorListItems : List<FirebaseRepo.Visitor>, val clickListener: (FirebaseRepo.Visitor) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    class RollCallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var linearLayout : LinearLayout = itemView.findViewById(R.id.roll_call_linear_layout)

        fun bind (visitor : FirebaseRepo.Visitor, clickListener : (FirebaseRepo.Visitor) -> Unit){
            itemView.roll_visitor_name.text = visitor.name

            linearLayout.setOnClickListener {
                linearLayout.setBackgroundColor(Color.GREEN)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.roll_call_item, parent, false)
        return RollCallViewHolder(view)
    }

    override fun getItemCount(): Int {
        return visitorListItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RollCallViewHolder).bind(visitorListItems[position], clickListener)
        //val context = holder.itemView.context

        //holder.linearLayout.setOnClickListener {
          // holder.linearLayout.setBackgroundColor(0x000000)
           // holder.linearLayout.background (@color/colorBlack)

       // }

    }


}