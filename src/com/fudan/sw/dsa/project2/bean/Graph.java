package com.fudan.sw.dsa.project2.bean;

import java.io.File;
import java.util.*;

import com.fudan.sw.dsa.project2.Utils.*;
import com.fudan.sw.dsa.project2.constant.FileGetter;
import jxl.*;

/**
 * for subway graph
 * @author zjiehang
 *
 */
public class Graph {
    private GridMap gridMap; //grid map which is used to find station by longitude and latitude
    private HashMap<String, Vertex> map;//hashmap with all station
    private int lineNumber;
    private final int NEAR_NUM = 5;//number of finding near stations
    private final double WALK_SPEED = 1.3888889;//meters per second

    public Graph(File file) throws Exception{
        map = new HashMap<>(500);
        gridMap = new GridMap(500);

        //initial subway map by file
        if (file == null || !file.exists()){
            throw new Exception("wrong file exception");
        }
        Workbook workbook = Workbook.getWorkbook(file);
        lineNumber = workbook.getNumberOfSheets();
        for (int i = 0; i < lineNumber; i++){
            initialSheet(workbook.getSheet(i));
        }

        //add edges to construct the loop way
        addLoop();

        //add all station to grid map
        for (Object add:map.entrySet()){
            Map.Entry entry = (Map.Entry)add;
            gridMap.add((Vertex)entry.getValue());
        }
    }

    private void initialSheet(Sheet sheet){
        //initialize station by line number
        String line = sheet.getName();
        int num = sheet.getColumns() - 3;//3 stand for 3 columns data
        Cell cell;

        //use number cell to read double value
        NumberCell numberCell;
        int rows = sheet.getRows();
        int row = num;

        //initialize station
        while (row < rows){
            cell = sheet.getCell(0,row);
            String name = getName(cell.getContents(),line);
            numberCell = (NumberCell)sheet.getCell(1,row);
            double longitude = numberCell.getValue();
            numberCell = (NumberCell)sheet.getCell(2,row);
            double latitude = numberCell.getValue();

            //if station exists, get next
            if (map.get(name) != null){
                row++;
                continue;
            }
            Vertex vertex = new Vertex(name,longitude,latitude);
            map.put(name, vertex);
            row++;
        }

        //some sheets have two types of line
        for (int i = 0; i < num; i++){
            Cell c;
            row = num;
            Vertex prev = map.get(sheet.getCell(0,row).getContents());
            Vertex cur;
            int prevTime = timeConverse(sheet.getCell(i + 3,row).getContents());
            int curTime;
            row++;
            //initialize edges between stations
            while (row < rows){
                cur = map.get(getName(sheet.getCell(0,row).getContents(),line));
                c = sheet.getCell(i + 3,row);
                curTime = timeConverse(c.getContents());
                if (curTime == -1){
                    row++;
                    continue;
                }
                //subway is double way
                Edge edge1 = new Edge(curTime - prevTime,prev,cur,line);
                Edge edge2 = new Edge(curTime - prevTime,cur,prev,line);
                cur.addPath(edge2);
                prev.addPath(edge1);
                prevTime = curTime;
                prev = cur;
                row++;
            }
        }
    }

    private String getName(String old, String line){
        //handle some special name or same name but not same station cases
        String res;
        switch (old){
            case "浦电路":
                res = old + line.charAt(line.length() - 1);
                break;
            default:
                res = old;
        }
        return res.trim();
    }

    private int timeConverse(String time){
        //converse string of time to integer of minutes
        int len = time.length();
        int minute = 0;
        int hour = 0;
        if (len < 5){
            return -1;
        }
        char ch = time.charAt(len - 1);
        minute += (ch - '0');
        ch = time.charAt(len - 2);
        minute += (ch - '0')*10;
        ch = time.charAt(len - 4);
        hour += (ch - '0');
        ch = time.charAt(len - 5);
        hour += (ch - '0')*10;
        if (hour == 0){
            hour = 24;
        }
        return hour*60+minute;
    }

