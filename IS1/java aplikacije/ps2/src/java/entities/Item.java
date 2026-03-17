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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "item")
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i")})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "item_id")
    private Integer itemId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private double price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "discount_pct")
    private double discountPct;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<WishlistItem> wishlistItemList;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne(optional = false)
    private Category categoryId;
    @JoinColumn(name = "seller_user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private UserPs2 sellerUserId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<CartItem> cartItemList;

    public Item() {
    }

    public Item(Integer itemId) {
        this.itemId = itemId;
    }

    public Item(Integer itemId, String name, double price, double discountPct) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.discountPct = discountPct;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(double discountPct) {
        this.discountPct = discountPct;
    }

    public List<WishlistItem> getWishlistItemList() {
        return wishlistItemList;
    }

    public void setWishlistItemList(List<WishlistItem> wishlistItemList) {
        this.wishlistItemList = wishlistItemList;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    public UserPs2 getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(UserPs2 sellerUserId) {
        this.sellerUserId = sellerUserId;
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
        hash += (itemId != null ? itemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.itemId == null && other.itemId != null) || (this.itemId != null && !this.itemId.equals(other.itemId))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String categoryInfo;
    try {
        categoryInfo = (categoryId == null) ? "null"
                : ("categoryId=" + categoryId.getCategoryId() + ", name=" + categoryId.getName());
    } catch (Exception e) {
        categoryInfo = "<unavailable>";
    }

    String sellerInfo;
    try {
        sellerInfo = (sellerUserId == null) ? "null"
                : ("userId=" + sellerUserId.getUserId() + ", username=" + sellerUserId.getUsername());
    } catch (Exception e) {
        sellerInfo = "<unavailable>";
    }

    String wishlistInfo;
    try {
        wishlistInfo = (wishlistItemList == null) ? "null" : ("size=" + wishlistItemList.size());
    } catch (Exception e) {
        wishlistInfo = "<unavailable>";
    }

    String cartItemsInfo;
    try {
        cartItemsInfo = (cartItemList == null) ? "null" : ("size=" + cartItemList.size());
    } catch (Exception e) {
        cartItemsInfo = "<unavailable>";
    }

    return "Item{" +
            "itemId=" + itemId +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", price=" + price +
            ", discountPct=" + discountPct +
            ", wishlistItemList=" + wishlistInfo +
            ", categoryId=(" + categoryInfo + ")" +
            ", sellerUserId=(" + sellerInfo + ")" +
            ", cartItemList=" + cartItemsInfo +
            '}';
}
    
}
