package com.moralist.shared.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
