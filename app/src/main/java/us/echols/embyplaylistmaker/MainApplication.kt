package us.echols.embyplaylistmaker

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor.Level
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin
import us.echols.embyplaylistmaker.di.embyPlaylistMakerModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        // Stetho
        Stetho.initializeWithDefaults(this)

        // KOIN
        startKoin(this, embyPlaylistMakerModules)

        // FastAndroidNetworking
        AndroidNetworking.initialize(applicationContext)
        if (BuildConfig.DEBUG) {
            AndroidNetworking.enableLogging(Level.BODY)
        }
    }
}
