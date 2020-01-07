package io.ikws4.weiju.ui.activitys

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@SuppressLint("Registered")
open class BasicActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 申请权限
     * @param permissions Array<String>
     */
    fun getPermission(permissions:Array<String>){
        permissions.forEach {permission ->
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){

                }else{
                    ActivityCompat.requestPermissions(this, arrayOf(permission),1)
                }
            }

        }
    }
}