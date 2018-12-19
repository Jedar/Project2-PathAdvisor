package com.fudan.sw.dsa.project2.bean;

import com.fudan.sw.dsa.project2.Utils.PointQueue;

import java.util.ArrayList;
import java.util.HashMap;

public class GridMap {
    private HashMap<Integer, ArrayList<Vertex>> map;
    private static final int GRID_SIZE = 100;
    private static final int STEP = 10000;

    public GridMap(int capacity){
        map = new HashMap<>(capacity);
    }

    public void add(Vertex vertex){
        ArrayList<Vertex> vertices;
        int key = getKey(vertex.getX(),vertex.getY());
        if (map.get(key) == null){
            vertices = new ArrayList<>();
            map.put(key,vertices);
        }
        else {
            vertices = map.get(key);
        }
        vertices.add(vertex);
    }

    public PointQueue<Vertex> nearNeighbors(double x,double y,int num){
        PointQueue<Vertex> queue = new PointQueue<>(x,y,num);
        int xk = getIndex(x);
        int yk = getIndex(y);
        int size = (int)(Math.log(num)/Math.log(2)) + 1;
        searchGridPoint(map.get(xk*STEP + yk),queue);
        for (int i = 1; i <= size || !queue.isFull(); i++){
            int j;
            int k = yk - i;
            for (j = xk - i; j < xk + i; j++){
                searchGridPoint(map.get(j*STEP + k),queue);
            }
            for (;k < yk + i; k++){
                searchGridPoint(map.get(j*STEP + k),queue);
            }
            for (;j > xk - i; j--){
                searchGridPoint(map.get(j*STEP + k),queue);
            }
            for (;k > yk - i; k--){
                searchGridPoint(map.get(j*STEP + k),queue);
            }
        }
        return queue;
    }

    public Vertex nearestPoint(double x,double y){
        PointQueue<Vertex> queue = nearNeighbors(x,y,3);
        return queue.getQueue().get(0);
    }

    private void searchGridPoint(ArrayList<Vertex> vertices,PointQueue<Vertex> queue){
        if (vertices == null){
            return;
        }
        for (Vertex vertex:vertices){
            queue.add(vertex);
        }
    }

    private int getKey(double x, double y){
        int key = 0;
        int xi = (int)(x*GRID_SIZE);
        int yi = (int)(y*GRID_SIZE);
        key = xi * STEP + yi;
        return key;
    }

    private int getIndex(double idx){
        return (int)(idx*GRID_SIZE);
    }


}
