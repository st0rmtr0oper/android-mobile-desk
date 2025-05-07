package com.example.problemdesk.presentation.logs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.problemdesk.databinding.FragmentSubLogItemBinding
import com.example.problemdesk.domain.models.RequestLog
import com.example.problemdesk.presentation.getStatus

class LogRecyclerViewAdapter(private val logListener: (RequestLog) -> Unit) :
	RecyclerView.Adapter<CardsViewHolder>() {

	var logs: List<RequestLog> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
//            notifyItemChanged(position)    --better performance, but idk how to use it
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = FragmentSubLogItemBinding.inflate(inflater, parent, false)
		return CardsViewHolder(binding)
	}

	override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
		holder.bind(logs[position], logListener)
	}

	override fun getItemCount(): Int = logs.size
}

class CardsViewHolder(private val binding: FragmentSubLogItemBinding) :
	RecyclerView.ViewHolder(binding.root) {
	fun bind(log: RequestLog, cancelledCardListener: (RequestLog) -> Unit) {
		with(binding) {
			name.text = getStatus(log.logId)     //TODO
			time.text = getStatus(log.logId)    //TODO reformatting data for logs
			status.text = getStatus(log.logId) //TODO
			if (log.logId.toString() == "") { //TODO too
				reasonLabel.visibility = View.GONE
				reason.visibility = View.GONE
			} else {
				reason.text = getStatus(log.logId)
			}
		}

		itemView.setOnClickListener {
			cancelledCardListener(log)
		}
	}
}