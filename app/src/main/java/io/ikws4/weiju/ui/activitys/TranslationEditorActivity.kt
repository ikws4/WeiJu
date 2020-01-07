package io.ikws4.weiju.ui.activitys

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import io.ikws4.weiju.R
import io.ikws4.weiju.utilities.TranslationInfoDiffCallBack
import io.ikws4.weiju.data.TranslationInfo
import io.ikws4.weiju.databinding.ActivityTranslationEditorBinding
import io.ikws4.weiju.databinding.ItemTranslationInfoBinding
import io.ikws4.weiju.ui.viewmodels.TranslationInfoViewModel
import io.ikws4.weiju.utilities.InjectorUtils
import java.util.*

class TranslationEditorActivity : BasicActivity() {
    private val args: TranslationEditorActivityArgs by navArgs()
    private val viewModel: TranslationInfoViewModel by viewModels {
        InjectorUtils.provideTranslationInfoViewModelFactory(this, args.prefName)
    }
    private val adapter = TranslationEditorAdapter()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityTranslationEditorBinding>(this, R.layout.activity_translation_editor)
        val toolbar = binding.actionBar.toolbar
        setSupportActionBar(toolbar)
        subscribeUi()
        setupRecyclerView(binding)
    }

    private fun setupRecyclerView(binding: ActivityTranslationEditorBinding) {
        with(binding.recyclerView) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = this@TranslationEditorActivity.adapter
        }
    }

    private fun subscribeUi() {
        viewModel.translationInfos.observe(this, Observer {
            adapter.setNewData(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_translation_editor, menu)
        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        return true
    }

    inner class TranslationEditorAdapter :
        ListAdapter<TranslationInfo, TranslationEditorAdapter.ViewHolder>(TranslationInfoDiffCallBack()), Filterable {
        private var filterStr = ""
        private var sourceList = listOf<TranslationInfo>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTranslationInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val tempData = arrayListOf<TranslationInfo>()
                    val filterResults = FilterResults()
                    filterStr = constraint.toString().toLowerCase(Locale.getDefault())

                    if (constraint.isNotEmpty()) {
                        for (translationInfo in sourceList) {
                            if (translationInfo.query.toLowerCase(Locale.getDefault()).contains(filterStr)
                                || translationInfo.result.toLowerCase(Locale.getDefault()).contains(filterStr)
                            ) {
                                tempData.add(translationInfo)
                            }
                        }
                        filterResults.values = tempData
                    } else {
                        filterResults.values = sourceList
                    }

                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    val data = results!!.values as ArrayList<TranslationInfo>
                    submitList(data)
                }

            }
        }

        fun setNewData(data: List<TranslationInfo>) {
            sourceList = data
            filter.filter(filterStr)
        }

        inner class ViewHolder(private val binding: ItemTranslationInfoBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(info: TranslationInfo) {
                with(binding) {
                    query.text = info.query
                    result.text = info.result
                    result.addTextChangedListener {  }
                    root.setOnClickListener {
                        MaterialDialog(it.context).show {
                            title(text = info.query)
                            input(
                                hint = info.result,
                                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            ) { _, charSequence ->
                                viewModel.update(info.copy(result = charSequence.toString()))
                            }
                            positiveButton(android.R.string.ok)
                            negativeButton(android.R.string.cancel)
                        }
                    }
                }
            }
        }
    }
}