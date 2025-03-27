package com.dazo66.test;

import com.dazo66.JoinerKey;
import com.dazo66.JoinerValue;

/**
 * @author dazo
 */
public class TestBeanB {

    private Long id;
    @JoinerKey("testB")
    private Long testBeanCId;


    public TestBeanB(Long id) {
        this.testBeanCId = id;
    }
}
