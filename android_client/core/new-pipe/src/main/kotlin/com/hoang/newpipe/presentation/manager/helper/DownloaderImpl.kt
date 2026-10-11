package com.hoang.newpipe.presentation.manager.helper


import com.hoang.common.domain.entity.Resource
import com.hoang.newpipe.domain.usecase.GetRestrictedModeUseCase
import kotlinx.coroutines.flow.first
import okhttp3.CompressionInterceptor
import okhttp3.Gzip
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.brotli.Brotli
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloaderImpl @Inject constructor(
    private val getRestrictedModeUseCase: GetRestrictedModeUseCase,
    private val infoCache: InfoCache,
) : Downloader() {
    private val mCookies: MutableMap<String?, String?> = mutableMapOf()
    val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(
            30,
            TimeUnit.SECONDS
        ) //                .cache(new Cache(new File(context.getExternalCacheDir(), "okhttp"),
        //                        16 * 1024 * 1024))
        .addInterceptor(
            CompressionInterceptor(
                Brotli,
                Gzip
            )
        )
        .build()


    fun getCookies(url: String): String {
        val youtubeCookie: String? =
            if (url.contains(YOUTUBE_DOMAIN))
                getCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY)
            else
                null

        // Re-captcha cookie is always added TODO: not sure if this is necessary
        return listOfNotNull(youtubeCookie, getCookie(RECAPTCHA_COOKIES_KEY))
            .map { cookies: String? ->
                cookies!!.split("; *".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            .distinct()
            .joinToString("; ")
    }

    fun getCookie(key: String?): String? = mCookies[key]

    fun setCookie(key: String?, cookie: String?) {
        mCookies[key] = cookie
    }

    fun removeCookie(key: String?) {
        mCookies.remove(key)
    }

    suspend fun updateYoutubeRestrictedModeCookies() {
        val restrictedModeEnabled =
            when (val result = getRestrictedModeUseCase(GetRestrictedModeUseCase.Params)) {
                is Resource.Success -> {
                    result.data.first()
                }

                is Resource.Error -> {
                    false
                }
            }

        updateYoutubeRestrictedModeCookies(restrictedModeEnabled == true)
    }

    fun updateYoutubeRestrictedModeCookies(youtubeRestrictedModeEnabled: Boolean) {
        if (youtubeRestrictedModeEnabled) {
            setCookie(
                YOUTUBE_RESTRICTED_MODE_COOKIE_KEY,
                YOUTUBE_RESTRICTED_MODE_COOKIE
            )
        } else {
            removeCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY)
        }
        infoCache.clearCache()
    }

    /**
     * Get the size of the content that the url is pointing by firing a HEAD request.
     *
     * @param url an url pointing to the content
     * @return the size of the content, in bytes
     */
    fun getContentLength(url: String?): Long =
        head(url).getHeader("Content-Length")?.toLong() ?: 0L

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        var requestBody: RequestBody? = null
        if (dataToSend != null) {
            requestBody = dataToSend.toRequestBody(null)
        }

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, requestBody)
            .url(url)
            .addHeader("User-Agent", USER_AGENT)

        val cookies = getCookies(url)
        if (cookies.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", cookies)
        }

        headers.forEach { (headerName: String?, headerValueList: MutableList<String?>?) ->
            requestBuilder.removeHeader(headerName!!)
            headerValueList!!.forEach(Consumer { headerValue: String? ->
                requestBuilder.addHeader(
                    headerName,
                    headerValue!!
                )
            })
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (response.code == 429) {
                throw ReCaptchaException("reCaptcha Challenge requested", url)
            }
            var responseBodyToReturn: String? = null
            response.body.use { body ->
                responseBodyToReturn = body.string()
            }
            val latestUrl = response.request.url.toString()
            return Response(
                response.code,
                response.message,
                response.headers.toMultimap(),
                responseBodyToReturn,
                latestUrl
            )
        }
    }
}