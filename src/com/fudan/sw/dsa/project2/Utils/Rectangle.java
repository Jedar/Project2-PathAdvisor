package com.fudan.sw.dsa.project2.Utils;

public class Rectangle {
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;

    public Rectangle(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public Rectangle(Rectangle rectangle){
        xMax = rectangle.xMax;
        xMin = rectangle.xMin;
        yMax = rectangle.yMax;
        yMin = rectangle.yMin;
    }

    public Rectangle(Point2D leftLow, Point2D rightTop){
        xMin = leftLow.getX();
        yMin = leftLow.getY();
        xMax = rightTop.getX();
        yMax = rightTop.getY();
    }

    public Rectangle(Rectangle r,Point2D p,int direction){
        switch (direction){
            case 0:
                xMin = p.getX();
                yMin = p.getY();
                xMax = r.xMax;
                yMax = r.yMax;
                break;
            case 1:
                yMin = p.getY();
                xMax = p.getX();
                xMin = r.getxMin();
                yMax = r.getyMax();
                break;
            case 2:
                xMax = p.getX();
                yMax = p.getY();
                xMin = r.xMin;
                yMin = r.yMin;
                break;
            case 3:
                xMin = p.getX();
                yMax = p.getY();
                xMax = r.xMax;
                yMin = r.yMin;
                break;
            default:

        }
    }

    public Boolean contains(Point2D point){
        if (point.getX() > xMax || point.getX() < xMin){
            return false;
        }
        if (point.getY() > yMax || point.getY() < yMin){
            return false;
        }
        return true;
    }

    public Boolean intersects(Rectangle rectangle){
        if (rectangle.getxMax() < this.xMin || rectangle.getxMin() > this.xMax){
            return false;
        }
        if (rectangle.getyMax() < this.yMin || rectangle.getyMin() > this.yMax){
            return false;
        }
        return true;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }
}
