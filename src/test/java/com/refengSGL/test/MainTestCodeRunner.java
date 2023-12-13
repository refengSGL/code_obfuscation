package com.refengSGL.test;

import com.alibaba.fastjson.JSONArray;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 19:27 2023-02-27
 */
public class MainTestCodeRunner {
    @Test
    public void testArrayListToJson() {
        ArrayList<String> list = new ArrayList<>();
        list.add("param1");
        list.add("param2");
        list.add("param3");

        System.out.println(JSONArray.toJSON(list));
    }
}
