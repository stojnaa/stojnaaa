package entities;

import entities.OrderItem;
import entities.Transaction;
import entities.UserPs3;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:48")
@StaticMetamodel(Order1.class)
public class Order1_ { 

    public static volatile SingularAttribute<Order1, Date> createdAt;
    public static volatile SingularAttribute<Order1, Integer> deliveryCityId;
    public static volatile SingularAttribute<Order1, Integer> orderId;
    public static volatile SingularAttribute<Order1, Double> totalPrice;
    public static volatile SingularAttribute<Order1, String> deliveryAddress;
    public static volatile ListAttribute<Order1, OrderItem> orderItemList;
    public static volatile SingularAttribute<Order1, UserPs3> buyerUserId;
    public static volatile SingularAttribute<Order1, Transaction> transaction;

}