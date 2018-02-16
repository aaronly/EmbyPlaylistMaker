package us.echols.embyplaylistmaker.di

import org.koin.dsl.module.applicationContext
import us.echols.embyplaylistmaker.data.network.serverrequest.EmbyServerRequest

val serverModule = applicationContext {
    factory { EmbyServerRequest() }
}
