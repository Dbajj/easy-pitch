package com.adiga.easypitch.utils;

/**
 * Created by dbajj on 2018-02-12.
 */

public class GraphCoordinatePool extends ObjectPool<GraphCoordinate> {
    public GraphCoordinatePool(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    protected GraphCoordinate create() {
        return new GraphCoordinate();
    }

}
