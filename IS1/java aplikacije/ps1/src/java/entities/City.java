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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "city")
@NamedQueries({
    @NamedQuery(name = "City.findAll", query = "SELECT c FROM City c")})
public class City implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cityId")
    private List<Usr> usrList;

    public City() {
    }

    public City(Integer id) {
        this.id = id;
    }

    public City(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Usr> getUsrList() {
        return usrList;
    }

    public void setUsrList(List<Usr> usrList) {
        this.usrList = usrList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof City)) {
            return false;
        }
        City other = (City) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
public String toString() {
    String usrInfo;
    try {
        usrInfo = (usrList == null) ? "null" : ("size=" + usrList.size());
        // Ako baš hoćeš i id-eve korisnika, možeš ovde da mapiraš, ali pazi na lazy:
        // usrInfo = usrList.stream().map(u -> String.valueOf(u.getId())).collect(Collectors.joining(","));
    } catch (Exception e) {
        usrInfo = "<unavailable>";
    }

    return "City{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", usrList=" + usrInfo +
            '}';
}
    
}
