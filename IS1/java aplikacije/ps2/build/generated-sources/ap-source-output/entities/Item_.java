package entities;

import entities.CartItem;
import entities.Category;
import entities.UserPs2;
import entities.WishlistItem;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(Item.class)
public class Item_ { 

    public static volatile SingularAttribute<Item, Integer> itemId;
    public static volatile SingularAttribute<Item, Double> price;
    public static volatile SingularAttribute<Item, Double> discountPct;
    public static volatile ListAttribute<Item, CartItem> cartItemList;
    public static volatile SingularAttribute<Item, String> name;
    public static volatile ListAttribute<Item, WishlistItem> wishlistItemList;
    public static volatile SingularAttribute<Item, String> description;
    public static volatile SingularAttribute<Item, UserPs2> sellerUserId;
    public static volatile SingularAttribute<Item, Category> categoryId;

}