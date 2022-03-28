package ru.proofeek.resdel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodMenuAdapter(var context: Context, val listener: Listener): RecyclerView.Adapter<FoodMenuAdapter.ViewHolder>() {

    var dataList = emptyList<DataModel>()

    @SuppressLint("NotifyDataSetChanged")
    internal fun setDataList(dataList: List<DataModel>){
        this.dataList = dataList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView
        var title : TextView

        init {
            image = itemView.findViewById(R.id.foodMenuGridImage)
            title = itemView.findViewById(R.id.foodMenuText)
        }
        fun bind(item: DataModel, listener: Listener){
                listener.getWidth(itemView.width)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_menu_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]


        holder.title.text = data.title
        holder.image.setImageResource(data.image)

        holder.itemView.post{
            //Log.e("WIDTH: ", holder.itemView.width.toString())
            holder.bind(data, listener)
        }
    }



    override fun getItemCount() = dataList.size

    interface Listener{
        fun getWidth(width: Int): Int{
            return width
        }
    }
}