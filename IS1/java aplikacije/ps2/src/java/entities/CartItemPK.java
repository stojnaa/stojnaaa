/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class CartItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "cart_id")
    private int cartId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_id")
    private int itemId;

    public CartItemPK() {
    }

    public CartItemPK(int cartId, int itemId) {
        this.cartId = cartId;
        this.itemId = itemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cartId;
        hash += (int) itemId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItemPK)) {
            return false;
        }
        CartItemPK other = (CartItemPK) object;
        if (this.cartId != other.cartId) {
            return false;
        }
        if (this.itemId != other.itemId) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    return "CartItemPK{" +
            "cartId=" + cartId +
            ", itemId=" + itemId +
            '}';
}
    
}
