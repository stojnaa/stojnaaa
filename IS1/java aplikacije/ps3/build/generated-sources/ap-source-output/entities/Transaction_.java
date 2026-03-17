package entities;

import entities.Order1;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-03-01T10:30:48")
@StaticMetamodel(Transaction.class)
public class Transaction_ { 

    public static volatile SingularAttribute<Transaction, Double> amountPaid;
    public static volatile SingularAttribute<Transaction, Order1> orderId;
    public static volatile SingularAttribute<Transaction, Date> paidAt;
    public static volatile SingularAttribute<Transaction, Integer> transactionId;

}