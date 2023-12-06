package com.sy.im.test;

import org.junit.Test;

import static org.junit.Assert.*;

import com.sy.im.client.IMSClientBootstrap;
import com.sy.im.client.IMSConnectStatusImpl;
import com.sy.im.client.IMSEventListenerImpl;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TestConnect(){

        String hosts = "[{\"host\":\"127.0.0.1\", \"port\":9090}]";
        IMSClientBootstrap bootstrap = IMSClientBootstrap.getInstance();
        bootstrap.init(
                hosts,
                new IMSEventListenerImpl(),
                new IMSConnectStatusImpl(),
                0);

    }
}