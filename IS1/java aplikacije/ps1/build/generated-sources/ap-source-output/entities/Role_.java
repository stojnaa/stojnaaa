package entities;

import entities.Usr;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:06")
@StaticMetamodel(Role.class)
public class Role_ { 

    public static volatile ListAttribute<Role, Usr> usrList;
    public static volatile SingularAttribute<Role, String> name;
    public static volatile SingularAttribute<Role, Integer> id;

}