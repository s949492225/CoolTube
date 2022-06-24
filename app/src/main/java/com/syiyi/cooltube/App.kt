package com.syiyi.cooltube

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.syiyi.cooltube.util.UserAgentInterceptor
import com.syiyi.cooltube.util.initComposeHacking
import com.syiyi.cooltube.util.toast
import dagger.hilt.android.HiltAndroidApp
import me.rerere.compose_setting.preference.initComposeSetting
import okhttp3.OkHttpClient

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        // Init MMKV
        initComposeSetting()

        // Init Compose Hacking
        initComposeHacking()
            .onFailure {
                toast("Failed to inject compose hacking")
            }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(UserAgentInterceptor())
                    .retryOnConnectionFailure(true)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("images"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .build()
    }
}