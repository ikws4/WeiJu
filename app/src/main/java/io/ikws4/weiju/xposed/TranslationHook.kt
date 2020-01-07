package io.ikws4.weiju.xposed

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.widget.TextView
import androidx.core.content.contentValuesOf
import io.ikws4.library.xposedktx.hookMethod
import io.ikws4.library.xposedktx.invokeMethod
import io.ikws4.weiju.provider.DatabaseProvider
import io.ikws4.weiju.utilities.WEIJU_SP
import io.ikws4.weiju.utilities.XSPUtils
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import toMD5
import toSHA256
import kotlin.random.Random


/**
 * 翻译功能
 * 支持的API：搜狗免费、搜狗、百度、有道
 * @property pkgName String
 * @property sp XSPUtils
 * @property weiJuSp XSPUtils
 * @property isEnable Boolean
 * @property isSaveOfflineTranslationData Boolean
 * @property saveOfflineTranslationDataWordCount Int
 * @property translationApi String
 * @property salt String
 * @constructor
 */
@ExperimentalCoroutinesApi
class TranslationHook(context: Context, private val pkgName: String) {
    private val sp = XSPUtils(context, pkgName)
    private val weiJuSp = XSPUtils(context, WEIJU_SP)
    // 是否开启翻译功能
    private val isEnable = sp.getBoolean("is_enable_translation")
    // 是否保存翻译数据
    private val isSaveOfflineTranslationData = weiJuSp.getBoolean("is_save_offline_translation_data")
    // 保存翻译的字符限制
    private val saveOfflineTranslationDataWordCount = weiJuSp.getInt("save_offline_translation_data_word_count")
    // 获取使用的api
    private val translationApi = sp.getString("translation_api")
    // 生成随机数，用于api接口
    private val salt = Random(1).nextInt().toString()

    init {
        if (isEnable) {
            when (translationApi) {
                "Sogou" -> {
                    val from = sp.getString("api_sogou_from")
                    val to = sp.getString("api_sogou_to")
                    val appid = weiJuSp.getString("api_sogou_appid")
                    val key = weiJuSp.getString("api_sogou_key")
                    translation(from, to) {
                        sogou(it, from, to, appid, key)
                    }
                }
                "Baidu" -> {
                    val from = sp.getString("api_baidu_from")
                    val to = sp.getString("api_baidu_to")
                    val appid = weiJuSp.getString("api_baidu_appid")
                    val key = weiJuSp.getString("api_baidu_key")
                    translation(from, to) {
                        baidu(it, from, to, appid, key)
                    }
                }
                "Youdao" -> {
                    val from = sp.getString("api_youdao_from")
                    val to = sp.getString("api_youdao_to")
                    val appid = weiJuSp.getString("api_youdao_appid")
                    val key = weiJuSp.getString("api_youdao_key")
                    translation(from, to) {
                        youdao(it, from, to, appid, key)
                    }
                }
            }
        }
    }

    /**
     * Doc https://deepi.sogou.com/doccenter/texttranslatedoc
     * @param from String
     * @param to String
     * @param appid String
     * @param key String
     */
    private fun sogou(query: String, from: String, to: String, appid: String, key: String): String {
        try {
            val client = OkHttpClient()
            val sign = (appid + query + salt + key).toMD5()
            val body = FormBody.Builder()
                .add("q", query)
                .add("from", from)
                .add("to", to)
                .add("pid", appid)
                .add("salt", salt)
                .add("sign", sign)
                .build()
            val request = Request.Builder()
                .url("http://fanyi.sogou.com/reventondc/api/sogouTranslate")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .post(body)
                .build()
            val response = client
                .newCall(request)
                .execute()
            val json = JSONObject(response.body!!.string())
            return json.getString("translation")
        } catch (ex: Exception) {
            return ""
        }
    }

    /**
     * Doc https://api.fanyi.baidu.com/api/trans/product/apidoc
     * @param from String
     * @param to String
     * @param appid String
     * @param key String
     */
    private fun baidu(query: String, from: String, to: String, appid: String, key: String): String {
        try {
            val client = OkHttpClient()
            val sign = (appid + query + salt + key).toMD5()
            val body = FormBody.Builder()
                .add("q", query)
                .add("from", from)
                .add("to", to)
                .add("appid", appid)
                .add("salt", salt)
                .add("sign", sign)
                .build()
            val request = Request.Builder()
                .url("https://fanyi-api.baidu.com/api/trans/vip/translate")
                .post(body)
                .build()
            val response = client
                .newCall(request)
                .execute()
            val json = JSONObject(response.body!!.string())
            return json.getJSONArray("trans_result").getJSONObject(0).getString("dst")
        } catch (ex: Exception) {
            MainHook.log(ex)
            return ""
        }
    }

