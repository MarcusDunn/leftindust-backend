package com.leftindust.mockingbird.extensions

fun <T> MutableSet<T>.replaceAll(set: Set<T>) {
    this.clear()
    this.addAll(set)
}

fun <T> MutableSet<T>.replaceAllIfNotNull(set: Set<T>?) {
    if (set == null) {
        return
    } else {
        this.clear()
        this.addAll(set)
    }
}