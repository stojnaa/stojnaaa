package entities;

import entities.UserPs2;
import entities.WishlistItem;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(Wishlist.class)
public class Wishlist_ { 

    public static volatile SingularAttribute<Wishlist, Date> createdAt;
    public static volatile ListAttribute<Wishlist, WishlistItem> wishlistItemList;
    public static volatile SingularAttribute<Wishlist, Integer> wishlistId;
    public static volatile SingularAttribute<Wishlist, UserPs2> userId;

}