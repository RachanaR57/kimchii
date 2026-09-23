package org.app.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform