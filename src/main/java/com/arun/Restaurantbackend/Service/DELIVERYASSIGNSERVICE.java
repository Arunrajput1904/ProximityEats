package com.arun.Restaurantbackend.Service;

import com.arun.Restaurantbackend.DTO.Estimatetimekm;
import com.arun.Restaurantbackend.Entity.SocietyEdge;
import com.arun.Restaurantbackend.Entity.SocietyN;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.SocietyEdgeRepo;
import com.arun.Restaurantbackend.Repository.SocietyNRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

class Edge {
    int to;
    int weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "to=" + to +
                ", weight=" + weight +
                '}';
    }
}


class pair{

    int parent;

    Edge edge;

    public pair(int parent, Edge edge) {
        this.parent = parent;
        this.edge = edge;
    }
}

@Service
@RequiredArgsConstructor
public class DELIVERYASSIGNSERVICE {
    private final SocietyNRepo societyNRepo;
private final SocietyEdgeRepo societyEdgeRepo;
 final Double maxKm=15.0;
private final int averageSecPerKm=85;
private final double moderate=1.2;
private final double normal=1.0;
private final double extreme=1.5;
private StringBuilder stringBuilder=new StringBuilder();
private int[] mainparented=new int[100+1];
private  List<SocietyEdge>edgeslist1;
    Map<Long, Integer> indexMap = new HashMap<>();
    Map<Integer,String>stringIntegerMap=new HashMap<>();
public Double findkm(String city1, String city2){
        SocietyN cityfirst=societyNRepo.findBySocietyName(city1).orElseThrow(()-> new ResourceNoFoundException(" Society not found"));
        SocietyN citylast=societyNRepo.findBySocietyName(city2).orElseThrow(()-> new ResourceNoFoundException(" Society not found"));
        System.out.println(" ...............................+"+cityfirst+citylast);


        int vertex, edges;
        List<SocietyN>citylist =societyNRepo.findAll();
        List<SocietyEdge>edgeslist=societyEdgeRepo.findAllSocietyEdge();

        edges=edgeslist.size();
vertex=citylist.size();


     for(int i=0; i<citylist.size(); i++){
         Long id=citylist.get(i).getId();
         String cityname=citylist.get(i).getSocietyName();
        indexMap.put(id,i);
        stringIntegerMap.put(i,cityname);
     }


        double[] dist = new double[vertex + 1];
        boolean[] visited = new boolean[vertex + 1];
        int[] parented=new int[vertex+1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parented, -1);


        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i <= vertex; i++) {
            graph.add(new ArrayList<>());
        }

        int u,v;
        int w;
        for (int i = 0; i < edges; i++) {
            u = indexMap.get(edgeslist.get(i).getSociety1().getId());
            v = indexMap.get(edgeslist.get(i).getSociety2().getId());
            w =   edgeslist.get(i).getDistance();

            graph.get(u).add(new Edge(v, w));
            graph.get(v).add(new Edge(u, w));
        }
        PriorityQueue<pair> pq =
                new PriorityQueue<>((a,b)-> a.edge.weight-b.edge.weight);
        int src= indexMap.get(cityfirst.getId());
        pq.add(new pair(src,new Edge(src,0)));
        dist[src]=0;
        parented[src]=-1;
        while(!(pq.isEmpty())){

            int parent=pq.peek().parent;
            int child=pq.peek().edge.to;

            int weight=pq.peek().edge.weight;
            pq.poll();

            if(visited[child]){
                continue;
            }
            visited[child]=true;
            for(int i=0; i<graph.get(child).size(); i++){

                int node=graph.get(child).get(i).to;
                int  weighted=graph.get(child).get(i).weight;
                if(!visited[node]){
                    if(weighted+weight<dist[node]){
                        dist[node]=weighted+weight;
                        parented[node]=child;
                    }
                    pq.add(new pair(child, new Edge(node, weight + weighted)));
                }
            }
        }


mainparented=parented;
        edgeslist1=edgeslist;
        int destination=indexMap.get(citylast.getId());
        return dist[destination];

    }


//public StringBuilder assignpathinorder()


    public Estimatetimekm findestimatedistanceandtime(String city1, String city2){
        Double km=findkm(city1,city2);

        SocietyN cityfirst=societyNRepo.findBySocietyName(city1).orElseThrow(()->
                new ResourceNoFoundException(" city not found"));


        SocietyN citylast=societyNRepo.findBySocietyName(city2).orElseThrow(()-> new ResourceNoFoundException(" city not found"));

int src= indexMap.get(cityfirst.getId());
int dest= indexMap.get(citylast.getId());
stringBuilder.delete(0,stringBuilder.length());
System.out.println(src+"  " +dest);
System.out.println(Arrays.toString(mainparented));

while (mainparented[dest]!=-1){
stringBuilder.insert(0,"->"+stringIntegerMap.get(mainparented[dest]));
    dest=mainparented[dest];
}
stringBuilder.append("-> "+citylast.getSocietyName());
//Arrays.fill(mainparented);

        Long extremetime= (long) (km*averageSecPerKm)/60;
        extremetime+=(5*averageSecPerKm)/60;
        extremetime= (long) (extremetime*moderate);

System.out.println("..................."+stringBuilder);
        return new Estimatetimekm(km, LocalDateTime.now().plusMinutes(extremetime),stringBuilder);

    }


public boolean  validkm(String city1,String city2){

        Estimatetimekm estimatetimekm=findestimatedistanceandtime(city1,city2);

        if(estimatetimekm.getEstkm()>maxKm){

            return false;

        }

        return true;

}
    public Long findminutes(Double km){
       return (long) (((km*averageSecPerKm)/60)*moderate);
    }








}
