/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps3;

import entities.*;
import java.util.*;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author Lenovo
 */
public class Main {
    //@Resource(lookup = "jms/__defaultConnectionFactory")

    private ConnectionFactory cf;

    //@Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem3Queue;

    // @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS3;

    //@PersistenceContext(unitName = "Podsistem1PU")
    private EntityManager em;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try {
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            p.put(Context.PROVIDER_URL, "iiop://localhost:3700"); // domain1 IIOP

            Context ictx = new InitialContext(p);

            cf = (ConnectionFactory) ictx.lookup("jms/__defaultConnectionFactory");
            podsistem3Queue = (Queue) ictx.lookup("jms/podsistem3Queue");
            centralReplyPS3 = (Queue) ictx.lookup("jms/centralReplyPS3");

            System.out.println("JNDI OK: cf+queues resolved");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ps3PU");
        em = emf.createEntityManager();

        obradiZahteve();
    }

    private void obradiZahteve() {
        try (JMSContext context = cf.createContext()) {
            JMSConsumer consumer = context.createConsumer(podsistem3Queue);
            JMSProducer producer = context.createProducer();

            while (true) {
                System.out.println("Cekam zahtev ");
                Message msg = consumer.receive();
                System.out.print("Primio zahtev broj ");

                if (msg instanceof ObjectMessage) {
                    try {
                        ObjectMessage objMsg = (ObjectMessage) msg;
                        int code = objMsg.getIntProperty("redniBrojZahteva");

                        @SuppressWarnings("unchecked")
                        HashMap<String, String> map = (HashMap<String, String>) objMsg.getObject();

                        String odgovor;
                        switch (code) {
                            case 14:
                                odgovor = pay(map); // map sadrži sve
                                break;
                            case 21:
                                odgovor = getUserOrders(map.get("userId"));
                                break;
                            case 22:
                                odgovor = getAllOrders();
                                break;
                            case 23:
                                odgovor = getAllTransactions();
                                break;
                            case 105:
                                odgovor = syncUserPs3(map.get("userId"), map.get("username"));
                                break;
                            default:
                                odgovor = "Los redniBrojZahteva: " + code;
                                break;
                        }

                        Message txtMsg = context.createTextMessage(odgovor);
                        producer.send(centralReplyPS3, txtMsg);
                        System.out.println("Poslao nazad odgovor: " + odgovor);

                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        producer.send(centralReplyPS3, context.createTextMessage("GRESKA u PS3: " + ex.getMessage()));
                    }
                }
            }
        }
    }

    private void izvrsiTransakciju(Object obj) {
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
        em.clear();
    }

    private void nakonTransakcije() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private String pay(HashMap<String, String> map) {
        String userIdStr = map.get("userId");
        String address = map.get("address");
        String cityIdStr = map.get("cityId");
        String totalStr = map.get("total");
        String itemsStr = map.get("items");

        int userId, cityId;
        double total;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }
        try {
            cityId = Integer.parseInt(cityIdStr);
        } catch (Exception e) {
            return "ERR:cityId";
        }
        try {
            total = Double.parseDouble(totalStr);
        } catch (Exception e) {
            return "ERR:total";
        }
        if (itemsStr == null || itemsStr.trim().isEmpty()) {
            return "ERR:items";
        }

        UserPs3 buyer = em.find(UserPs3.class, userId);
        if (buyer == null) {
            return "ERR:no_user_ps3";
        }

        try {
            em.getTransaction().begin();

            Order1 o = new Order1();
            o.setBuyerUserId(buyer);
            o.setDeliveryAddress(address == null ? "" : address);
            o.setDeliveryCityId(cityId);
            o.setCreatedAt(new Date());
            o.setTotalPrice(total);
            em.persist(o);
            em.flush();
            String[] parts = itemsStr.split(";");
            for (String p : parts) {
                if (p.trim().isEmpty()) {
                    continue;
                }
                String[] f = p.split(":");
                int itemId = Integer.parseInt(f[0]);
                int qty = Integer.parseInt(f[1]);
                double unit = Double.parseDouble(f[2]);

                OrderItem oi = new OrderItem();
                oi.setOrderId(o);
                oi.setItemId(itemId);
                oi.setQuantity(qty);
                oi.setUnitPrice(unit);
                em.persist(oi);
            }

            Transaction t = new Transaction();
            t.setOrderId(o);
            t.setAmountPaid(total);
            t.setPaidAt(new Date());
            em.persist(t);

            em.getTransaction().commit();
            em.clear();

            return "OK:orderId=" + o.getOrderId() + ", transactionId=" + t.getTransactionId();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "ERR:db";
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private String getUserOrders(String userIdStr) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "userId must be number.";
        }

        UserPs3 u = em.find(UserPs3.class, userId);
        if (u == null) {
            return "No user with id=" + userId;
        }

        List<Order1> orders = em.createQuery(
                "SELECT o FROM Order1 o WHERE o.buyerUserId.userId = :uid ORDER BY o.orderId",
                Order1.class)
                .setParameter("uid", userId)
                .getResultList();

        if (orders.isEmpty()) {
            return "User does not have orders.";
        }
        String odgovor = "All orders userId=" + userId + ":\n";
        StringBuffer sb = new StringBuffer();

        for (Order1 o : orders) {
            sb.append(o.toString()).append("\n");

        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String getAllOrders() {
        String odgovor = "All orders: \n";
        List<Order1> orders = em.createQuery(
                "SELECT o FROM Order1 o ORDER BY o.orderId",
                Order1.class)
                .getResultList();

        if (orders.isEmpty()) {
            return "No orders.";
        }

        StringBuffer sb = new StringBuffer();
        for (Order1 o : orders) {
            sb.append(o.toString()).append("\n");

        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String getAllTransactions() {
        String odgovor = "All Transactions: \n";
        List<Transaction> txs = em.createQuery(
                "SELECT t FROM Transaction t ORDER BY t.transactionId",
                Transaction.class)
                .getResultList();

        if (txs.isEmpty()) {
            return "No transactions.";
        }

        StringBuffer sb = new StringBuffer();
        for (Transaction t : txs) {
            sb.append(t.toString()).append("\n");
        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String syncUserPs3(String userIdStr, String username) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }

        if (username.isEmpty()) {
            return "ERR:username";
        }

        UserPs3 existing = em.find(UserPs3.class, userId);
        if (existing != null) {
            return "OK:exists";
        }

        try {
            em.getTransaction().begin();

            UserPs3 u = new UserPs3();
            u.setUserId(userId);
            u.setUsername(username);
            em.persist(u);

            em.getTransaction().commit();
            em.clear();
            return "OK";

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return "ERR:db";
        }
    }

}
