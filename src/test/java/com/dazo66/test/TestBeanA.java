package com.dazo66.test;

import com.dazo66.JoinerKey;

/**
 * @author dazo
 */
public class TestBeanA {
    private String name;
    private Integer age;
    private String address;
    @JoinerKey("testA")
    private Long testBeanCId;

    public TestBeanA(Long id) {
        this.testBeanCId = id;
    }

}
