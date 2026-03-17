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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Order1.findAll", query = "SELECT o FROM Order1 o")})
public class Order1 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "order_id")
    private Integer orderId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_price")
    private double totalPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Basic(optional = false)
    @NotNull
    @Column(name = "delivery_city_id")
    private int deliveryCityId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private List<OrderItem> orderItemList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Transaction transaction;
    @JoinColumn(name = "buyer_user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private UserPs3 buyerUserId;

    public Order1() {
    }

    public Order1(Integer orderId) {
        this.orderId = orderId;
    }

    public Order1(Integer orderId, double totalPrice, Date createdAt, String deliveryAddress, int deliveryCityId) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.deliveryAddress = deliveryAddress;
        this.deliveryCityId = deliveryCityId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public int getDeliveryCityId() {
        return deliveryCityId;
    }

    public void setDeliveryCityId(int deliveryCityId) {
        this.deliveryCityId = deliveryCityId;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public UserPs3 getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(UserPs3 buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderId != null ? orderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Order1)) {
            return false;
        }
        Order1 other = (Order1) object;
        if ((this.orderId == null && other.orderId != null) || (this.orderId != null && !this.orderId.equals(other.orderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Order{"
                + "orderId=" + orderId
                + ", buyerUserId=" + (buyerUserId != null ? buyerUserId.getUserId() : null)
                + ", totalPrice=" + totalPrice
                + ", createdAt=" + createdAt
                + ", deliveryAddress='" + deliveryAddress + '\''
                + ", deliveryCityId=" + deliveryCityId
                + ", itemsCount=" + (orderItemList != null ? orderItemList.size() : 0)
                + ", transactionId=" + (transaction != null ? transaction.getTransactionId() : null)
                + '}';
    }

}