    private void addLoop(){
        int len = 1;
        String[] startings = {"虹桥路"};
        String[] endings = {"宜山路"};
        String[] lines = {"Line 4"};
        int[] times = {3};
        for (int i = 0; i < len; i++){
            addEdge(startings[i],endings[i],times[i],lines[i]);
        }
    }

    private void addEdge(String s,String e,int time,String line){
        Vertex start = map.get(s);
        Vertex end = map.get(e);
        if (start == null || end == null){
            return;
        }
        Edge edge = new Edge(time,start,end,line);
        Edge edgeInverse = new Edge(time,end,start,line);
        //subway line is double way
        start.addPath(edge);
        end.addPath(edgeInverse);
    }

    public Vertex findNearest(Vertex point){
        return gridMap.nearestPoint(point.getX(),point.getY());
    }

    public Path findFewestTimePath(Vertex s,Vertex e){
        //get the near stations of start point and end point
        PointQueue<Vertex> nearStarts = gridMap.nearNeighbors(s.getX(),s.getY(),NEAR_NUM);
        PointQueue<Vertex> nearEnds = gridMap.nearNeighbors(e.getX(),e.getY(),NEAR_NUM);

        //a set of all possible path
        List<Path> paths = new ArrayList<>();

        //find all combination of near start point station and end
        for (int i = 0; i < NEAR_NUM; i++){
            Vertex nearStart = nearStarts.getQueue().get(i);
            double startDistance = nearStart.distanceTo(s);
            for (int j = 0; j < NEAR_NUM; j++){
                Path path = new Path(s,e);
                Vertex nearEnd = nearEnds.getQueue().get(j);
                double endDistance = nearEnd.distanceTo(e);
                findPath(path,nearStart,nearEnd);
                double walkTime = (startDistance + endDistance)/WALK_SPEED;
                walkTime /= 60;
                path.setMinutes(path.getMinutes() + walkTime);
                path.setStartWalk(startDistance);
                path.setEndWalk(endDistance);
                paths.add(path);
            }
        }

        //compare time cost to get the best one
        Path mainPath = paths.get(0);
        for (Path path:paths){
            if (mainPath.getMinutes() > path.getMinutes()){
                mainPath = path;
            }
        }
        return mainPath;
    }

    public Path findLeastTransferPath(Vertex s, Vertex e){
        //get near stations of start point and end point
        PointQueue<Vertex> nearStarts = gridMap.nearNeighbors(s.getX(),s.getY(),NEAR_NUM);
        PointQueue<Vertex> nearEnds = gridMap.nearNeighbors(e.getX(),e.getY(),NEAR_NUM);

        //possible result
        List<Path> paths = new ArrayList<>();

        //compute all combination
        for (int i = 0; i < NEAR_NUM; i++){
            Vertex nearStart = nearStarts.getQueue().get(i);
            double startDistance = nearStart.distanceTo(s);
            for (int j = 0; j < NEAR_NUM; j++){

                Vertex nearEnd = nearEnds.getQueue().get(j);
                double endDistance = nearEnd.distanceTo(e);
                double walkTime = (startDistance + endDistance)/WALK_SPEED;
                walkTime /= 60;
                Path path = findLeastTransferPathByStation(s,e,nearStart,nearEnd);
                paths.add(path);
                path.setStartWalk(startDistance);
                path.setEndWalk(endDistance);
                path.setMinutes(path.getMinutes() + walkTime);
            }
        }

        //compare transfer times to get the best one
        Path mainPath = paths.get(0);
        for (Path path:paths){
            if (mainPath.getTransferTimes() > path.getTransferTimes()){
                mainPath = path;
            }
        }
        return mainPath;
    }

