/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "cart_item")
@NamedQueries({
    @NamedQuery(name = "CartItem.findAll", query = "SELECT c FROM CartItem c")})
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CartItemPK cartItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cart cart;
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;

    public CartItem() {
    }

    public CartItem(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public CartItem(CartItemPK cartItemPK, int quantity) {
        this.cartItemPK = cartItemPK;
        this.quantity = quantity;
    }

    public CartItem(int cartId, int itemId) {
        this.cartItemPK = new CartItemPK(cartId, itemId);
    }

    public CartItemPK getCartItemPK() {
        return cartItemPK;
    }

    public void setCartItemPK(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartItemPK != null ? cartItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItem)) {
            return false;
        }
        CartItem other = (CartItem) object;
        if ((this.cartItemPK == null && other.cartItemPK != null) || (this.cartItemPK != null && !this.cartItemPK.equals(other.cartItemPK))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String cartInfo;
    try {
        cartInfo = (cart == null) ? "null" : ("cartId=" + cart.getCartId());
    } catch (Exception e) {
        cartInfo = "<unavailable>";
    }

    String itemInfo;
    try {
        itemInfo = (item == null) ? "null" : ("itemId=" + item.getItemId() + ", name=" + item.getName());
    } catch (Exception e) {
        itemInfo = "<unavailable>";
    }

    return "CartItem{" +
            "cartItemPK=" + cartItemPK +
            ", quantity=" + quantity +
            ", cart=(" + cartInfo + ")" +
            ", item=(" + itemInfo + ")" +
            '}';
}
}
