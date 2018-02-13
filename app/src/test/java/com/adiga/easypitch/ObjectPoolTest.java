package com.adiga.easypitch;

import android.support.v4.util.SparseArrayCompat;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.GraphCoordinatePool;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;


/**
 * Created by dbajj on 2018-02-12.
 */

public class ObjectPoolTest {

    GraphCoordinatePool testPool;

    @Before
    public void setup() {
        testPool = new GraphCoordinatePool(10);
    }

    @Test
    public void testPoolReturn() {
        ArrayList<GraphCoordinate> list = new ArrayList<GraphCoordinate>();
        for(int i = 0; i < 10; i++) {
            list.add(testPool.acquire());
        }

        assertEquals(10,testPool.spaceUsed());
        assertEquals(0,testPool.spaceRemaining());

        int removed = 0;

        while(!list.isEmpty()) {
            GraphCoordinate g = list.get(0);

            list.remove(0);

            testPool.release(g);

            assertEquals(10-++removed,testPool.spaceUsed());
        }

        assertEquals(0,testPool.spaceUsed());
        assertEquals(10,testPool.spaceRemaining());

    }

    @Test
    public void testSparseArrayRemove() {
        SparseArrayCompat array = new SparseArrayCompat(10);

        array.append(0,"0");
        array.append(1,"1");
        array.append(2,"2");

        assertEquals("0",array.get(0));
        assertEquals("1",array.get(1));
        assertEquals("2",array.get(2));

        int index = array.indexOfValue("0");

        array.removeAt(index);

        assertEquals("1",array.get(1));
        assertEquals("2",array.get(2));

    }
}
