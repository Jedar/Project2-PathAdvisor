package com.fudan.sw.dsa.project2.bean;

import com.fudan.sw.dsa.project2.Utils.Point2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * For each station of subway
 * If you need other attribute, add it
 * @author zjiehang
 *
 */
public class Vertex extends Point2D
{
	public static final double EARTH_RADIUS = 6378137.0;
	private Address data;
	private String address;
	private double longitude = 0.0;//经度
	private double latitude = 0.0;//纬度
	private List<Edge> paths;

	private int weight;
	private Edge prev;
	private List<Edge> prevList;

	public Vertex(String address, double longitude, double latitude)
	{
		data = new Address(address, longitude, latitude);
		paths = new ArrayList<>();
		prevList = new ArrayList<>();
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Vertex(String address, String longitude, String latitude)
	{
		paths = new ArrayList<>();
		prevList = new ArrayList<>();
		this.address = address;
		this.latitude = Double.parseDouble(latitude);
		this.longitude = Double.parseDouble(longitude);
		data = new Address(address, this.longitude, this.latitude);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public List<Edge> getPaths() {
		return paths;
	}

	public void addPath(Edge e){
		for (Edge i:paths){
			if (i.getEnd() == e.getEnd() && i.getLine().equals(e.getLine())){
				return;
			}
		}
		paths.add(e);
	}

	public void fresh(){
		weight = Integer.MAX_VALUE;
		prev = null;
		prevList.clear();
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Edge getPrev() {
		return prev;
	}

	public void setPrev(Edge prev) {
		this.prev = prev;
	}

	@Override
	public double getX() {
		return longitude;
	}

	@Override
	public double getY() {
		return latitude;
	}

	@Override
	public double distanceTo(Point2D point) {
		return distanceTo(point.getX(),point.getY());
	}

	@Override
	public double distanceTo(double x, double y) {
		double rad1 = Math.toRadians(this.latitude);
		double rad2 = Math.toRadians(y);

		double a = rad1 - rad2;
		double b = Math.toRadians(this.longitude) - Math.toRadians(x);

		double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(rad1)*Math.cos(rad2)*Math.pow(Math.sin(b/2),2)));
		s = s*EARTH_RADIUS;
		s = Math.round(s*10000)/10000.0;
		return s;
	}

	public Address getData() {
		return data;
	}

	public List<Edge> getPrevList() {
		return prevList;
	}

}
