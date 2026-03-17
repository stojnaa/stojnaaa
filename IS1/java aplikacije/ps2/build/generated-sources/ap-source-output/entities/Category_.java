package entities;

import entities.Category;
import entities.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:27")
@StaticMetamodel(Category.class)
public class Category_ { 

    public static volatile SingularAttribute<Category, String> name;
    public static volatile ListAttribute<Category, Category> categoryList;
    public static volatile ListAttribute<Category, Item> itemList;
    public static volatile SingularAttribute<Category, Category> parentCategoryId;
    public static volatile SingularAttribute<Category, Integer> categoryId;

}