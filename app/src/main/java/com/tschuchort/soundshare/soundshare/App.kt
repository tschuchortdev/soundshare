package com.tschuchort.soundshare.soundshare

import android.support.multidex.MultiDexApplication
import com.akaita.java.rxjava2debug.RxJava2Debug
import com.facebook.stetho.Stetho
import com.google.firebase.crash.FirebaseCrash
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }


        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)

            LeakCanary.install(this)

            FirebaseCrash.setCrashCollectionEnabled(false)
            Timber.plant(Timber.DebugTree())
        }
        else {
            FirebaseCrash.setCrashCollectionEnabled(true)
            Timber.plant(FirebaseCrashReportTree())
        }

        //this has to be done AFTER initializing crash reporting (firebase, crashlytics etc)
        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf("com.tschuchort.soundshare.soundshare"))


    }
}


