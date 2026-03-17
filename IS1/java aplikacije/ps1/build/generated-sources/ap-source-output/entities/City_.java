package entities;

import entities.Usr;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:06")
@StaticMetamodel(City.class)
public class City_ { 

    public static volatile ListAttribute<City, Usr> usrList;
    public static volatile SingularAttribute<City, String> name;
    public static volatile SingularAttribute<City, Integer> id;

}