    /**
     * Doc https://ai.youdao.com/docs/doc-trans-api.s
     * @param from String
     * @param to String
     * @param appid String
     * @param key String
     */
    private fun youdao(query: String, from: String, to: String, appid: String, key: String): String {
        try {
            val client = OkHttpClient()
            val length = query.length
            val input = if (length > 20) {
                val result = query.substring(0, 10) + length + query.substring(length - 10, length)
                result
            } else {
                query
            }
            val currentTime = System.currentTimeMillis() / 1000
            val sign = (appid + input + salt + currentTime + key).toSHA256()
            val body = FormBody.Builder()
                .add("q", query)
                .add("from", from)
                .add("to", to)
                .add("appKey", appid)
                .add("salt", salt)
                .add("sign", sign)
                .add("signType", "v3")
                .add("curtime", currentTime.toString())
                .build()
            val request = Request.Builder()
                .url("https://openapi.youdao.com/api")
                .post(body)
                .build()
            val response = client
                .newCall(request)
                .execute()
            val json = JSONObject(response.body!!.string())
            return json.getJSONArray("translation").getString(0)
        } catch (ex: Exception) {
            MainHook.log(ex)
            return ""
        }
    }


    private fun translationSogouFree(from: String, to: String, apiResult: (query: String) -> String) {
        TextView::class.java.hookMethod("setText", CharSequence::class.java, TextView.BufferType::class.java,
            afterHookedMethod = { param ->
                val context = this.context
                val query = (param.args[0] ?: "").toString().trim()
                val type = param.args[1] as TextView.BufferType

                val translationInfo = context.contentResolver.query(
                    Uri.parse(DatabaseProvider.TRANSLATION_INFO_URI + "/query/result/" + query), null, null,
                    arrayOf(pkgName, from, to), null
                )
                translationInfo?.moveToFirst()

                val user =
                    context.contentResolver.query(Uri.parse(DatabaseProvider.USER_URI + "/query/freeSogouApiAmount"), null, null, arrayOf(), null)
                user?.moveToFirst()
                val freeSogouApiAmount = user?.getInt(0) ?: 0

                if (freeSogouApiAmount > query.length) {
                    try {
                        if ((translationInfo == null || translationInfo.count == 0) && query != "") {
                            GlobalScope.launch(Dispatchers.IO) {
                                val result = apiResult.invoke(query)
                                // 翻译完成回调
                                context.contentResolver.update(
                                    Uri.parse(DatabaseProvider.USER_URI + "/update/freeSogouApiAmount"),
                                    contentValuesOf(Pair("freeSogouApiAmount", query.length)), null, null
                                )
                                withContext(Dispatchers.Main) {
                                    // 保存数据到本地
                                    if (isSaveOfflineTranslationData && query.length <= saveOfflineTranslationDataWordCount) {
                                        val contextValues = ContentValues().apply {
                                            put("pkgName", pkgName)
                                            put("query", query)
                                            put("from", from)
                                            put("to", to)
                                            put("result", result)
                                        }
                                        context.contentResolver.insert(Uri.parse(DatabaseProvider.TRANSLATION_INFO_URI + "/insert/"), contextValues)
                                    }
                                    this@hookMethod.invokeMethod(TextView::class.java, "setText", result, type, true, 0)
                                }
                            }
                        } else {
                            val translationInfoResult = translationInfo?.getString(0) ?: throw NullPointerException("Not fount translation reult")
                            invokeMethod(TextView::class.java, "setText", translationInfoResult, type, true, 0)
                        }
                    } catch (ex: Exception) {
                        MainHook.log(ex)
                    } finally {
                        translationInfo?.close()
                        user?.close()
                    }
                }
            })
    }
    
    /**
     * 使用方法
     * translation(from,to){query->
     *     //在这里返回result
     * }
     * @param from String
     * @param to String
     * @param apiResult Function1<[@kotlin.ParameterName] String, String>
     */
    private fun translation(from: String, to: String, apiResult: (query: String) -> String) {


        TextView::class.java.hookMethod("setText", CharSequence::class.java, TextView.BufferType::class.java,
            afterHookedMethod = { param ->
                val context = this.context
                val query = (param.args[0] ?: "").toString().trim()
                val type = param.args[1] as TextView.BufferType

                val translationInfo = context.contentResolver.query(
                    Uri.parse(DatabaseProvider.TRANSLATION_INFO_URI + "/query/result/" + query), null, null,
                    arrayOf(pkgName, from, to), null
                )
                translationInfo?.moveToFirst()

                try {
                    if ((translationInfo == null || translationInfo.count == 0) && query != "") {
                        GlobalScope.launch(Dispatchers.IO) {
                            val result = apiResult.invoke(query)


                            if (result != "") {
                                withContext(Dispatchers.Main) {
                                    // 保存数据到本地
                                    if (isSaveOfflineTranslationData && query.length <= saveOfflineTranslationDataWordCount) {
                                        val contextValues = ContentValues().apply {
                                            put("pkgName", pkgName)
                                            put("query", query)
                                            put("from", from)
                                            put("to", to)
                                            put("result", result)
                                        }
                                        context.contentResolver.insert(Uri.parse(DatabaseProvider.TRANSLATION_INFO_URI + "/insert/"), contextValues)
                                    }
                                    this@hookMethod.invokeMethod(TextView::class.java, "setText", result, type, true, 0)
                                }
                            }
                        }
                    } else {
                        val translationInfoResult = translationInfo?.getString(0) ?: throw NullPointerException("Not fount translation reult")
                        invokeMethod(TextView::class.java, "setText", translationInfoResult, type, true, 0)
                    }
                } catch (ex: Exception) {
                    MainHook.log(ex)
                } finally {
                    translationInfo?.close()
                }
            })
    }
}