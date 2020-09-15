package lab1

import kotlin.reflect.KClass

private class InstanceNotFoundException(message: String) : RuntimeException(message)

@Suppress("UNCHECKED_CAST")
object ServiceLocator {

    private val instanceMap = mutableMapOf<KClass<*>, MutableMap<String?, *>>()

    private fun <T : Any> createQualifiedMap(forClass: KClass<T>): MutableMap<String?, T> {
        return mutableMapOf<String?, T>().also { instanceMap[forClass] = it }
    }

    fun <T : Any> register(instance: T, kClass: KClass<T>, qualifier: String? = null) {
        val qualifiedMap = (instanceMap[kClass] as? MutableMap<String?, T>) ?: createQualifiedMap(forClass = kClass)
        qualifiedMap[qualifier] = instance
    }

    fun <T : Any> get(kClass: KClass<T>, qualifier: String? = null): T {
        val qualifiedMap =
            instanceMap[kClass] ?: throw InstanceNotFoundException("No instances of type ${kClass.qualifiedName} found")
        val typedMap = qualifiedMap as Map<String?, T>
        return typedMap[qualifier] ?: throw InstanceNotFoundException("Instance with qualifier '$qualifier' not found")
    }
}