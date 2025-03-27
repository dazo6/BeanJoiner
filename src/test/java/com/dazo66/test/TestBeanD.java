package com.dazo66.test;

import com.dazo66.JoinerKey;

/**
 * @author dazo
 */
public class TestBeanD {

    private Long id;
    @JoinerKey("testD")
    private Long testBeanDId;


    public TestBeanD(Long id) {
        this.testBeanDId = id;
    }
}
