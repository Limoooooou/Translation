import android.util.Log
import androidx.annotation.VisibleForTesting
import com.example.translation.api.BaiduSignUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

object BaiduTranslationApi {

    private val appId = "20250609002377601"
    private val secretKey = "bRZ7m1bV5nztVBsQdhF2"

    @VisibleForTesting
    var okHttpClient: OkHttpClient = OkHttpClient()
        private set
    /**
     * 执行翻译请求
     * @param text 要翻译的文本
     * @param from 源语言代码
     * @param to 目标语言代码
     * @return 翻译结果字符串
     */
    fun translateText(text: String, from: String, to: String): String {
        // 验证参数
        if (text.isEmpty()) {
            return "翻译文本不能为空"
        }
        if (from.isEmpty() || to.isEmpty()) {
            return "源语言和目标语言不能为空"
        }
        if (appId.isEmpty() || secretKey.isEmpty()) {
            return "AppID和密钥不能为空"
        }

        // 生成随机数salt
        val salt = System.currentTimeMillis().toString()

        // 生成签名
        val sign = BaiduSignUtil.generateSign(appId, text, salt, secretKey)

        // 构建请求URL
        val url = "https://fanyi-api.baidu.com/api/trans/vip/translate" +
                "?q=${java.net.URLEncoder.encode(text, "UTF-8")}" +
                "&from=$from" +
                "&to=$to" +
                "&appid=$appId" +
                "&salt=$salt" +
                "&sign=$sign"

        Log.d("BaiduTranslationApi", "Request URL: $url")

        // 创建OkHttp请求
        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "No error body"
                throw IOException("翻译请求失败: HTTP ${response.code} - $errorBody")
            }

            val responseBody = response.body?.string()
                ?: throw IOException("响应体为空")

            return parseTranslationResponse(responseBody)
        }
    }

    @VisibleForTesting
    fun parseTranslationResponseForTest(jsonResponse: String) = parseTranslationResponse(jsonResponse)


    /**
     * 解析百度翻译API的JSON响应
     * @param jsonResponse JSON格式的响应字符串
     * @return 翻译结果字符串
     */
    private fun parseTranslationResponse(jsonResponse: String): String {
        Log.d("BaiduImageTranslation", "Response: $jsonResponse")
        return try {
            // 使用org.json库解析JSON
            val jsonObject = JSONObject(jsonResponse)

            // 检查是否有trans_result字段
            if (!jsonObject.has("trans_result")) {
                throw IOException("响应中缺少trans_result字段")
            }

            val transResultArray = jsonObject.getJSONArray("trans_result")
            if (transResultArray.length() == 0) {
                throw IOException("翻译结果为空")
            }

            // 获取第一个翻译结果
            val firstResult = transResultArray.getJSONObject(0)
            val translatedText = firstResult.getString("dst")

            translatedText
        } catch (e: Exception) {
            Log.e("BaiduTranslationApi", "解析翻译响应失败", e)
            return "解析翻译响应失败"
        }
    }

    // 测试专用的设置方法
    @VisibleForTesting
    fun setTestClient(client: OkHttpClient) {
        okHttpClient = client
    }
}
