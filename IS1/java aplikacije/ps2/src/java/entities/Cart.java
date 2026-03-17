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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "cart")
@NamedQueries({
    @NamedQuery(name = "Cart.findAll", query = "SELECT c FROM Cart c")})
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cart_id")
    private Integer cartId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_price")
    private double totalPrice;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OneToOne(optional = false)
    private UserPs2 userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartItem> cartItemList;

    public Cart() {
    }

    public Cart(Integer cartId) {
        this.cartId = cartId;
    }

    public Cart(Integer cartId, double totalPrice) {
        this.cartId = cartId;
        this.totalPrice = totalPrice;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public UserPs2 getUserId() {
        return userId;
    }

    public void setUserId(UserPs2 userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartId != null ? cartId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cart)) {
            return false;
        }
        Cart other = (Cart) object;
        if ((this.cartId == null && other.cartId != null) || (this.cartId != null && !this.cartId.equals(other.cartId))) {
            return false;
        }
        return true;
    }

   @Override
public String toString() {
    String userInfo;
    try {
        userInfo = (userId == null) ? "null"
                : ("userId=" + userId.getUserId() + ", username=" + userId.getUsername());
    } catch (Exception e) {
        userInfo = "<unavailable>";
    }

    String itemsInfo;
    try {
        itemsInfo = (cartItemList == null) ? "null" : ("size=" + cartItemList.size());
    } catch (Exception e) {
        itemsInfo = "<unavailable>";
    }

    return "Cart{" +
            "cartId=" + cartId +
            ", totalPrice=" + totalPrice +
            ", userId=(" + userInfo + ")" +
            ", cartItemList=" + itemsInfo +
            '}';
}
    
}
