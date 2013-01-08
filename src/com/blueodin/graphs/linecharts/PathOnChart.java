package com.blueodin.graphs.linecharts;

import java.util.ArrayList;

import com.blueodin.graphs.charts.PointOnChart;

public class PathOnChart {

    PathAttributes attributes;

    ArrayList<PointOnChart> points;

    public PathOnChart(ArrayList<PointOnChart> points, PathAttributes pathAttributes) {

        this.attributes = pathAttributes;
        this.points = points;
    }
}