package entities;

import entities.Cart;
import entities.CartItemPK;
import entities.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(CartItem.class)
public class CartItem_ { 

    public static volatile SingularAttribute<CartItem, Item> item;
    public static volatile SingularAttribute<CartItem, Integer> quantity;
    public static volatile SingularAttribute<CartItem, Cart> cart;
    public static volatile SingularAttribute<CartItem, CartItemPK> cartItemPK;

}