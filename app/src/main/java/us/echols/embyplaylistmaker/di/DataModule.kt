package us.echols.embyplaylistmaker.di

import android.arch.persistence.room.Room
import android.preference.PreferenceManager
import org.koin.dsl.module.applicationContext
import us.echols.embyplaylistmaker.data.EmbyDataSource
import us.echols.embyplaylistmaker.data.EmbyRepository
import us.echols.embyplaylistmaker.data.FileHelper
import us.echols.embyplaylistmaker.data.db.EmbyDb
import us.echols.embyplaylistmaker.data.db.EmbyLocalDataSource
import us.echols.embyplaylistmaker.data.network.EmbyApiClient
import us.echols.embyplaylistmaker.data.network.EmbyApiHelper
import us.echols.embyplaylistmaker.data.network.EmbyRemoteDataSource
import us.echols.embyplaylistmaker.data.prefs.PreferencesHelper
import us.echols.embyplaylistmaker.data.prefs.SharedPreferencesHelper

const val EMBY_LOCAL_DATA_SOURCE = "EmbyLocalDataSource"
const val EMBY_REMOTE_DATA_SOURCE = "EmbyRemoteDataSource"
const val DATABASE_NAME: String = "EmbyDatabase"

val dataModule = applicationContext {

    bean {
        SharedPreferencesHelper(
                PreferenceManager.getDefaultSharedPreferences(get())
        ) as PreferencesHelper
    }

    bean { EmbyRepository(get(EMBY_REMOTE_DATA_SOURCE), get(EMBY_LOCAL_DATA_SOURCE)) }

    bean(EMBY_REMOTE_DATA_SOURCE) { EmbyRemoteDataSource(get()) as EmbyDataSource }

    bean(EMBY_LOCAL_DATA_SOURCE) { EmbyLocalDataSource(get(), get()) as EmbyDataSource }

    bean {
        Room.databaseBuilder(get(), EmbyDb::class.java, DATABASE_NAME).build()
    }

    bean { EmbyApiClient(get()) as EmbyApiHelper }

    factory { FileHelper(get()) }

}