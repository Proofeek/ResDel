package ru.proofeek.resdel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(var context: Context): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var dataList = emptyList<DataModelNews>()

    @SuppressLint("NotifyDataSetChanged")
    internal fun setDataList(dataList: List<DataModelNews>){
        this.dataList = dataList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView
        var text : TextView
        var date: TextView

        init {
            image = itemView.findViewById(R.id.newsImage)
            text = itemView.findViewById(R.id.newsText)
            date = itemView.findViewById(R.id.newsDateText)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        holder.text.text = data.text
        holder.date.text = data.date
        holder.image.setImageResource(data.image)
    }

    override fun getItemCount() = dataList.size
}