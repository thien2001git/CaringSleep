package com.hoang.newpipe.presentation.manager.helper


import androidx.collection.LruCache
import com.hoang.common.utils.AppLog
import org.schabi.newpipe.extractor.Info
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoCache @Inject constructor() {
    /**
     * Identifies the type of [Info] to put into the cache.
     */
    enum class Type {
        STREAM,
        CHANNEL,
        CHANNEL_TAB,
        COMMENTS,
        PLAYLIST,
        KIOSK,
    }

    fun getFromKey(
        serviceId: Int,
        url: String,
        cacheType: Type
    ): Info? {
        AppLog.d("getFromKey() called with: serviceId = [$serviceId], url = [$url]")
        synchronized(LRU_CACHE) {
            return getInfo(keyOf(serviceId, url, cacheType))
        }
    }

    fun putInfo(
        serviceId: Int,
        url: String,
        info: Info,
        cacheType: Type
    ) {
        AppLog.d("putInfo() called with: info = [$info]")

        val expirationMillis: Long = info.getCacheExpirationMillis()
        synchronized(LRU_CACHE) {
            val data = CacheData(info, expirationMillis)
            LRU_CACHE.put(keyOf(serviceId, url, cacheType), data)
        }
    }

    fun removeInfo(
        serviceId: Int,
        url: String,
        cacheType: Type
    ) {
        AppLog.d("removeInfo() called with: serviceId = [$serviceId], url = [$url]")
        synchronized(LRU_CACHE) {
            LRU_CACHE.remove(keyOf(serviceId, url, cacheType))
        }
    }

    fun clearCache() {
        AppLog.d("clearCache() called")
        synchronized(LRU_CACHE) {
            LRU_CACHE.evictAll()
        }
    }

    fun trimCache() {
        AppLog.d("trimCache() called")
        synchronized(LRU_CACHE) {
            removeStaleCache()
            LRU_CACHE.trimToSize(TRIM_CACHE_TO)
        }
    }

    val size: Long
        get() =
            synchronized(LRU_CACHE) {
                LRU_CACHE.size().toLong()
            }


    private class CacheData(val info: Info, timeoutMillis: Long) {
        private val expireTimestamp: Long = System.currentTimeMillis() + timeoutMillis

        val isExpired: Boolean
            get() = System.currentTimeMillis() > expireTimestamp
    }

    companion object {

        private const val MAX_ITEMS_ON_CACHE = 60

        /**
         * Trim the cache to this size.
         */
        private const val TRIM_CACHE_TO = 30

        private val LRU_CACHE = LruCache<String, CacheData>(MAX_ITEMS_ON_CACHE)

        private fun keyOf(
            serviceId: Int,
            url: String,
            cacheType: Type
        ): String {
            return serviceId.toString() + ":" + cacheType.ordinal + ":" + url
        }

        private fun removeStaleCache() {
            for ((key, data) in LRU_CACHE.snapshot()) {
                if (data.isExpired) {
                    LRU_CACHE.remove(key)
                }
            }
        }

        private fun getInfo(key: String): Info? {
            val data = LRU_CACHE[key] ?: return null

            if (data.isExpired) {
                LRU_CACHE.remove(key)
                return null
            }

            return data.info
        }
    }
}
