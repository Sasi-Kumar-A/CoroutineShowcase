package com.example.coroutines

import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Helper function to print logs showing the executing thread and timestamp
fun log(message: String) {
    val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    val thread = Thread.currentThread().name
    println("[$time] [$thread] $message")
}

// -------------------------------------------------------------------------
// 1. suspend functions: non-blocking async routines
// -------------------------------------------------------------------------

// Simulates an I/O network fetch (e.g., querying an API)
suspend fun fetchUserData(): String {
    log("Fetching user profile from network...")
    delay(1500) // Non-blocking delay
    return "User: John Doe"
}

// Simulates a second concurrent network call
suspend fun fetchAccountBalance(): Double {
    log("Fetching account balance from bank server...")
    delay(1200)
    return 8450.75
}

// Simulates a CPU-heavy computation running on Dispatchers.Default
suspend fun calculateCreditRiskScore(balance: Double): Int = withContext(Dispatchers.Default) {
    log("Calculating credit score on CPU dispatcher...")
    var score = 300
    // Simulating heavy CPU cycles
    for (i in 1..10_000_000) {
        score = (score + (balance.toInt() % 7)) % 850
    }
    score + 400
}

// Demonstrating coroutineScope for structured concurrency
suspend fun aggregateDashboardData(): Pair<String, Double> = coroutineScope {
    log("Starting concurrent child jobs inside custom coroutineScope...")

    // 2. async & await: Parallel processing
    val userDeferred: Deferred<String> = async(Dispatchers.IO) {
        fetchUserData()
    }

    val balanceDeferred: Deferred<Double> = async(Dispatchers.IO) {
        fetchAccountBalance()
    }

    // Both calls run in parallel; await retrieves their values
    val user = userDeferred.await()
    val balance = balanceDeferred.await()

    Pair(user, balance)
}

// -------------------------------------------------------------------------
// 3. Entry Point using runBlocking
// -------------------------------------------------------------------------
fun main() = runBlocking {
    log("=== Coroutine Showcase Application Started ===")

    // 4. launch: Fire-and-forget background job
    val backgroundLoggerJob: Job = launch(Dispatchers.IO) {
        log("Background logger started.")
        repeat(3) { count ->
            delay(800)
            log("Background health check ping #${count + 1}")
        }
        log("Background logger completed.")
    }

    // 5. withContext(Dispatchers.IO): Offloading disk/network work
    val localCacheResult = withContext(Dispatchers.IO) {
        log("Reading token from local storage file...")
        delay(400)
        "AUTH_TOKEN_XYZ_998"
    }
    log("Retrieved token: $localCacheResult")

    // Running parallel async tasks through our suspend function
    val (user, balance) = aggregateDashboardData()
    log("Combined Results -> $user | Balance: $$balance")

    // Running CPU-heavy task via Dispatchers.Default
    val score = calculateCreditRiskScore(balance)
    log("Calculated Risk Score: $score")

    // 6. Dispatchers.Main: Switching to UI/Main Thread
    withContext(Dispatchers.Main) {
        log("Safely updating UI state with User='$user', Score=$score")
    }

    // Ensure our launched fire-and-forget job completes before shutting down
    backgroundLoggerJob.join()

    log("=== Coroutine Showcase Application Finished Cleanly ===")
}