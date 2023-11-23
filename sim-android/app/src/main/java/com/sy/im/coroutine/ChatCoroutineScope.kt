package com.sy.im.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 *
 */
internal object ChatCoroutineScope : CoroutineScope {

    /*
    SupervisorJob() 该作用域可以在子协程发生异常时继续运行其他子协程，而不会影响整个作用域的运行。
    这样在主线程运行，发生异常不会阻塞主线程
     */
    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate // 在主线程上立即执行协程

}