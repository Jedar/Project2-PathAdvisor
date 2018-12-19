package com.fudan.sw.dsa.project2.Utils;

import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private QuadtreeNode root;
    private Rectangle domain;

    public Quadtree(Rectangle domain){
        this.domain = domain;
        root = null;
    }

    public void insert(Point2D point){
        QuadtreeNode node = new QuadtreeNode(point);

        if (root == null){
            root = node;
            root.setSpace(domain);
            return;
        }

        insert(root,node);
    }

    private void insert(QuadtreeNode cur, QuadtreeNode node){
        int dir = cur.getDirection(node.point);
        if (cur.child[dir] == null){
            cur.child[dir] = node;
            node.parent = cur;
            node.setSpace(new Rectangle(cur.space,cur.point,dir));
        }
        else{
            insert(cur.child[dir],node);
        }
    }

    public Point2D nearestPoint(double x, double y){
        Point2D point = null;
        double rad = getMinDistanceOf(x,y);

        double dist = Double.MAX_VALUE;
        Rectangle rectangle = new Rectangle(x - rad,x + rad,y - rad, y + rad);
        List<Point2D> res = queryRange(rectangle);
        for (Point2D i:res){
            double d = i.distanceTo(x,y);
            if (d < dist){
                dist = d;
                point = i;
            }
        }
        return point;
    }

    public List<Point2D> queryRange(Rectangle range){
        List<Point2D> res = new ArrayList<>();
        queryRange(range,root,res);
        return res;
    }

    private void queryRange(Rectangle range, QuadtreeNode node, List<Point2D> res){
        if (node == null){
            return;
        }
        if (range.contains(node.point)){
            res.add(node.point);
        }
        if (range.intersects(node.space)){
            for (QuadtreeNode i:node.child){
                queryRange(range,i,res);
            }
        }
    }

    private double getMinDistanceOf(double x, double y){
        QuadtreeNode cur = root;
        double dist = Double.MAX_VALUE;
        Point2D point = null;
        while (cur != null){
            int dir = cur.getDirection(x,y);
            double distance = cur.point.distanceTo(x,y);
            if (distance <= dist){
                dist = distance;
                point = cur.point;
            }
            cur = cur.child[dir];
        }
        double len = Math.abs(x - point.getX());
        double wid = Math.abs(y - point.getY());
        double rad = (len > wid)?len:wid;
        return rad;
    }

    public PointQueue nearNeighbors(double x,double y, int num){
        final double xStep = 0.043;
        final double yStep = 0.015;

        PointQueue queue = new PointQueue(x,y,num);
        double rad = getMinDistanceOf(x,y);

        int i = 1;
        while (!queue.isFull()){
            queue.getQueue().clear();
            double xi = rad + xStep * i;
            double yi = rad + yStep * i;
            Rectangle rectangle = new Rectangle(x-xi,x + xi,y - yi,y + yi);
            List<Point2D> res = queryRange(rectangle);
            for (Point2D iter:res){
                queue.add(iter);
            }
            i++;
        }

        return queue;
    }

    public static void main(String[] args) {

    }
}
