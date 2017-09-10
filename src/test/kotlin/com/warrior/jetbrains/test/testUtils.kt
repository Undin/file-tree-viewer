package com.warrior.jetbrains.test

import com.warrior.jetbrains.test.model.FileInfo
import com.warrior.jetbrains.test.model.FileInfoLoader
import com.warrior.jetbrains.test.model.Result
import org.mockito.ArgumentMatcher
import org.mockito.Mockito
import java.nio.file.Paths

fun FileInfoLoader.getChildrenSync(file: FileInfo): List<FileInfo> {
    var children: List<FileInfo>? = null
    getChildrenAsync(file) { children = it }.get()
    return children ?: error("Result list is supposed to be not empty")
}

fun FileInfoLoader.resolveFtpServerSync(host: String, username: String?,
                                        password: CharArray?, name: String?): Result<FileInfo, String> {
    var result: Result<FileInfo, String>? = null
    resolveFtpServerAsync(host, username, password, name) { result = it }.get()
    return result ?: error("Result is supposed to be not empty")
}

fun FileInfoLoader.resourceFile(path: String, isRoot: Boolean): FileInfo {
    val realPath = Paths.get(javaClass.classLoader.getResource(path).path)
    return getLocalFile(realPath, isRoot) ?: error("Failed to create 'FileInfo' from $path")
}

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

inline fun <reified T> any(): T {
    Mockito.any(T::class.java)
    return uninitialized()
}

fun <T> argThat(matcher: ArgumentMatcher<T>): T {
    Mockito.argThat(matcher)
    return uninitialized()
}

// hack to use Mockito.any() from kotlin
// see https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791
fun <T> uninitialized(): T = null as T
