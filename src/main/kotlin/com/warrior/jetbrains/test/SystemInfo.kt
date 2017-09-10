package com.warrior.jetbrains.test

object SystemInfo {

    val osName: String = System.getProperty("os.name")

    val isMac: Boolean = osName.toLowerCase().startsWith("mac")
}
