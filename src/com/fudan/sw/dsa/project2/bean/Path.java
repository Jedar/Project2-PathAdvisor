package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;
import java.util.List;

public class Path
{
	private Vertex startPoint;
	private List<Edge> edgeList = new ArrayList<>();
	private Vertex endPoint;
	private double minutes;
	private int transferTimes = 0;
	private List<Vertex> subwayList = new ArrayList<>();
	private List<String> lineList = new ArrayList<>();
	private double startWalk = 0;
	private double endWalk = 0;

	public Path(Vertex s, Vertex e){
	    startPoint = s;
	    endPoint = e;
	    minutes = 0;
    }

    public void setStartWalk(double startWalk) {
        this.startWalk = startWalk;
    }

    public void setEndWalk(double endWalk) {
        this.endWalk = endWalk;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public Vertex getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vertex startPoint) {
		this.startPoint = startPoint;
	}

	public Vertex getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Vertex endPoint) {
		this.endPoint = endPoint;
	}

	public double getMinutes() {
		return minutes;
	}

	public void setMinutes(double minutes) {
		this.minutes = minutes;
	}

    public void minutesAdd(double time) {
        minutes += time;
    }

    public void transferTimesPlus(){
		transferTimes++;
	}

	public int getTransferTimes() {
		return transferTimes;
	}

	public void initialPath(){
	    subwayList.add(edgeList.get(0).getStart());
	    for (Edge edge:edgeList){
	    	Vertex vertex = edge.getEnd();
	    	if (vertex.getPrev() != null){
	    		lineList.add(vertex.getPrev().getLine());
			}
	        subwayList.add(edge.getEnd());
        }
    }

    public ReturnValue getReturnValue(){
	    ReturnValue res = new ReturnValue();
	    List<Address> addressList = new ArrayList<>();
	    for (Vertex vertex:subwayList){
	        addressList.add(vertex.getData());
        }
	    res.setSubwayList(addressList);
	    res.setMinutes(minutes);
	    res.setStartWalk(startWalk);
	    res.setEndWalk(endWalk);
	    res.setLineList(lineList);
	    return res;
    }
}
