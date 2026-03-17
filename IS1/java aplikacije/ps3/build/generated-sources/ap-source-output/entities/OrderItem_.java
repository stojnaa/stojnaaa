package entities;

import entities.Order1;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:48")
@StaticMetamodel(OrderItem.class)
public class OrderItem_ { 

    public static volatile SingularAttribute<OrderItem, Double> unitPrice;
    public static volatile SingularAttribute<OrderItem, Integer> itemId;
    public static volatile SingularAttribute<OrderItem, Integer> quantity;
    public static volatile SingularAttribute<OrderItem, Order1> orderId;
    public static volatile SingularAttribute<OrderItem, Integer> orderItemId;

}