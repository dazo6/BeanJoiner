package com.dazo66.test;

import com.dazo66.JoinerKey;
import com.dazo66.JoinerValue;

import java.util.List;

/**
 * @author dazo
 */
public class TestBeanC {
    @JoinerValue("testA")
    private TestBeanA testBeanA;

    @JoinerValue("testB")
    private TestBeanB testBeanB;

    @JoinerValue(value = "testD", isGroup = true)
    private List<TestBeanD> testBeanDList;

    @JoinerKey({"testA", "testB", "testD"})
    private Long mainId;


    public TestBeanC(Long id) {
        this.mainId = id;
    }
}
