//package com.fudan.sw.dsa.project2.bean;
//
//import com.fudan.sw.dsa.project2.Utils.Point2D;
//import com.fudan.sw.dsa.project2.Utils.PointQueue;
//import com.fudan.sw.dsa.project2.Utils.Quadtree;
//import com.fudan.sw.dsa.project2.Utils.Rectangle;
//import com.fudan.sw.dsa.project2.constant.FileGetter;
//import jxl.Cell;
//import jxl.NumberCell;
//import jxl.Sheet;
//import jxl.Workbook;
//
//import java.io.File;
//import java.util.*;
//
//public class GraphByTree {
//    private Quadtree tree;
//    private HashMap<String, Vertex> map;
//    private int lineNumber;
//
//    public GraphByTree(File file) throws Exception{
//        map = new HashMap<>(500);
//        Rectangle rectangle = new Rectangle(-180.0,180.0,-90.0,90.0);
//        tree = new Quadtree(rectangle);
//
//        if (file == null || !file.exists()){
//            throw new Exception("wrong file exception");
//        }
//        Workbook workbook = Workbook.getWorkbook(file);
//        lineNumber = workbook.getNumberOfSheets();
//        for (int i = 0; i < lineNumber; i++){
//            initialSheet(workbook.getSheet(i));
//        }
//        addLoop();
//        for (Object add:map.entrySet()){
//            Map.Entry entry = (Map.Entry)add;
//            tree.insert((Vertex)entry.getValue());
//        }
//    }
//
//    private void initialSheet(Sheet sheet){
//        String line = sheet.getName();
//        int num = sheet.getColumns() - 3;//3 stand for 3 columns data
//        Cell cell;
//        NumberCell numberCell;
//        int rows = sheet.getRows();
//        int row = num;
//        while (row < rows){
//            cell = sheet.getCell(0,row);
//            String name = getName(cell.getContents(),line);
//            numberCell = (NumberCell)sheet.getCell(1,row);
//            double longitude = numberCell.getValue();
//            numberCell = (NumberCell)sheet.getCell(2,row);
//            double latitude = numberCell.getValue();
//            if (map.get(name) != null){
//                row++;
//                continue;
//            }
//            Vertex vertex = new Vertex(name,longitude,latitude);
//            map.put(name, vertex);
//            row++;
//        }
//
//        for (int i = 0; i < num; i++){
//            Cell c;
//            row = num;
//            Vertex prev = map.get(sheet.getCell(0,row).getContents());
//            Vertex cur;
//            int prevTime = timeConverse(sheet.getCell(i + 3,row).getContents());
//            int curTime;
//            row++;
//            while (row < rows){
//                cur = map.get(getName(sheet.getCell(0,row).getContents(),line));
//                c = sheet.getCell(i + 3,row);
//                curTime = timeConverse(c.getContents());
//                if (curTime == -1){
//                    row++;
//                    continue;
//                }
//                //subway is double way
//                Edge edge1 = new Edge(curTime - prevTime,prev,cur,line);
//                Edge edge2 = new Edge(curTime - prevTime,cur,prev,line);
//                cur.addPath(edge2);
//                prev.addPath(edge1);
//                prevTime = curTime;
//                prev = cur;
//                row++;
//            }
//        }
//    }
//
//    private String getName(String old, String line){
//        String res;
//        switch (old){
//            case "浦电路":
//                res = old + line.charAt(line.length() - 1);
//                break;
//            default:
//                res = old;
//        }
//        return res.trim();
//    }
//
//    private int timeConverse(String time){
//        int len = time.length();
//        int minute = 0;
//        int hour = 0;
//        if (len < 5){
//            return -1;
//        }
//        char ch = time.charAt(len - 1);
//        minute += (ch - '0');
//        ch = time.charAt(len - 2);
//        minute += (ch - '0')*10;
//        ch = time.charAt(len - 4);
//        hour += (ch - '0');
//        ch = time.charAt(len - 5);
//        hour += (ch - '0')*10;
//        if (hour == 0){
//            hour = 24;
//        }
//        return hour*60+minute;
//    }
//
//    private void addLoop(){
//        int len = 1;
//        String[] startings = {"虹桥路"};
//        String[] endings = {"宜山路"};
//        String[] lines = {"Line 4"};
//        int[] times = {3};
//        for (int i = 0; i < len; i++){
//            addEdge(startings[i],endings[i],times[i],lines[i]);
//        }
//    }
//
//    private void addEdge(String s,String e,int time,String line){
//        Vertex start = map.get(s);
//        Vertex end = map.get(e);
//        if (start == null || end == null){
//            return;
//        }
//        Edge edge = new Edge(time,start,end,line);
//        Edge edgeInverse = new Edge(time,end,start,line);
//        //subway line is double way
//        start.addPath(edge);
//        end.addPath(edgeInverse);
//    }
//
//    public Vertex findNearest(Vertex point){
//        return (Vertex)tree.nearestPoint(point.getX(),point.getY());
//    }
//
//    public Path findLeastTransferPath(Vertex s, Vertex e){
//        Path mainPath = new Path(s,e);
//        Vertex nearStart = findNearest(s);
//        Vertex nearEnd = findNearest(e);
//        buildTransferMap(nearStart,nearEnd);
//
//        Vertex cur = nearEnd;
//        String curLine = "";
//        getPathFromMap(mainPath, cur, curLine);
//        Path[] paths = new Path[nearEnd.getPrevList().size()];
//        for (int i = 0; i < paths.length;i++){
//            paths[i] = new Path(s,e);
//            cur = nearEnd;
//            curLine = nearEnd.getPrevList().get(i).getLine();
//            getPathFromMap(paths[i], cur, curLine);
//            if (mainPath.getMinutes() > paths[i].getMinutes()){
//                mainPath = paths[i];
//            }
//        }
//        mainPath.initialPath();
//        return mainPath;
//    }
//
//    private void getPathFromMap(Path mainPath, Vertex cur, String curLine) {
//        while (true){
//            Edge edge = cur.getPrev();
//            for (Edge i:cur.getPrevList()){
//                if (i.getLine().equals(curLine)){
//                    edge = i;
//                }
//            }
//            if (edge != null){
//                mainPath.minutesAdd(edge.getTime());
//                mainPath.getEdgeList().add(0,edge);
//                cur = edge.getStart();
//                curLine = edge.getLine();
//            }
//            else {
//                break;
//            }
//        }
//    }
//
//    public void buildTransferMap(Vertex s, Vertex e){
//        initializeMap();
//        s.setWeight(0);
//        Stack<Edge> stack1 = new Stack<>();
//        Stack<Edge> stack2 = new Stack<>();
//        Stack<Edge> stack = stack1;
//        Stack<Edge> bag = stack2;
//        int i = 0;
//        markLine(s,"",stack);
//        while (!stack.empty()){
//            while (!stack.isEmpty()){
//                Edge edge = stack.pop();
//                Vertex end = edge.getEnd();
//                if (end.getWeight() == Integer.MAX_VALUE){
//                    end.setWeight(i);
//                    markLine(edge.getEnd(),edge.getLine(),bag);
//                    end.setPrev(edge);
//                }
//            }
//            bag = stack;
//            stack = (stack == stack1)?stack2:stack1;
//            i++;
//        }
//    }
//
//    private void markLine(Vertex vertex,String line,Stack<Edge> stack){
//        for (Edge edge:vertex.getPaths()){
//            if (edge.getLine().equals(line)){
//                Vertex end = edge.getEnd();
//                if (end.getWeight() == Integer.MAX_VALUE) {
//                    end.setWeight(vertex.getWeight());
//                    end.setPrev(edge);
//                    markLine(end,line,stack);
//                }
//                else if (vertex.getPrev() != null && !vertex.getPrev().getLine().equals(line)){//vertex.getPrev().getStart() != edge.getEnd()
//                    boolean flag = true;
//                    for (Edge edge1:vertex.getPrevList()){
//                        if (edge1.getLine().equals(line)){
//                            flag = false;
//                        }
//                    }
//                    if (flag){
//                        end.getPrevList().add(edge);
//                        markLine(end,line,stack);
//                    }
//                }
//            }
//            else {
//                stack.push(edge);
//            }
//        }
//    }
//
//    public void buildTransferMap_old(Vertex s, Vertex e){
//        initializeMap();
//        s.setWeight(0);
//        List<Vertex> heap = new ArrayList<>();
//        List<Vertex> finish = new ArrayList<>();
//        heap.add(s);
//        Vertex cur;
//        while (heap.size() > 0){
//            cur = extractMin(heap);
//            String line = "";
//            Vertex prev = null;
//            if (cur.getPrev() != null){
//                line = cur.getPrev().getLine();
//                prev = cur.getPrev().getStart();
//            }
//            for (Edge edge:cur.getPaths()){
//                Vertex end = edge.getEnd();
//                if (end == prev){
//                    continue;
//                }
//                int weight = cur.getWeight();
//                if (!edge.getLine().equals(line)){
//                    weight++;
//                    for (Edge edge1:cur.getPrevList()){
//                        if (edge1.getLine().equals(edge.getLine())){
//                            weight--;
//                        }
//                    }
//                }
//                if (end.getWeight() == Integer.MAX_VALUE){
//                    end.setWeight(weight);
//                    heap.add(end);
//                    end.setPrev(edge);
//                }
//                else  if (end.getWeight() == weight){
//                    end.getPrevList().add(edge);
//                }
//                else if (end.getWeight() > weight){
//                    end.setWeight(weight);
//                    end.setPrev(edge);
//                    end.getPrevList().clear();
//                }
//            }
////            if (cur == e){
////                break;
////            }
//            finish.add(cur);
//            buildMinHeap(heap);
//        }
//    }
//
//    public Path findNearestStationPath(Vertex s, Vertex e){
//        Path mainPath = new Path(s, e);
//        Vertex nearStart = findNearest(s);
//        Vertex nearEnd = findNearest(e);
//        buildMap(nearStart,nearEnd);
//        Vertex cur = nearEnd;
//        while (cur != null){
//            Edge edge = cur.getPrev();
//            if (edge != null){
//                mainPath.minutesAdd(edge.getTime());
//                mainPath.getEdgeList().add(0,edge);
//                cur = edge.getStart();
//            }
//            else {
//                cur = null;
//            }
//        }
//        mainPath.initialPath();
//        return mainPath;
//    }
//
//    private void buildMap(Vertex s, Vertex e){
//        initializeMap();
//        List<Vertex> finish = new ArrayList<>();
//        List<Vertex> heap = new ArrayList<>();
//        Vertex cur = s;
//        s.setWeight(0);
//        while (true){
//            int size = cur.getPaths().size();
//            for (int i = 0; i < size; i++){
//                Edge edge = cur.getPaths().get(i);
//                Vertex end = edge.getEnd();
//                if (finish.contains(end)){
//                    continue;
//                }
//                if (end.getWeight() == Integer.MAX_VALUE){
//                    heap.add(end);
//                }
//                //TODO:multi choice
//                int weight = cur.getWeight() + edge.getTime();
//                if (end.getWeight() > weight){
//                    end.setWeight(weight);
//                    end.setPrev(edge);
//                }
//            }
//            buildMinHeap(heap);
//            if (cur == e){
//                break;
//            }
//            if (heap.size() == 0){
//                break;
//            }
//            finish.add(cur);
//            cur = extractMin(heap);
//        }
//    }
//
//    private Vertex extractMin(List<Vertex> heap){
//        int size = heap.size();
//        if (size == 0){
//            return null;
//        }
//        Vertex min = heap.get(0);
//        Vertex last = heap.remove(size - 1);
//        if (size == 1){
//            return last;
//        }
//        heap.set(0,last);
//        heapify(heap,1);
//        return min;
//    }
//
//    private void buildMinHeap(List<Vertex> heap){
//        for(int i = heap.size()/2+1;i >= 1; i--){
//            heapify(heap,i);
//        }
//    }
//
//    private void heapify(List<Vertex> heap, int index){
//        int left = 2*index;
//        int right = 2*index + 1;
//        int least = index;
//        if (left <= heap.size() && heap.get(left-1).getWeight() < heap.get(index-1).getWeight() ){
//            least = left;
//        }
//        if (right <= heap.size() && heap.get(right-1).getWeight() < heap.get(index-1).getWeight() ){
//            least = right;
//        }
//        if (least != index){
//            Vertex ver1 = heap.get(index - 1);
//            Vertex ver2 = heap.get(least - 1);
//            heap.set(index-1,ver2);
//            heap.set(least-1,ver1);
//            heapify(heap,least);
//        }
//    }
//
//    private void initializeMap(){
//        for (Object o : map.entrySet()) {
//            Map.Entry entry = (Map.Entry) o;
//            ((Vertex) entry.getValue()).fresh();
//        }
//    }
//
//    public void printAddress(String name){
//        Vertex vertex = map.get(name);
//        if (vertex == null){
//            System.out.println("not find the vertex");
//            return;
//        }
//        System.out.println("name: "+ vertex.getAddress());
//        System.out.println("longitude: "+ vertex.getLongitude());
//        System.out.println("latitude: "+ vertex.getLatitude());
//        System.out.print("Near station: ");
//        for (Edge i: vertex.getPaths()){
//            System.out.print(i.getEnd().getAddress()+" ");
//        }
//        System.out.println();
//    }
//
//    public static void main(String[] args) {
//        FileGetter getter = new FileGetter();
//        try {
//            GraphByTree graph = new GraphByTree(getter.readFileFromClasspath());
//            graph.printAddress("虹桥路");
//            Vertex near = (Vertex)graph.tree.nearestPoint(121.510384,31.190936);
//            System.out.println("nearest station: "+near.getAddress());
//            Rectangle rectangle = new Rectangle(121.552461,121.577038,31.195168,31.222101);
//
//            List<Point2D> points = graph.tree.queryRange(rectangle);
//            System.out.println("\nquery search: ");
//            for (Point2D i:points){
//                Vertex vertex = (Vertex)i;
//                System.out.println(vertex.getAddress());
//            }
//
//            long start = System.nanoTime();
//            PointQueue<Vertex> queue = graph.tree.nearNeighbors(121.475494,31.22312,10);
//            long end = System.nanoTime();
//            System.out.println("time spend: "+(end - start));
//
//            System.out.println("near neighbors: ");
//            for (Vertex i:queue.getQueue()){
//                System.out.println((i).getAddress());
//            }
//            System.out.println();
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
