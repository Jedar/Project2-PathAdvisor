package com.fudan.sw.dsa.project2.bean;

import java.io.Serializable;
import java.util.List;

public class ReturnValue implements Serializable
{
	Address startPoint;
	List<Address> subwayList;
	List<String> lineList;
	Address endPoint;
	double minutes;
	double startWalk;
	double endWalk;

    public double getStartWalk() {
        return startWalk;
    }

    public void setStartWalk(double startWalk) {
        this.startWalk = startWalk;
    }

    public double getEndWalk() {
        return endWalk;
    }

    public void setEndWalk(double endWalk) {
        this.endWalk = endWalk;
    }

    public Address getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Address startPoint) {
		this.startPoint = startPoint;
	}

	public List<Address> getSubwayList() {
		return subwayList;
	}

	public void setSubwayList(List<Address> subwayList) {
		this.subwayList = subwayList;
	}

	public Address getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Address endPoint) {
		this.endPoint = endPoint;
	}

	public double getMinutes() {
		return minutes;
	}

	public void setMinutes(double minutes) {
		this.minutes = minutes;
	}

    public List<String> getLineList() {
        return lineList;
    }

    public void setLineList(List<String> lineList) {
        this.lineList = lineList;
    }
}
