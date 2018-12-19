package com.fudan.sw.dsa.project2.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PointQueue<T extends Point2D> {
    private int num;
    private List<T> queue = new ArrayList<>();
    private double x;
    private double y;

    public PointQueue(double x,double y, int num){
        this.num = num;
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty(){
        return queue.size() == 0;
    }

    public boolean isFull(){
        return num==queue.size();
    }

    public void add(T point){
        insert(point);
    }

    private void insert(T point){
        if (isEmpty()){
            queue.add(point);
        }
        else {
            boolean flag = false;
            for (int i = 0; i < queue.size(); i++){
                Point2D p = queue.get(i);
                if (point.distanceTo(x,y) < p.distanceTo(x,y)){
                    queue.add(i,point);
                    flag = true;
                    break;
                }
            }
            if (!flag){
                queue.add(point);
            }
            if (queue.size() > num){
                queue.remove(queue.size() - 1);
            }
        }
    }

    public List<T> getQueue(){
        return queue;
    }
}
