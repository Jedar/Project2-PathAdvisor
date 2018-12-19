package com.fudan.sw.dsa.project2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fudan.sw.dsa.project2.bean.Vertex;
import org.springframework.stereotype.Service;

import com.fudan.sw.dsa.project2.bean.Graph;
import com.fudan.sw.dsa.project2.bean.Path;
import com.fudan.sw.dsa.project2.constant.FileGetter;
import com.fudan.sw.dsa.project2.bean.ReturnValue;

/**
 * this class is what you need to complete
 * @author zjiehang
 *
 */
@Service
public class IndexService 
{
	//the subway graph
	private Graph graph = null;
	
	/**
	 * create the graph use file
	 */
	public void createGraphFromFile()
	{
		//如果图未初始化
		if(graph==null)
		{
			FileGetter fileGetter= new FileGetter();
			try
			{
				//create the graph from file
				graph = new Graph(fileGetter.readFileFromClasspath());
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public ReturnValue travelRoute(Map<String, Object>params)
	{
		String startAddress = 	params.get("startAddress").toString();	
		String startLongitude = params.get("startLongitude").toString();
		String startLatitude = params.get("startLatitude").toString();
		String endAddress = params.get("endAddress").toString();
		String endLongitude = params.get("endLongitude").toString();
		String endLatitude = params.get("endLatitude").toString();
		String choose = params.get("choose").toString();
		
		System.out.println(startAddress);
		System.out.println(startLongitude);
		System.out.println(startLatitude);
		System.out.println(endAddress);
		System.out.println(endLongitude);
		System.out.println(endLatitude);
		System.out.println(choose);

		//TODO:is start point and end point add to return value?
		Vertex startPoint = new Vertex(startAddress, startLongitude, startLatitude);
		Vertex endPoint = new Vertex(endAddress, endLongitude, endLatitude);
		List<Vertex> vertices =new ArrayList<Vertex>();
		ReturnValue returnValue = null;
		Path path = null;
		switch (choose)
		{
		case "1":
			//步行最少
			//举个例子
			path = graph.findNearestStationPath(startPoint,endPoint);
			returnValue = path.getReturnValue();
//			System.out.println(returnValue.getStartPoint().getAddress());
//			vertices.add(new Vertex("张江高科", "121.593923", "31.207892"));
//			vertices.add(new Vertex("龙阳路", "121.564028", "31.209714"));
//			vertices.add(new Vertex("世纪公园", "121.557164", "31.215891"));
//			vertices.add(new Vertex("上海科技馆", "121.550589", "31.225433"));
//			vertices.add(new Vertex("世纪大道", "121.533449", "31.23482"));
//			vertices.add(new Vertex("东昌路", "121.5220233", "31.23905"));
//			vertices.add(new Vertex("陆家嘴", "121.508836", "31.243558"));
//			vertices.add(new Vertex("南京东路", "121.490331", "31.242817"));
//			vertices.add(new Vertex("人民广场", "121.479371", "31.238803"));
			break;
		case "2":
			//换乘最少
			path = graph.findLeastTransferPath(startPoint,endPoint);
			returnValue = path.getReturnValue();
			break;
		case "3":
			//时间最短:
			path = graph.findFewestTimePath(startPoint,endPoint);
			returnValue = path.getReturnValue();
			break;
		default:
			break;
		}

		if (returnValue != null) {
			returnValue.setStartPoint(startPoint.getData());
			returnValue.setEndPoint(endPoint.getData());
		}

		return returnValue;
	}
}
