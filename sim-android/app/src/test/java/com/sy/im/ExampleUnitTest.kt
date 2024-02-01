package com.sy.im

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sy.im.model.FriendRequest
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Type

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val requests = "[{\"createTime\":1705673211000,\"fromId\":3,\"id\":2,\"isAgreed\":0,\"sendId\":1}]"
        val friendRequestListType: Type =
            object : TypeToken<List<FriendRequest?>?>() {}.type
        val result: List<FriendRequest> = Gson().fromJson(requests, friendRequestListType)
//        val result: List<FriendRequest> =
//            Gson().fromJson(requests, object : TypeToken<List<FriendRequest?>?>() {}.type)

        println(result)

    }
}
