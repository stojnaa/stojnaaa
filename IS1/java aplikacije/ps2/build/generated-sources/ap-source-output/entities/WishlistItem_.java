package entities;

import entities.Item;
import entities.Wishlist;
import entities.WishlistItemPK;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(WishlistItem.class)
public class WishlistItem_ { 

    public static volatile SingularAttribute<WishlistItem, Date> addedAt;
    public static volatile SingularAttribute<WishlistItem, Item> item;
    public static volatile SingularAttribute<WishlistItem, WishlistItemPK> wishlistItemPK;
    public static volatile SingularAttribute<WishlistItem, Wishlist> wishlist;

}