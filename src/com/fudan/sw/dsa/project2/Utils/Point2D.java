package com.fudan.sw.dsa.project2.Utils;

public abstract class Point2D {
    public abstract double getX();
    public abstract double getY();
    public abstract double distanceTo(Point2D point);
    public abstract double distanceTo(double x, double y);
}