    public Path findLeastTransferPathByStation(Vertex s, Vertex e, Vertex nearStart, Vertex nearEnd){
        Path mainPath = new Path(s,e);

        buildTransferMap(nearStart,nearEnd);

        //find main path
        Vertex cur = nearEnd;
        String curLine = cur.getPrev().getLine();//""=>this
        getPathFromMap(mainPath, cur, curLine);

        //find other paths
        Path[] paths = new Path[nearEnd.getPrevList().size()];
        for (int i = 0; i < paths.length;i++){
            paths[i] = new Path(s,e);
            cur = nearEnd;
            curLine = nearEnd.getPrevList().get(i).getLine();
            getPathFromMap(paths[i], cur, curLine);

            //if cost less time, improve main path
            if (mainPath.getMinutes() > paths[i].getMinutes()){
                mainPath = paths[i];
            }
        }

        mainPath.initialPath();
        return mainPath;
    }

    private void getPathFromMap(Path mainPath, Vertex cur, String curLine) {
        //back up to get the path
        while (true){
            Edge edge = cur.getPrev();

            //if exists a edge not need to transfer, turn edge
            for (Edge i:cur.getPrevList()){
                if (i.getLine().equals(curLine)){
                    edge = i;
                }
            }
            if (edge != null){
                mainPath.minutesAdd(edge.getTime());
                mainPath.getEdgeList().add(0,edge);
                cur = edge.getStart();
                if (!curLine.equals(edge.getLine())){
                    mainPath.transferTimesPlus();
                }
                curLine = edge.getLine();
            }
            else {
                break;
            }
        }
    }

    public void buildTransferMap(Vertex s, Vertex e){
        initializeMap();
        s.setWeight(0);

        //use two stack to store next line
        Stack<Edge> stack1 = new Stack<>();
        Stack<Edge> stack2 = new Stack<>();

        //stack use to traverse
        Stack<Edge> stack = stack1;

        //bag uses to store
        Stack<Edge> bag = stack2;
        int i = 0;

        //mark start point
        markLine(s,null,"",stack);

        //mark next line until all station marked
        while (!stack.empty()){
            while (!stack.isEmpty()){
                Edge edge = stack.pop();
                Vertex end = edge.getEnd();

                //if the station is not marked, mark it
                if (end.getWeight() == Integer.MAX_VALUE){
                    end.setWeight(i);
                    markLine(edge.getEnd(),edge.getStart(),edge.getLine(),bag);
                    end.setPrev(edge);
                }
            }

            //exchange stack and bag
            bag = stack;
            stack = (stack == stack1)?stack2:stack1;

            //plus weight
            i++;
        }
    }

    private void markLine(Vertex vertex, Vertex pre, String line,Stack<Edge> stack){
        for (Edge edge:vertex.getPaths()){
            //find edge with same line
            if (edge.getLine().equals(line)){
                Vertex end = edge.getEnd();

                //if not mark, then mark it
                if (end.getWeight() == Integer.MAX_VALUE) {
                    end.setWeight(vertex.getWeight());
                    end.setPrev(edge);
                    markLine(end,vertex,line,stack);
                }

                //special case that station is marked by other line
                //TODO: loop problem
                else if (end != pre){
                    boolean flag = true;
                    if(vertex.getPrev() != null && vertex.getPrev().getLine().equals(line)){
                        flag = false;
                    }
                    for (Edge edge1:vertex.getPrevList()){
                        if (edge1.getLine().equals(line)){
                            flag = false;
                        }
                    }
                    if (flag){
                        end.getPrevList().add(edge);
                        markLine(end,vertex,line,stack);
                    }
                }
            }
            else {
                stack.push(edge);
            }
        }
    }

    public Path findNearestStationPath(Vertex s, Vertex e){
        Path mainPath = new Path(s, e);

        //find nearest station of start point and end point
        Vertex nearStart = findNearest(s);
        Vertex nearEnd = findNearest(e);

        //get the path
        findPath(mainPath,nearStart,nearEnd);

        //add walk distance and walk time
        double startDistance = nearStart.distanceTo(s);
        double endDistance = nearEnd.distanceTo(e);
        double walkTime = (startDistance + endDistance)/WALK_SPEED;
        mainPath.setMinutes(mainPath.getMinutes() + walkTime/60);
        mainPath.setEndWalk(endDistance);
        mainPath.setStartWalk(startDistance);

        return mainPath;
    }

