package com.example.fundoos.utils

import java.util.concurrent.atomic.AtomicInteger

object RandomUtils {
    private val seed = AtomicInteger()

    fun getRandomInt() = seed.getAndIncrement() + System.currentTimeMillis().toInt()
}