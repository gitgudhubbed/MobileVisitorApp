package com.example.mobilevisitorapp

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.visitor_item.view.*

class VisitorAdapter(var visitorListItems : List<FirebaseRepo.Visitor>, val clickListener: (FirebaseRepo.Visitor) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()



    class VisitorViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var linearLayout : LinearLayout = itemView.findViewById(R.id.visitor_list_linear_layout)
        var expandableLayout : RelativeLayout = itemView.findViewById(R.id.visitor_list_expandable)
        var signOutBtn : Button = itemView.findViewById(R.id.visitor_list_button)

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
        val context = holder.itemView.context

        //Expandable view bind
        val isExpandable : Boolean = visitorListItems[position].expandable
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.linearLayout.setOnClickListener {
            val event = visitorListItems[position]
            event.expandable = !event.expandable
            notifyItemChanged(position)
        }
        //Sign-out function call
        holder.signOutBtn.setOnClickListener {
            Log.d("tag", "button pressed")
            //Confirmation check
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Sign-out")
            builder.setMessage("Are you sure you wish to sign-out")

            builder.setPositiveButton("Yes"){dialog, which ->
                //Sign out function completed in FirebaseRepo
                val signOutVis = visitorListItems[position]
                signOutVis.outTime = Timestamp.now()
                Log.d("tag", "After function -- --- ${signOutVis}")
                firebaseRepo.visitorSignOut(signOutVis)
                Toast.makeText(context,"Sign-out Confirmed", Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No"){dialog, which ->
                Toast.makeText(context,"Sign-out Cancelled", Toast.LENGTH_LONG).show()
           }
           val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}
