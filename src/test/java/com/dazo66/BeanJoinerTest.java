package com.dazo66;

import com.dazo66.test.TestBeanA;
import com.dazo66.test.TestBeanB;
import com.dazo66.test.TestBeanC;
import com.dazo66.test.TestBeanD;

import java.util.Arrays;
import java.util.List;

/**
 * @author dazo
 */
public class BeanJoinerTest {

    public static void main(String[] args) {
        List<TestBeanA> testBeanAS = Arrays.asList(new TestBeanA(1L), new TestBeanA(2L), new TestBeanA(3L));
        List<TestBeanB> testBeanBS = Arrays.asList(new TestBeanB(4L), new TestBeanB(5L), new TestBeanB(1L));
        List<TestBeanD> testBeanDS = Arrays.asList(new TestBeanD(1L), new TestBeanD(1L), new TestBeanD(2L), new TestBeanD(5L));
        List<TestBeanC> testBeanCS = Arrays.asList(new TestBeanC(1L), new TestBeanC(2L), new TestBeanC(4L));
        BeanJoiner.join(testBeanCS, testBeanAS);
        BeanJoiner.join(testBeanCS, testBeanBS);
        BeanJoiner.join(testBeanCS, testBeanDS);
        System.out.println(testBeanCS);
    }

}
