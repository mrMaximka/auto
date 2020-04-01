package com.mrmaximka.autoexpert.ui.list

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.utils.NavigationUtils
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(), ListAdapter.OnClick {

    private lateinit var viewModel: ListViewModel
    private lateinit var adapter: ListAdapter
    private var width: Int =  0
    private var bundle: Bundle? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        val categoryId: Int = arguments?.get("categoryId") as Int
        Toast.makeText(context, "Cat id: $categoryId", Toast.LENGTH_LONG).show()

        viewModel.needChooseQuest.observe(viewLifecycleOwner, Observer {
            it?.let {
                NavigationUtils.observerNavigation(
                    requireActivity(),
                    it,
                    viewModel.needChooseQuest,
                    R.id.action_listFragment_to_questFragment,
                    bundle
                )
            }
        })

        viewModel.loadElements(adapter, categoryId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calcWidth()
        initList()
    }

    private fun calcWidth() {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
    }

    private fun initList() {
        adapter = ListAdapter(this, width, context!!)
        items_list.layoutManager = GridLayoutManager(context, 4)
        items_list.adapter = adapter
    }

    override fun onElementClick(position: Int) {
        bundle = Bundle()
        bundle!!.putInt("position", position)
        viewModel.onClickQuest()
//        Toast.makeText(context, "$elementId", Toast.LENGTH_LONG).show()
    }
}
