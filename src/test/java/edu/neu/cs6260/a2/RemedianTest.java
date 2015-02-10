package edu.neu.cs6260.a2;

import org.junit.Test;

import static org.junit.Assert.*;

public class RemedianTest {

    @Test
    public void testAdd() throws Exception {
        Remedian rm = new Remedian(5);
        for (int i=0; i<30; i++) {
            rm.add(i+1);
        }
        double t = rm.getRemedian();
        assertTrue(t == 13.0);
    }

    @Test
    public void testGetRemedian() throws Exception {

    }
}