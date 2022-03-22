package ru.proofeek.resdel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoodMenu : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var foodMenuAdapter: FoodMenuAdapter
    private var dataList = mutableListOf<DataModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = requireView().findViewById(R.id.recyclerFoodMenu)


        recyclerView.layoutManager = GridLayoutManager(activity?.applicationContext, 2)
        foodMenuAdapter = activity?.applicationContext?.let { FoodMenuAdapter(it) }!!
        recyclerView.adapter = foodMenuAdapter

        dataList.add(DataModel("Title", R.drawable.i7508))
        dataList.add(DataModel("Title", R.drawable.i7577))
        dataList.add(DataModel("Title", R.drawable.i7589))
        dataList.add(DataModel("Title", R.drawable.i7615))
        dataList.add(DataModel("Title", R.drawable.i7643))
        dataList.add(DataModel("Title", R.drawable.i7666))
        dataList.add(DataModel("Title", R.drawable.i7589))
        dataList.add(DataModel("Title", R.drawable.i7615))
        dataList.add(DataModel("Title", R.drawable.i7508))



        foodMenuAdapter.setDataList(dataList)
        Log.e("BBBBBBBBBBBBBBBBBBBBBBBBBB", "Fefef")
    }

    companion object{

        @JvmStatic
        fun newInstance() = FoodMenu()
    }
}