package ru.proofeek.resdel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.proofeek.resdel.model.NewsItem

class BannerAdapter(var context: Context, val listener: Listener): RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    var dataList = emptyList<DataModel>()

    @SuppressLint("NotifyDataSetChanged")
    internal fun setDataList(dataList: List<DataModel>){
        this.dataList = dataList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView = itemView.findViewById(R.id.BannerGridImage)
        fun bind(item: DataModel, listener: BannerAdapter.Listener){
            itemView.setSafeOnClickListener {
                listener.OnClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data, listener)

        holder.image.setImageResource(data.image)
    }

    override fun getItemCount() = dataList.size

    interface Listener{
        fun OnClick(item: DataModel)
    }
}