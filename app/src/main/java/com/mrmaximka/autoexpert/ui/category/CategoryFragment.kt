package com.mrmaximka.autoexpert.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mrmaximka.autoexpert.MainActivity
import com.mrmaximka.autoexpert.R
import com.mrmaximka.autoexpert.utils.NavigationUtils
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment(), CategoryAdapter.OnClick {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter
    private var bundle: Bundle? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        viewModel.needOpen.observe(viewLifecycleOwner, Observer {
            it?.let {
                NavigationUtils.observerNavigation(
                    requireActivity(),
                    it,
                    viewModel.needOpen,
                    R.id.action_categoryFragment_to_listFragment,
                    bundle
                )
            }
        })

        viewModel.loadCategories(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createList()
    }

    private fun createList() {
        adapter = CategoryAdapter(this)
        category_list.adapter = adapter
    }

    override fun onViewClick(categoryId: Int) {
        bundle = Bundle()
        bundle!!.putInt("categoryId", categoryId)
        viewModel.onCategoryClick()
        Toast.makeText(context, "$categoryId", Toast.LENGTH_LONG).show()
    }
}
