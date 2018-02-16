package us.echols.embyplaylistmaker.util

interface KoinComponent

///**
// * lazy inject given dependency for KoinComponent
// * @param name - bean name / optional
// */
//inline fun <reified T> KoinComponent.inject(name: String = ""): Lazy<T> = lazy {
//    StandAloneContext.koinContext.get<T>(name)
//}
//
///**
// * lazy inject given property for KoinComponent
// * @param key - key property
// * throw MissingPropertyException if property is not found
// */
//inline fun <reified T> KoinComponent.property(key: String) = lazy {
//    StandAloneContext.koinContext.getProperty<T>(key)
//}
//
///**
// * lazy inject given property for KoinComponent
// * give a default value if property is missing
// *
// * @param key - key property
// * @param defaultValue - default value if property is missing
// *
// */
//inline fun <reified T> KoinComponent.property(key: String, defaultValue: T) = lazy {
//    StandAloneContext.koinContext.getProperty(key, defaultValue)
//}