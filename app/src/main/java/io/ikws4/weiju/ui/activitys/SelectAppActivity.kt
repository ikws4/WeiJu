package io.ikws4.weiju.ui.activitys

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.ikws4.weiju.R
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.databinding.ActivitySelectappBinding
import io.ikws4.weiju.databinding.ItemSelectappBinding
import io.ikws4.weiju.ui.viewmodels.AppInfoViewModel
import io.ikws4.weiju.utilities.AppInfoDiffCallBack
import io.ikws4.weiju.utilities.InjectorUtils
import io.ikws4.weiju.utilities.SPManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import replaceAll
import java.util.*

/**
 * 选择需要Hook的应用
 * @property viewModel AppInfoViewModel
 * @property adapter Adapter
 */
@ExperimentalCoroutinesApi
class SelectAppActivity : BasicActivity(), CoroutineScope by MainScope() {
    private val viewModel by viewModels<AppInfoViewModel> {
        InjectorUtils.provideAppInfoViewModelFactory(this)
    }
    private val spManager by lazy {
        SPManager.getInstance(this)
    }
    private val adapter = Adapter()
    private lateinit var binding: ActivitySelectappBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_selectapp)
        val toolbar = binding.actionBar.toolbar
        setSupportActionBar(toolbar)
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_selectapp, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        setupSearchView(searchView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                intent.putExtra("pageName", SettingActivity.GENERAL)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 当有新数据时时，更新adapter的数据
     * 在Toolbar的子标题显示当前app的数量
     */
    private fun subscribeUi() {
        val weiJuSP = spManager.WeiJuSP()

        viewModel.appInfos.observe(this, Observer { appInfos ->
            appInfos.filter { appInfo ->
                !appInfo.isSelect
            }.filter { appInfo ->
                appInfo.isSystemApp and weiJuSP.isHideSystemApp
            }.apply {
                adapter.setNewData(this)
                binding.isLoaded = isNotEmpty()
                binding.actionBar.toolbar.subtitle = "$size apps"
            }
        })
    }

    /**
     * 初始化RecyclerView
     * 设置adapter的点击监听器，点击时更新数据
     */
    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        with(binding.recyclerView) {
            addItemDecoration(dividerItemDecoration)
            adapter = this@SelectAppActivity.adapter
        }
    }

    /**
     * 搜索数据
     * 可搜索数据是App的名称
     * @param searchView SearchView
     */
    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private inner class Adapter : ListAdapter<AppInfo, Adapter.ViewHolder>(AppInfoDiffCallBack()),
        Filterable {
        private var filterStr = ""
        private var sourceList = listOf<AppInfo>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemSelectappBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val tempData = arrayListOf<AppInfo>()
                val filterResults = FilterResults()
                filterStr = constraint.toString()

                if (constraint.isNotEmpty()) {
                    for (appInfo in sourceList) {
                        if (appInfo.name.toLowerCase(Locale.getDefault()).contains(filterStr.toLowerCase(Locale.getDefault()))) {
                            tempData.add(appInfo)
                        }
                    }
                    filterResults.values = tempData
                } else {
                    filterResults.values = sourceList
                }

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(content: CharSequence?, filterResults: FilterResults?) {
                val data = filterResults!!.values as ArrayList<AppInfo>
                submitList(data)
            }
        }

        /**
         * 设置新数据
         * @param list List<AppInfo>
         */
        fun setNewData(list: List<AppInfo>) {
            sourceList = list
            filter.filter(filterStr)
        }

        inner class ViewHolder(private val binding: ItemSelectappBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(appInfo: AppInfo) {
                with(binding) {
                    icon.setImageDrawable(Drawable.createFromPath(appInfo.iconPath))
                    name.text = appInfo.name
                    addButton.setOnClickListener {
                        // 标记为被选择（添加）
                        viewModel.update(appInfo.copy(isSelect = true))
                        // 把app的包名添加到hook_list.xml
                        spManager.HookListSP().add(appInfo.pkgName)
                        // 应用模版
                        if (spManager.WeiJuSP().isAutoApplyTemplate) {
                            val configSP = this@SelectAppActivity.getSharedPreferences(appInfo.pkgName, Context.MODE_PRIVATE)
                            configSP.replaceAll(spManager.templateSP.all)
                        }
                    }
                }
            }

        }
    }

}
