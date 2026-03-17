/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "user_ps2")
@NamedQueries({
    @NamedQuery(name = "UserPs2.findAll", query = "SELECT u FROM UserPs2 u")})
public class UserPs2 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "username")
    private String username;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sellerUserId")
    private List<Item> itemList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "userId")
    private Wishlist wishlist;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "userId")
    private Cart cart;

    public UserPs2() {
    }

    public UserPs2(Integer userId) {
        this.userId = userId;
    }

    public UserPs2(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserPs2)) {
            return false;
        }
        UserPs2 other = (UserPs2) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String itemsInfo;
    try {
        itemsInfo = (itemList == null) ? "null" : ("size=" + itemList.size());
    } catch (Exception e) {
        itemsInfo = "<unavailable>";
    }

    String wishlistInfo;
    try {
        wishlistInfo = (wishlist == null) ? "null" : ("wishlistId=" + wishlist.getWishlistId());
    } catch (Exception e) {
        wishlistInfo = "<unavailable>";
    }

    String cartInfo;
    try {
        cartInfo = (cart == null) ? "null" : ("cartId=" + cart.getCartId());
    } catch (Exception e) {
        cartInfo = "<unavailable>";
    }

    return "UserPs2{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", itemList(selling)=" + itemsInfo +
            ", wishlist=(" + wishlistInfo + ")" +
            ", cart=(" + cartInfo + ")" +
            '}';
}
    
}
