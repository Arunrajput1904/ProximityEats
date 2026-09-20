package com.arun.Restaurantbackend.Service;
import com.arun.Restaurantbackend.DTO.BundleDto;
import com.arun.Restaurantbackend.DTO.BundleResponse;
import com.arun.Restaurantbackend.DTO.StopDto;
import com.arun.Restaurantbackend.Entity.*;
import com.arun.Restaurantbackend.Exception.AccessDeniedException;
import com.arun.Restaurantbackend.Exception.BadRequestException;
import com.arun.Restaurantbackend.Exception.ConflictException;
import com.arun.Restaurantbackend.Exception.ResourceNoFoundException;
import com.arun.Restaurantbackend.Repository.*;
import com.arun.Restaurantbackend.Utilis.BundleStatus;
import com.arun.Restaurantbackend.Utilis.OrderEnum;
import com.arun.Restaurantbackend.Utilis.OrderType;
import com.arun.Restaurantbackend.Utilis.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundleService {
   private ModelMapper mapper;
    private final BundleRepo bundleRepo;
    private final DELIVERYASSIGNSERVICE deliveryassignservice;
   private final Validationhandler validationhandler;
    private final UserprofileRepo userprofileRepo;
    private final DeliveryBoyRepo deliveryBoyRepo;
    private final StopRepo stopRepo;
    private final SocietyNRepo societyNRepo;
    private final SocietyEdgeRepo societyEdgeRepo;
    private final OrderRepo orderRepo;

    public List<BundleResponse> findAllBundleItem() {
        List<Bundle>list=bundleRepo.findByStatus(BundleStatus.PREPARED);
        User user=validationhandler.finduser();
        Userprofile userprofile=userprofileRepo.findById(user.getId()).orElseThrow(()-> new ResourceNoFoundException("User address is not found"));
        return list.stream()
                .filter(x->{
                    if(deliveryassignservice.findkm(x.getRestaurant().getTown(),userprofile.getSocietyName())<=15){
                        return true;
                    }
                    else{
                        return false;
                    }

                })
                .map(x-> {
                    return BundleResponse.builder().bundleStatus(BundleStatus.PREPARED).restaurantName(x.getRestaurant().getName())
                            .RestaurantDistanceFromYou(deliveryassignservice.findkm(x.getRestaurant().getTown(),userprofile.getSocietyName()).longValue()).price(x.getPrice()).bundleId(x.getId()).build();

                })
                .toList();
    }


    @Transactional
    public BundleDto makethebundleaccept(Long bundleId) {
         User user=validationhandler.finduser();
        Bundle bundle=bundleRepo.findById(bundleId).orElseThrow(()-> new BadRequestException("Bundle id  is wrong"));
        List<Stop> byBundleId = stopRepo.findByBundleId(bundle.getId());
        if(!(bundle.getLastUpdateTime().isAfter(LocalDateTime.now().minusMinutes(2) ) && bundle
                .getStatus().equals(BundleStatus.PREPARED))){
            throw new AccessDeniedException("Invalid access to bundleorder");
        }

        bundle.setStatus(BundleStatus.OUT_OF_DELIVERY);
        bundle.getOrderBundles().stream().map(x-> x.getOrder()).forEach(x-> {
             x.setStatus(OrderEnum.OUT_FOR_DELIVERY);

        });
        BundleDto bundleDto=mapper.map(bundle,BundleDto.class);

        List<StopDto> list = byBundleId.stream().map((element) -> mapper.map(element, StopDto.class)).toList();
        bundleDto.setStopList(list);
        return bundleDto;
    }

    @Transactional
    public void cancelBundle(Long id) {

User user=validationhandler.finduser();
        CancelEntity cancelEntity=new CancelEntity();

        cancelEntity.setUserid(user.getId());
        cancelEntity.setOrderid(id);
        cancelEntity.setReason(RoleEnum.DELIVERY_BOY);

        Bundle bundle=bundleRepo.findByIdAndStatus(id,BundleStatus.OUT_OF_DELIVERY).orElseThrow(()-> new ConflictException("Order is not valid"));
        bundle.getOrderBundles().stream().map(x-> x.getOrder()).forEach(x-> {
            x.setStatus(OrderEnum.PROCESSING_REFUND);

        });
        bundle.setCancelEntity(cancelEntity);

        bundle.setStatus(BundleStatus.CANCELLED);

    }


    public void orderstatus(Long id, OrderEnum status) {

        User user=validationhandler.finduser();

        DeliveryBoy deliveryBoy=deliveryBoyRepo.findByUserid(user.getId()).orElseThrow(()-> new ConflictException("Not delivery boy"));
        Bundle bundle = bundleRepo.findByDeliveryBoyidAndStatus(deliveryBoy.getId(), BundleStatus.OUT_OF_DELIVERY).orElseThrow(() -> new ConflictException("No order afflicated to you"));

        bundle.getOrderBundles()
                .stream()
                .filter(x->x.getOrder().getId().equals(id))
                .forEach(x-> x.getOrder().setStatus(status));

    }

    @Transactional
    public BundleDto preparedBundle(Long id) {

        User finduser = validationhandler.finduser();
        Bundle bundle=bundleRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Bundle is Not Found"));

        if(!bundle.getRestaurant().getManagerProfile().getUser().getId().equals(finduser.getId())){
            throw new ConflictException("Invalid Bundle access");
        }

        if(!(bundle.getLastUpdateTime().isAfter(LocalDateTime.now().minusMinutes(10) ) && bundle
                .getStatus().equals(BundleStatus.PREPARING))){
            throw new AccessDeniedException("Invalid access to bundleorder");
        }

        bundle.setStatus(BundleStatus.PREPARED);
        bundle.setLastUpdateTime(LocalDateTime.now());
        bundle.getOrderBundles()
                .stream()
                .map(bundles-> bundles.getOrder())
                .forEach(x-> x.setStatus(OrderEnum.PREPARED));

        return mapper.map(bundle,BundleDto.class);

    }


    @Transactional
    public void canceloverallBundle(Long id) {

        User finduser = validationhandler.finduser();
        Bundle bundle=bundleRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Bundle is Not Found"));

        if(!bundle.getRestaurant().getManagerProfile().getUser().getId().equals(finduser.getId())){
            throw new ConflictException("Invalid Bundle access");
        }
      if(!bundle.getStatus().equals(BundleStatus.PREPARING)){
          throw new AccessDeniedException("Invalid access");
      }
        bundle.getOrderBundles()
                        .stream()
                                .map(x-> x.getOrder())
                                        .forEach(x->{
                                            CancelEntity cancel=new CancelEntity();
                                            cancel.setOrderid(x.getId());
                                            cancel.setReason(RoleEnum.DELIVERY_BOY);
                                            cancel.setUserid(finduser.getId());
                                            x.setStatus(OrderEnum.PROCESSING_REFUND);
                                            x.setCancel(cancel);
                                        });
        bundle.setStatus(BundleStatus.CANCELLED);

    }




                void createnewBundle(String zone, List<Order> value) {
                    Bundle bundle1=new Bundle();

                    bundle1.setZoneName(zone);
                    bundle1.setStatus(BundleStatus.BUILDING);
                    bundle1.setCreatedAt(LocalDateTime.now());

                    List<OrderBundle> list = value.stream()
                            .filter(x-> x.getCancel() == null)
                            .map(x -> OrderBundle.builder().bundle(bundle1).order(x).build())
                            .toList();

                    for (int i=0; i<list.size(); i++){
                        OrderBundle orderBundle=list.get(i);
                        if(bundle1.getItemCount()<5){
                            bundle1.getOrderBundles().add(orderBundle);
                bundle1.setItemCount(bundle1.getItemCount()+1);
            }
            else{
                if(bundle1.getOrderBundles().size()>0) {
                    if(bundle1.getOrderBundles().size()>0) {
                        String town = bundle1.getOrderBundles().getFirst().getOrder().getRestaurant().getTown();

                        bundleupdationOrOrderDynamicchange(bundle1,town);
                    }
                }
                List <Order>list1= new ArrayList<>();
                list1.addAll(i,value);
                createnewBundle(zone,list1);
                break;
            }
        }
        if(bundle1.getOrderBundles().size()<=4 && bundle1.getOrderBundles().size()>0) {
            bundleRepo.save(bundle1);
        }
        String town = bundle1.getOrderBundles().getFirst().getOrder().getRestaurant().getTown();

        if(bundle1.getOrderBundles().size()>0) {
            bundleupdationOrOrderDynamicchange(bundle1,town);
        }
    }



    void addtothebundle(Bundle bundle1, List<Order> value) {
        List<OrderBundle> list = value.stream()
                .filter(x-> x.getCancel() == null)
                .map(x -> OrderBundle.builder().bundle(bundle1).order(x).build())
                .toList();

        for (int i=0; i<list.size(); i++){
            OrderBundle orderBundle=list.get(i);
            if(bundle1.getItemCount()<5){
                bundle1.getOrderBundles().add(orderBundle);
                bundle1.setItemCount(bundle1.getItemCount()+1);
            }
            else{
                List <Order>list1= new ArrayList<>();
                list1.addAll(i,value);
                createnewBundle(bundle1.getZoneName(),list1);
                break;
            }
        }
        String town = bundle1.getOrderBundles().getFirst().getOrder().getRestaurant().getTown();

        if(bundle1.getOrderBundles().size()>0) {
            bundleupdationOrOrderDynamicchange(bundle1,town);
        }

    }

    void bundleupdationOrOrderDynamicchange(Bundle bundle1, String town) {
        List<Stop> getpath = getpath(bundle1.getOrderBundles(), town);
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0; i<getpath.size(); i++){
            Stop stop=getpath.get(i);
            stop.setBundleId(bundle1.getId());
            stringBuilder.append(getpath.get(i));
            if (i<getpath.size()-1) {
                stringBuilder.append("->");
            }
        }
        bundle1.setOptimalRoute(stringBuilder.toString());
        stopRepo.saveAll(getpath);
        int km=getpath.getLast().value;
        if(km<5){
            bundle1.setPrice(50.0);
        }
        else if(km<10){
            bundle1.setPrice(80.0);
        }
        else{
            bundle1.setPrice(100.0);
        }
        bundle1.setStatus(BundleStatus.PREPARING);
        List<Order> orderStream = bundle1.getOrderBundles()
                .stream()
                .map(x -> {
                    x.getOrder().setOrderType(OrderType.BUNDLE);
                    return x.getOrder();
                }).toList();
        orderRepo.saveAll(orderStream);
        bundle1.setLastUpdateTime(LocalDateTime.now());
        bundleRepo.save(bundle1);
    }

    private List<Stop> getpath(List<OrderBundle> orderBundles,String string) {

        int[][] ints = shortestDistance();

        Map<Integer,Integer>map=new HashMap<>();
        Map<String,Integer>stringMap=new HashMap<>();
        List<SocietyN>societyNList=societyNRepo.findAll();
        Optional<SocietyN> first = societyNList.stream()
                .filter(x -> x.getSocietyName().equals(string))
                .findFirst();
        int count=0;
        map.put(first.get().getId().intValue(),count);
        stringMap.put(string,count);
        count++;
        for(OrderBundle bundle :orderBundles){
            String societyName = bundle.getOrder().getAddress().getSocietyName();

            for (SocietyN societyN : societyNList){
                if(societyN.getSocietyName().toLowerCase().trim().equals(societyName.toLowerCase().trim())){
                    map.put(count,societyN.getId().intValue());
                    stringMap.put(societyName,count);
                    count++;
                    break;
                }
            }
        }

        int [][]mat=new int[count][count];

        for(int i=0; i<mat.length; i++){
            for (int j=0; j<mat.length; j++){
                mat[i][j]=ints[map.get(i)][map.get(j)];
            }
        }

        boolean []visited=new boolean[count];

        visited[0]=true;

        int [] path=new int[count];
        int i=0;
        int max=0;
        List<Stop>pairList=new ArrayList<>();
        pairList.add(new Stop(i,0));
        while (!visited[i]){
            visited[i]=true;

            int min=Integer.MAX_VALUE;
            int index=-1;
            for(int i1 = 0; i1<mat[i].length; i++){
                if(i==i1){
                    continue;
                }
                if(min>mat[i][i1]){

                    min=mat[i][i1];
                    index=i1;
                }

            }
            pairList.add(new Stop(index,min+pairList.getLast().value));
            i=index;
        }


        return pairList;




    }


    private int[][]  shortestDistance(){

        List<SocietyN>list = societyNRepo.findAll();

        int n=list.size()+1;

        List<SocietyEdge>edges=societyEdgeRepo.findAll();


        int [][]mat=new int[n][n];

        for(int i=0; i<mat.length; i++){
            Arrays.fill(mat[i],Integer.MAX_VALUE);
        }

        for(SocietyEdge edge : edges){
            long s=edge.getSociety1().getId();
            long p=edge.getSociety2().getId();
            int u=(int)s;
            int v=(int)p;
            mat[u][v]=edge.getDistance();
            mat[v][u]=edge.getDistance();
        }

        for(int k=1; k<=n; k++){
            for(int i=1; i<=n; i++ ){
                for(int j=1; j<=n; j++){
                    if(mat[i][k]==Integer.MAX_VALUE || mat[k][j]==Integer.MAX_VALUE ){
                        continue;
                    }

                    mat[i][j]=Math.min(mat[i][j],mat[i][k]+mat[k][j]);
                }
            }
        }



        return mat;
    }


    @Transactional
    public void deliveredBundle(Long id) {


        User finduser = validationhandler.finduser();
        Bundle bundle=bundleRepo.findById(id).orElseThrow(()-> new ResourceNoFoundException("Bundle is Not Found"));

        if(!bundle.getRestaurant().getManagerProfile().getUser().getId().equals(finduser.getId())){
            throw new ConflictException("Invalid Bundle access");
        }

        bundle.setStatus(BundleStatus.DELIVERED);
    }
}
