package entities;

import entities.CartItem;
import entities.UserPs2;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(Cart.class)
public class Cart_ { 

    public static volatile SingularAttribute<Cart, Double> totalPrice;
    public static volatile ListAttribute<Cart, CartItem> cartItemList;
    public static volatile SingularAttribute<Cart, Integer> cartId;
    public static volatile SingularAttribute<Cart, UserPs2> userId;

}