/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "wishlist_item")
@NamedQueries({
    @NamedQuery(name = "WishlistItem.findAll", query = "SELECT w FROM WishlistItem w")})
public class WishlistItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WishlistItemPK wishlistItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt;
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;
    @JoinColumn(name = "wishlist_id", referencedColumnName = "wishlist_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Wishlist wishlist;

    public WishlistItem() {
    }

    public WishlistItem(WishlistItemPK wishlistItemPK) {
        this.wishlistItemPK = wishlistItemPK;
    }

    public WishlistItem(WishlistItemPK wishlistItemPK, Date addedAt) {
        this.wishlistItemPK = wishlistItemPK;
        this.addedAt = addedAt;
    }

    public WishlistItem(int wishlistId, int itemId) {
        this.wishlistItemPK = new WishlistItemPK(wishlistId, itemId);
    }

    public WishlistItemPK getWishlistItemPK() {
        return wishlistItemPK;
    }

    public void setWishlistItemPK(WishlistItemPK wishlistItemPK) {
        this.wishlistItemPK = wishlistItemPK;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishlistItemPK != null ? wishlistItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistItem)) {
            return false;
        }
        WishlistItem other = (WishlistItem) object;
        if ((this.wishlistItemPK == null && other.wishlistItemPK != null) || (this.wishlistItemPK != null && !this.wishlistItemPK.equals(other.wishlistItemPK))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String itemInfo;
    try {
        itemInfo = (item == null) ? "null" : ("itemId=" + item.getItemId() + ", name=" + item.getName());
    } catch (Exception e) {
        itemInfo = "<unavailable>";
    }

    String wishlistInfo;
    try {
        wishlistInfo = (wishlist == null) ? "null" : ("wishlistId=" + wishlist.getWishlistId());
    } catch (Exception e) {
        wishlistInfo = "<unavailable>";
    }

    return "WishlistItem{" +
            "wishlistItemPK=" + wishlistItemPK +
            ", addedAt=" + addedAt +
            ", item=(" + itemInfo + ")" +
            ", wishlist=(" + wishlistInfo + ")" +
            '}';
}
    
}
