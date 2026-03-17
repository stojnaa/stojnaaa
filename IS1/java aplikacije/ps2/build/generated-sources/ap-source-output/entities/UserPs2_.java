package entities;

import entities.Cart;
import entities.Item;
import entities.Wishlist;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(UserPs2.class)
public class UserPs2_ { 

    public static volatile SingularAttribute<UserPs2, Wishlist> wishlist;
    public static volatile ListAttribute<UserPs2, Item> itemList;
    public static volatile SingularAttribute<UserPs2, Integer> userId;
    public static volatile SingularAttribute<UserPs2, Cart> cart;
    public static volatile SingularAttribute<UserPs2, String> username;

}