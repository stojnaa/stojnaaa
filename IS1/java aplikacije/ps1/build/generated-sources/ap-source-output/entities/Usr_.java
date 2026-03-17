package entities;

import entities.City;
import entities.Role;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:06")
@StaticMetamodel(Usr.class)
public class Usr_ { 

    public static volatile SingularAttribute<Usr, String> firstName;
    public static volatile SingularAttribute<Usr, String> lastName;
    public static volatile SingularAttribute<Usr, String> password;
    public static volatile SingularAttribute<Usr, String> address;
    public static volatile SingularAttribute<Usr, Long> balance;
    public static volatile SingularAttribute<Usr, Integer> id;
    public static volatile SingularAttribute<Usr, City> cityId;
    public static volatile ListAttribute<Usr, Role> roleList;
    public static volatile SingularAttribute<Usr, String> username;

}