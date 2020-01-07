package io.ikws4.weiju.utilities

import io.ikws4.weiju.BuildConfig

const val DATABASE_NAME = "weiju-db"
const val WEIJU_PKG_NAME = BuildConfig.APPLICATION_ID
const val FREE_SOGOU_API_REWARDED_AD_ID = "ca-app-pub-7928027245732586/3326443327"
const val TEST_DEVICE_ID = "43805120C657BBBA84A17AE9C2BBBCA7"
// sharedPreferences name constants
const val HOOK_LIST_SP = "hook_list" // 用于在Xposed中的的判断
const val TEMPLATE_SP = "$WEIJU_PKG_NAME.template"
const val WEIJU_SP = "${WEIJU_PKG_NAME}_preferences"
