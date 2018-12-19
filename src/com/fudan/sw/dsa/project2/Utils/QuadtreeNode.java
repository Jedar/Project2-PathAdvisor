package com.fudan.sw.dsa.project2.Utils;

public class QuadtreeNode {
    QuadtreeNode parent;
    Point2D point;
    QuadtreeNode[] child = new QuadtreeNode[4];
    Rectangle space;

    public QuadtreeNode(Point2D point){
        this.point = point;
        for(QuadtreeNode i:child){
            i = null;
        }
        parent = null;
    }

    public int getDirection(Point2D p){
        Boolean up = p.getY() > point.getY();
        Boolean left = p.getX() >= point.getX();
        if (up && left){
            return 0;
        }
        if (up){
            return 1;
        }
        if (!left){
            return 2;
        }
        return 3;
    }

    public int getDirection(double x, double y){
        Boolean up = y > point.getY();
        Boolean left = x >= point.getX();
        if (up && left){
            return 0;
        }
        if (up){
            return 1;
        }
        if (!left){
            return 2;
        }
        return 3;
    }

    public void setSpace(Rectangle r){
        space = r;
    }
}
