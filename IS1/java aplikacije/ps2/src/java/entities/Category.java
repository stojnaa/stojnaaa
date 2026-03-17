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
@Table(name = "category")
@NamedQueries({
    @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")})
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "category_id")
    private Integer categoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryId")
    private List<Item> itemList;
    @OneToMany(mappedBy = "parentCategoryId")
    private List<Category> categoryList;
    @JoinColumn(name = "parent_category_id", referencedColumnName = "category_id")
    @ManyToOne
    private Category parentCategoryId;

    public Category() {
    }

    public Category(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Category(Integer categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public Category getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Category parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        if ((this.categoryId == null && other.categoryId != null) || (this.categoryId != null && !this.categoryId.equals(other.categoryId))) {
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

    String childrenInfo;
    try {
        childrenInfo = (categoryList == null) ? "null" : ("size=" + categoryList.size());
    } catch (Exception e) {
        childrenInfo = "<unavailable>";
    }

    String parentInfo;
    try {
        parentInfo = (parentCategoryId == null) ? "null"
                : ("categoryId=" + parentCategoryId.getCategoryId() + ", name=" + parentCategoryId.getName());
    } catch (Exception e) {
        parentInfo = "<unavailable>";
    }

    return "Category{" +
            "categoryId=" + categoryId +
            ", name='" + name + '\'' +
            ", itemList=" + itemsInfo +
            ", categoryList(children)=" + childrenInfo +
            ", parentCategoryId=(" + parentInfo + ")" +
            '}';
}
    
}
