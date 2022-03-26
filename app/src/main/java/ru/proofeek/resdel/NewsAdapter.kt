package ru.proofeek.resdel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.proofeek.resdel.model.NewsItem
import java.text.SimpleDateFormat
import java.util.*


class NewsAdapter(var context: Context, val items: ArrayList<NewsItem>, val listener: Listener): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView
        var text : TextView
        var date: TextView

        init {
            image = itemView.findViewById(R.id.newsImage)
            text = itemView.findViewById(R.id.newsText)
            date = itemView.findViewById(R.id.newsDateText)
        }
        fun bind(item: NewsItem, listener: Listener){
            itemView.setSafeOnClickListener{
                listener.OnClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, listener)
        holder.date.text = convertDate(item.active_from)
        holder.text.text = item.name

        if(item.logo.isNotEmpty()) {
            Picasso.get()
                .load(item.logo)
                .into(holder.image)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDate(date: Long): String{
        val timeD = Date(date * 1000)
        val monthFormat = SimpleDateFormat("MM")
        val dayFormat = SimpleDateFormat("d")
        val month: String = monthToText(monthFormat.format(timeD))
        val day: String = dayFormat.format(timeD)
        return "$day $month"
    }

    private fun monthToText(month: String): String{
        when(month){
            "01" -> return context.resources.getString(R.string.january)
            "02" -> return context.resources.getString(R.string.february)
            "03" -> return context.resources.getString(R.string.march)
            "04" -> return context.resources.getString(R.string.april)
            "05" -> return context.resources.getString(R.string.may)
            "06" -> return context.resources.getString(R.string.june)
            "07" -> return context.resources.getString(R.string.july)
            "08" -> return context.resources.getString(R.string.august)
            "09" -> return context.resources.getString(R.string.september)
            "10" -> return context.resources.getString(R.string.october)
            "11" -> return context.resources.getString(R.string.november)
            "12" -> return context.resources.getString(R.string.december)
        }
        return context.resources.getString(R.string.january)
    }

    override fun getItemCount() = items.size

    interface Listener{
        fun OnClick(item: NewsItem)
    }
}