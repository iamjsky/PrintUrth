package com.printurth.obj;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjModelTest {
    @Test
    public void testParseInts() throws Exception {
        int[] ints = new int[4];

        ObjModel.parseInts("1/2/3", ints);
        assertEquals(ints[0], 1);
        assertEquals(ints[1], 2);
        assertEquals(ints[2], 3);

        ObjModel.parseInts("111/222/333/444", ints);
        assertEquals(ints[0], 111);
        assertEquals(ints[1], 222);
        assertEquals(ints[2], 333);
        assertEquals(ints[3], 444);

        ObjModel.parseInts("12//34", ints);
        assertEquals(ints[0], 12);
        assertEquals(ints[1], -1);
        assertEquals(ints[2], 34);

        ObjModel.parseInts("42", ints);
        assertEquals(ints[0], 42);
        assertEquals(ints[1], -1);
        assertEquals(ints[2], -1);
    }
}