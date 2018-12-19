package com.fudan.sw.dsa.project2.bean;

public class Edge {
    private int time;
    private Vertex start;
    private Vertex end;
    private String line;
    private String destination;

    public Edge(int time, Vertex s, Vertex e, String line){
        this.time = time;
        start = s;
        end = e;
        this.line = line;
    }

    public int getTime() {
        return time;
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public String getLine() {
        return line;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String dst){
        destination = dst;
    }
}