package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    Map<String,Order> orderObj = new HashMap<>();  // "orderId" : Order
    Map<String,Integer> orders = new HashMap<>(); // "orderId" : 0 , 0 - not Assigned , 1 - Assigned
    Map<String,DeliveryPartner> partners = new HashMap<>(); // "partnerId" : DeliveryPartner
    Map<String,List<String>> PartnerOrders = new HashMap<>(); // "partnerId" : <orderId1,orderId2>

    public void addOrder(Order order) {
        orderObj.put(order.getId(),order);
        orders.put(order.getId(),0);
    }

    public void addPartner(DeliveryPartner deliveryPartner) {
        partners.put(deliveryPartner.getId(),deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {

        Order o = orderObj.getOrDefault(orderId,null);
        if(o == null) {
            throw new RuntimeException("OrderId is not present in db");
        }
        partners.get(partnerId).setNumberOfOrders(partners.get(partnerId).getNumberOfOrders()+1);

        List<String> temp = PartnerOrders.getOrDefault(partnerId,new ArrayList<>());
        temp.add(orderId);
        orders.put(orderId,1);
        PartnerOrders.put(partnerId,temp);
    }

    public Order getOrderById(String orderId) {
        if(orderObj.containsKey(orderId)) return orderObj.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(partners.containsKey(partnerId)) return partners.get(partnerId);
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if(PartnerOrders.containsKey(partnerId)) return PartnerOrders.get(partnerId).size();
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if(PartnerOrders.containsKey(partnerId)) return PartnerOrders.get(partnerId);
        return null;
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderObj.keySet());
    }

    public int getCountOfUnassignedOrders() {
        int count = 0;
        for (String s : orders.keySet()) {
            if(orders.get(s) == 0) count++;
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String time = "0";
        List<String> currOrders = PartnerOrders.get(partnerId);
        for(String str : currOrders) {
            String nTIme = orderObj.get(str).getDeliveryTime();
            if(Integer.parseInt(nTIme) > Integer.parseInt(time)) time = nTIme;
        }
        return time;
    }

    public void deletePartnerById(String partnerId) {
        List<String> currOrders = PartnerOrders.get(partnerId);
        for(String str : currOrders) {
            orders.put(str,0);
        }
        PartnerOrders.remove(partnerId);
        partners.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderObj.remove(orderId);
        orders.remove(orderId);
        for(String str : PartnerOrders.keySet()) {
            List<String> currOrders = PartnerOrders.get(str);
            int idx = 0;
            for(String s : currOrders) {

                if(s.equals(orderId)) {
                    currOrders.remove(idx);
                    PartnerOrders.put(str,currOrders);
                    return;
                }
                idx++;
            }

        }
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int count = 0;
        for(String str : PartnerOrders.get(partnerId)) {
            int currTime = Integer.parseInt(orderObj.get(str).getDeliveryTime());
            if(currTime > Integer.parseInt(time)) count++;
        }
        return count;
    }
}
