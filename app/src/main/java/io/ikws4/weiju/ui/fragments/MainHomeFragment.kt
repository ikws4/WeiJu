package io.ikws4.weiju.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.ikws4.weiju.R
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.databinding.FragmentMainHomeBinding
import io.ikws4.weiju.databinding.ItemSelectedappBinding
import io.ikws4.weiju.ui.viewmodels.AppInfoViewModel
import io.ikws4.weiju.utilities.AppInfoDiffCallBack
import io.ikws4.weiju.utilities.DividerItemDecoration
import io.ikws4.weiju.utilities.InjectorUtils
import io.ikws4.weiju.utilities.SPManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


@ExperimentalCoroutinesApi
class MainHomeFragment : Fragment(), CoroutineScope by MainScope() {
    private val viewModel by viewModels<AppInfoViewModel> { InjectorUtils.provideAppInfoViewModelFactory(requireActivity()) }
    private val spManager by lazy { SPManager.getInstance(context!!) }
    private val adapter = Adapter()
    private lateinit var binding: FragmentMainHomeBinding
    private val adapterLeftItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        private lateinit var currentDeletedItem: AppInfo

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = adapter.currentList[position]
            currentDeletedItem = item
            viewModel.update(item.copy(isSelect = false))
            spManager.HookListSP().remove(item.pkgName)
            Snackbar.make(view!!, R.string.item_deleted_remind, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.update(item)
                    spManager.HookListSP().add(item.pkgName)
                }.show()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        setupFab()
        setupRecyclerView()
        refreshModuleState(if (isExpModuleActive()) "ACTIVE" else "WARNING")
        subscribeUi()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    /**
     * 与viewModel关联,更新数据
     */
    private fun subscribeUi() {
        viewModel.appInfos.observe(viewLifecycleOwner, Observer { appInfos ->
            appInfos.filter { appInfo ->
                appInfo.isSelect

            }.apply {
                adapter.submitList(this)
                binding.isHasApps = !isNullOrEmpty()
            }
        })
    }

    private fun isExpModuleActive(): Boolean {

        var isExp = false
        if (context == null) {
            throw IllegalArgumentException("context must not be null!!")
        }

        try {
            val contentResolver = context!!.contentResolver
            val uri = Uri.parse("content://me.weishu.exposed.CP/")
            var result: Bundle? = null
            try {
                result = contentResolver.call(uri, "active", null, null)
            } catch (e: RuntimeException) {
                // TaiChi is killed, try invoke
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context!!.startActivity(intent)
                } catch (e1: Throwable) {
                    return false
                }

            }

            if (result == null) {
                result = contentResolver.call(uri, "active", null, null)
            }

            if (result == null) {
                return false
            }
            isExp = result.getBoolean("active", false)
        } catch (ignored: Throwable) {
        }

        return isExp
    }

    /**
     * 刷新模块状态
     * @param state 状态,通过Hook来改变状态 {ACTIVE WARNING UPDATE}
     */
    @Keep
    private fun refreshModuleState(state: String = "WARNING") {
        with(binding) {
            moduleStateIcon = when (state) {
                "ACTIVE" -> R.drawable.ic_check_circle
                "WARNING" -> R.drawable.ic_warning
                "UPDATE" -> R.drawable.ic_get_app
                else -> R.drawable.ic_warning
            }

            moduleStateHint = when (state) {
                "ACTIVE" -> R.string.module_active_hint
                "WARNING" -> R.string.module_waring_hint
                "UPDATE" -> R.string.module_new_version_hint
                else -> R.string.module_waring_hint
            }

            layoutModuleState.moduleStateClicker.setOnClickListener {
                when (state) {
                    "ACTIVE" -> {
                    }
                    "WARNING" -> {
                    }
                    "UPDATE" -> {
                        val downloadUrl = Uri.parse(spManager.WeiJuSP().newVersionDownloadUrl)
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data = downloadUrl
                        startActivity(intent)
                    }
                }
            }
        }
    }

    /**
     * 初始化RecyclerView
     */
    private fun setupRecyclerView() {
        binding.layoutHookingApps.recyclerView.apply {
            adapter = this@MainHomeFragment.adapter
            ItemTouchHelper(adapterLeftItemTouchCallback).attachToRecyclerView(this)
            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }

    /**
     * 监听fab,用来启动 选择应用 界面
     */
    private fun setupFab() {
        with(binding) {
            fab.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    MainHomeFragmentDirections.toSelectAppActivity()
                )
            )
            // 通过监听滚动,实现fab的显隐动画
            nestedScroll.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                if (oldScrollY < scrollY && oldScrollY != 0) {
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
            }
        }
    }

    private inner class Adapter : ListAdapter<AppInfo, Adapter.ViewHolder>(AppInfoDiffCallBack()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemSelectedappBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position).let { appInfo ->
                holder.bind(appInfo)
            }
        }

        inner class ViewHolder(private val binding: ItemSelectedappBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(appInfo: AppInfo) {
                with(binding) {
                    icon.setImageDrawable(Drawable.createFromPath(appInfo.iconPath))
                    name.text = appInfo.name
                    // 启动CategoryActivity
                    // 携带pkgName和name
                    binding.root.setOnClickListener {
                        it.findNavController().navigate(MainHomeFragmentDirections.toCategoryActivity(appInfo.name, appInfo.pkgName))
                    }

                }
            }
        }
    }
}