    private void findPath(Path mainPath,Vertex nearStart,Vertex nearEnd){

        //find path by backing up
        buildMap(nearStart,nearEnd);
        Vertex cur = nearEnd;
        String curLine = nearEnd.getPrev().getLine();
        while (cur != null){
            Edge edge = cur.getPrev();
            if (edge != null){
                mainPath.minutesAdd(edge.getTime());
                mainPath.getEdgeList().add(0,edge);
                cur = edge.getStart();
                if (!curLine.equals(edge.getLine())){
                    mainPath.transferTimesPlus();
                }
                curLine = edge.getLine();
            }
            else {
                cur = null;
            }
        }
        mainPath.initialPath();
    }

    private void buildMap(Vertex s, Vertex e){
        //build map by Dijkstra
        initializeMap();
        List<Vertex> finish = new ArrayList<>();
        List<Vertex> heap = new ArrayList<>();
        Vertex cur = s;
        s.setWeight(0);
        while (true){
            //traverse all edge
            int size = cur.getPaths().size();
            for (int i = 0; i < size; i++){
                Edge edge = cur.getPaths().get(i);
                Vertex end = edge.getEnd();
                if (finish.contains(end)){
                    continue;
                }
                if (end.getWeight() == Integer.MAX_VALUE){
                    heap.add(end);
                }

                //whether the path cost is less
                int weight = cur.getWeight() + edge.getTime();
                if (end.getWeight() > weight){
                    end.setWeight(weight);
                    end.setPrev(edge);
                }
            }
            buildMinHeap(heap);

            //already find a path to end point, break
            if (cur == e){
                break;
            }
            if (heap.size() == 0){
                break;
            }
            finish.add(cur);
            cur = extractMin(heap);
        }
    }

    private Vertex extractMin(List<Vertex> heap){
        int size = heap.size();
        if (size == 0){
            return null;
        }
        Vertex min = heap.get(0);
        Vertex last = heap.remove(size - 1);
        if (size == 1){
            return last;
        }
        heap.set(0,last);
        heapify(heap,1);
        return min;
    }

    private void buildMinHeap(List<Vertex> heap){
        for(int i = heap.size()/2+1;i >= 1; i--){
            heapify(heap,i);
        }
    }

    private void heapify(List<Vertex> heap, int index){
        int left = 2*index;
        int right = 2*index + 1;
        int least = index;
        if (left <= heap.size() && heap.get(left-1).getWeight() < heap.get(index-1).getWeight() ){
            least = left;
        }
        if (right <= heap.size() && heap.get(right-1).getWeight() < heap.get(index-1).getWeight() ){
            least = right;
        }
        if (least != index){
            Vertex ver1 = heap.get(index - 1);
            Vertex ver2 = heap.get(least - 1);
            heap.set(index-1,ver2);
            heap.set(least-1,ver1);
            heapify(heap,least);
        }
    }

    private void initializeMap(){
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            ((Vertex) entry.getValue()).fresh();
        }
    }

    public void printAddress(String name){
        Vertex vertex = map.get(name);
        if (vertex == null){
            System.out.println("not find the vertex");
            return;
        }
        System.out.println("name: "+ vertex.getAddress());
        System.out.println("longitude: "+ vertex.getLongitude());
        System.out.println("latitude: "+ vertex.getLatitude());
        System.out.print("Near station: ");
        for (Edge i: vertex.getPaths()){
            System.out.print(i.getEnd().getAddress()+" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        FileGetter getter = new FileGetter();
        try {
            Graph graph = new Graph(getter.readFileFromClasspath());
            graph.printAddress("虹桥路");
            Vertex near = (Vertex)graph.gridMap.nearestPoint(121.510384,31.190936);
            System.out.println("nearest station: "+near.getAddress());

//            long start = System.nanoTime();
            PointQueue<Vertex> queue = graph.gridMap.nearNeighbors(121.475494,31.22312,10);
//            long end = System.nanoTime();
//            System.out.println("time spend: "+(end - start));
            System.out.println("near neighbors: ");
            for (Vertex i:queue.getQueue()){
                System.out.println((i).getAddress());
            }
            System.out.println();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
