/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "wishlist")
@NamedQueries({
    @NamedQuery(name = "Wishlist.findAll", query = "SELECT w FROM Wishlist w")})
public class Wishlist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "wishlist_id")
    private Integer wishlistId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wishlist")
    private List<WishlistItem> wishlistItemList;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OneToOne(optional = false)
    private UserPs2 userId;

    public Wishlist() {
    }

    public Wishlist(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Wishlist(Integer wishlistId, Date createdAt) {
        this.wishlistId = wishlistId;
        this.createdAt = createdAt;
    }

    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<WishlistItem> getWishlistItemList() {
        return wishlistItemList;
    }

    public void setWishlistItemList(List<WishlistItem> wishlistItemList) {
        this.wishlistItemList = wishlistItemList;
    }

    public UserPs2 getUserId() {
        return userId;
    }

    public void setUserId(UserPs2 userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishlistId != null ? wishlistId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wishlist)) {
            return false;
        }
        Wishlist other = (Wishlist) object;
        if ((this.wishlistId == null && other.wishlistId != null) || (this.wishlistId != null && !this.wishlistId.equals(other.wishlistId))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String itemsInfo;
    try {
        itemsInfo = (wishlistItemList == null) ? "null" : ("size=" + wishlistItemList.size());
    } catch (Exception e) {
        itemsInfo = "<unavailable>";
    }

    String userInfo;
    try {
        userInfo = (userId == null) ? "null"
                : ("userId=" + userId.getUserId() + ", username=" + userId.getUsername());
    } catch (Exception e) {
        userInfo = "<unavailable>";
    }

    return "Wishlist{" +
            "wishlistId=" + wishlistId +
            ", createdAt=" + createdAt +
            ", wishlistItemList=" + itemsInfo +
            ", userId=(" + userInfo + ")" +
            '}';
}
    
}
