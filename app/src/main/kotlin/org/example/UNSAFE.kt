package org.example

@Suppress("UNCHECKED_CAST")
fun <T: Any> clone(obj: T): T {
    val clazz = obj::class.java
    val copy = UNSAFE.allocateInstance(clazz) as T
    copyDeclaredFields(obj, copy, clazz)
    return copy
}

private val UNSAFE = Class.forName("sun.misc.Unsafe")
    .getDeclaredField("theUnsafe")
    .apply { isAccessible = true }
    .get(null) as sun.misc.Unsafe

private tailrec fun <T> copyDeclaredFields(obj: T, copy: T, clazz: Class<out T>) {
    for (field in clazz.declaredFields) {
        field.isAccessible = true
        val v = field.get(obj)
        field.set(copy, if (v === obj) copy else v)
    }
    val superclass = clazz.superclass
    if (superclass != null) copyDeclaredFields(obj, copy, superclass)
}