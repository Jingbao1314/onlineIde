package com.jingbao.service;

import com.jingbao.serverAnnotation.RequestMapping;
import com.jingbao.serverAnnotation.Service;

/**
 * @author jijngbao
 * @date 19-7-20
 */
@Service
public class Test {
    @RequestMapping(value = "/test")
    public void test(){
        System.out.println("test");
    }

    @RequestMapping(value = "/dataTest")
    public String test(String data){
        System.out.println("data test");
        return "xxxxxx";
    }
}
