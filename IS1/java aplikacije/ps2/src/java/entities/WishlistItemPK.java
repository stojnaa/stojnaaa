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
public class WishlistItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "wishlist_id")
    private int wishlistId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_id")
    private int itemId;

    public WishlistItemPK() {
    }

    public WishlistItemPK(int wishlistId, int itemId) {
        this.wishlistId = wishlistId;
        this.itemId = itemId;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
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
        hash += (int) wishlistId;
        hash += (int) itemId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistItemPK)) {
            return false;
        }
        WishlistItemPK other = (WishlistItemPK) object;
        if (this.wishlistId != other.wishlistId) {
            return false;
        }
        if (this.itemId != other.itemId) {
            return false;
        }
        return true;
    }

   @Override
public String toString() {
    return "WishlistItemPK{" +
            "wishlistId=" + wishlistId +
            ", itemId=" + itemId +
            '}';
}
    
}
