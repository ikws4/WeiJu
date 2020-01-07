package io.ikws4.weiju.ui.activitys

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.android.material.appbar.MaterialToolbar
import io.ikws4.weiju.R
import io.ikws4.weiju.databinding.ActivityCategoryBinding

class CategoryActivity : BasicActivity() {
    private val args by navArgs<CategoryActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCategoryBinding>(this, R.layout.activity_category)
        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar = binding.actionBar.toolbar
        setupToolbarWithNav(navController, toolbar)
    }

    /**
     * 设置Toolbar标题为appName
     * 把destination的label属性作为toolbar的子标题
     * @param navController NavController
     * @param toolbar MaterialToolbar
     */
    private fun setupToolbarWithNav(navController: NavController, toolbar: MaterialToolbar) {
        toolbar.title = args.title
        setSupportActionBar(toolbar)
        val bundle = Bundle().apply {
            putString("pkgName", args.pkgName)
        }
        with(navController) {
            // 动态设置导航图属性
            setGraph(R.navigation.nav_graph_category, bundle)
            addOnDestinationChangedListener { _, destination, _ ->
                toolbar.subtitle = destination.label
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }
}
