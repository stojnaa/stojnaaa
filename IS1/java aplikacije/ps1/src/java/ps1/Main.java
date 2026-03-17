/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ps1;

import entities.City;
import entities.Role;
import entities.Usr;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.persistence.TypedQuery;
import java.util.Properties;
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
    private Queue podsistem1Queue;

    // @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    //@PersistenceContext(unitName = "Podsistem1PU")
    private EntityManager em1;

    /**
     * @param args the command line arguments
     */
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
            podsistem1Queue = (Queue) ictx.lookup("jms/podsistem1Queue");
            centralReplyPS1 = (Queue) ictx.lookup("jms/centralReplyPS1");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ps1PU");
        em1 = emf.createEntityManager();

        processRequests();
    }

    private void processRequests() {
        try (JMSContext context = cf.createContext()) {
            JMSConsumer consumer = context.createConsumer(podsistem1Queue);
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
                            case 1:
                                odgovor = checkCredentials(map.get("username"), map.get("password"));
                                break;
                            case 2:
                                odgovor = createCity(map.get("name"));
                                break;
                            case 3:
                                odgovor = createUser(map.get("username"), map.get("password"),
                                        map.get("firstName"), map.get("lastName"),
                                        map.get("address"), map.get("balance"), map.get("cityId"));
                                break;
                            case 4:
                                odgovor = addMoney(map.get("userId"), map.get("amount"));
                                break;
                            case 5:
                                odgovor = updateUser(map.get("userId"), map.get("address"), map.get("cityId"));
                                break;
                            case 15:
                                odgovor = getAllCities();
                                break;
                            case 16:
                                odgovor = getAllUsers();
                                break;
                            case 101:
                                odgovor = getUserShippingInfo(map.get("userId")); // vrati "address|cityId"
                                break;
                            case 106:
                                odgovor = getUserRoles(map.get("userId"));
                                break;
                            case 107: {
    odgovor = deductMoney(map.get("userId"), map.get("amount"));
    break;
}
                            default:
                                odgovor = "Los redniBrojZahteva: " + code;
                                break;
                            
                        }

                        Message txtMsg = context.createTextMessage(odgovor);
                        producer.send(centralReplyPS1, txtMsg);
                        System.out.println("Poslao nazad odgovor: " + odgovor);

                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        producer.send(centralReplyPS1, context.createTextMessage("GRESKA u PS1: " + ex.getMessage()));
                    }
                }
            }
        }
    }

    private void executeInTransaction(Object obj) {
        em1.getTransaction().begin();
        em1.persist(obj);
        em1.getTransaction().commit();
        em1.clear();
    }

    private void rollbackIfActive() {
        if (em1.getTransaction().isActive()) {
            em1.getTransaction().rollback();
        }
    }

    private String checkCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return "error: username/password missing.";
        }

        username = username.trim();
        password = password.trim();

        try {
            TypedQuery<Usr> q = em1.createQuery(
                    "SELECT u FROM Usr u WHERE u.username = :un AND u.password = :pw",
                    Usr.class);
            q.setParameter("un", username);
            q.setParameter("pw", password);

            List<Usr> list = q.getResultList();
            if (list.isEmpty()) {
                return "Login FAIL";
            }

            Usr u = list.get(0);
            return "OK|" + u.getId() + "|" + u.getUsername() + "|"+u.getPassword();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "error when checking credentials.";
        }
    }

    private String createCity(String name) {
        String odgovor = "Created city: ";
        City mesto = new City();
        mesto.setName(name);

        try {
            executeInTransaction(mesto);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "error create city.";
        } finally {
            rollbackIfActive();
        }
        return odgovor + mesto;
    }

    private String createUser(String username, String password, String firstName, String lastName, String address, String balanceStr, String cityIdStr) {
        String odgovor = "Created user: ";
        if (username.isEmpty() || password.isEmpty()) {
            return "error: username/password missing.";
        }
        if (cityIdStr.isEmpty()) {
            return "error: cityId missing.";
        }

        int cityId;
        long balance = 0;

        try {
            cityId = Integer.parseInt(cityIdStr);
        } catch (NumberFormatException e) {
            return "error: cityId must be number.";
        }

        if (!balanceStr.isEmpty()) {
            try {
                balance = Long.parseLong(balanceStr);
            } catch (NumberFormatException e) {
                return "error: balance must be number.";
            }
            if (balance < 0) {
                return "error: balance can not be negative.";
            }
        }
        City city = em1.find(City.class, cityId);
        if (city == null) {
            return "No city with id=" + cityId;
        }
        Long exists = em1.createQuery("SELECT COUNT(u) FROM Usr u WHERE u.username = :un", Long.class)
                .setParameter("un", username)
                .getSingleResult();
        if (exists != null && exists > 0) {
            return "Username already exists: " + username;
        }

        Usr korisnik = new Usr();
        korisnik.setUsername(username);
        korisnik.setPassword(password);
        korisnik.setFirstName(firstName);
        korisnik.setLastName(lastName);
        korisnik.setAddress(address);
        korisnik.setBalance(balance);
        korisnik.setCityId(city);

        try {
            executeInTransaction(korisnik);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "error when create user.";
        } finally {
            rollbackIfActive();
        }

        return "OK|" + korisnik.getId() + "|" + korisnik.getUsername();
    }

    private String addMoney(String userIdStr, String amountStr) {
        if (userIdStr == null || userIdStr.trim().isEmpty()
                || amountStr == null || amountStr.trim().isEmpty()) {
            return "error: userId/amount missing.";
        }

        int userId;
        long amount;

        try {
            userId = Integer.parseInt(userIdStr.trim());
            amount = Long.parseLong(amountStr.trim());
        } catch (NumberFormatException e) {
            return "error: userId and amount must be numbers.";
        }

        if (amount <= 0) {
            return "error: amount must bi > 0.";
        }

        Usr u = em1.find(Usr.class, userId);
        if (u == null) {
            return "No user with id=" + userId;
        }

        try {
            em1.getTransaction().begin();
            Usr managed = em1.find(Usr.class, userId);
            managed.setBalance(managed.getBalance() + amount);

            em1.getTransaction().commit();
            em1.refresh(managed);
            em1.clear();

            return "Add " + amount + " user id=" + userId
                    + ". New balance = " + managed.getBalance();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "Error when adding money.";
        } finally {
            rollbackIfActive();
        }
    }
    private String deductMoney(String userIdStr, String amountStr) {
        if (userIdStr == null || userIdStr.trim().isEmpty()
                || amountStr == null || amountStr.trim().isEmpty()) {
            return "error: userId/amount missing.";
        }

        int userId;
        long amount;

        try {
            userId = Integer.parseInt(userIdStr.trim());
            amount = Long.parseLong(amountStr.trim());
        } catch (NumberFormatException e) {
            return "error: userId and amount must be numbers.";
        }

        if (amount <= 0) {
            return "error: amount must bi > 0.";
        }

        Usr u = em1.find(Usr.class, userId);
        if (u == null) {
            return "No user with id=" + userId;
        }

        try {
            em1.getTransaction().begin();
            Usr managed = em1.find(Usr.class, userId);
            if (managed.getBalance() < amount) {
            em1.getTransaction().rollback();
            return "ERR:not_enough";
        }
                        managed.setBalance(managed.getBalance() - amount);


            em1.getTransaction().commit();
            em1.refresh(managed);
            em1.clear();

            return "OK|" + managed.getBalance();

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "Error when adding money.";
        } finally {
            rollbackIfActive();
        }
    }
    private String updateUser(String userIdStr, String address, String cityIdStr) {
        String odgovor = "City change in ";

        if (userIdStr.isEmpty()) {
            return "error: userId missing.";
        }
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return "error: userId must be number.";
        }

        Usr u = em1.find(Usr.class, userId);
        if (u == null) {
            return "No user with id=" + userId;
        }

        City newCity = null;
        if (!cityIdStr.isEmpty()) {
            try {
                int cityId = Integer.parseInt(cityIdStr);
                newCity = em1.find(City.class, cityId);
                if (newCity == null) {
                    return "No city with id=" + cityId;
                }
            } catch (NumberFormatException e) {
                return "ERR: cityId must be number.";
            }
        }

        City finalCity = newCity;
        String finalAddress = address.isEmpty() ? null : address;
        try {
            em1.getTransaction().begin();
            if (finalAddress != null) {
                em1.createQuery("UPDATE Usr u SET u.address = :adr WHERE u.id = :id")
                        .setParameter("adr", finalAddress)
                        .setParameter("id", userId)
                        .executeUpdate();
            }
            if (finalCity != null) {
                em1.createQuery("UPDATE Usr u SET u.cityId = :cid WHERE u.id = :id")
                        .setParameter("cid", finalCity)
                        .setParameter("id", userId)
                        .executeUpdate();
            }

            em1.getTransaction().commit();
            Usr refreshed = em1.find(Usr.class, userId);
            em1.refresh(refreshed);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "Error when changing city.";
        } finally {
            rollbackIfActive();
        }
        return "Update OK: userId=" + userId
                + (finalAddress != null ? ", address=" + finalAddress : "")
                + (finalCity != null ? ", cityId=" + finalCity.getId() : "");

    }

    private String getAllCities() {
        String odgovor = "Cities: \n";
        TypedQuery<City> q = em1.createQuery("SELECT c FROM City c ORDER BY c.id", City.class);
        List<City> list = q.getResultList();
        if (list.isEmpty()) {
            return "No cities found.";
        }
        StringBuffer sb = new StringBuffer();
        for (City m : list) {
            sb.append(m.toString() + "\n");
        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String getAllUsers() {
        String odgovor = "Users: \n";
        TypedQuery<Usr> q = em1.createQuery("SELECT u FROM Usr u ORDER BY u.id", Usr.class);
        List<Usr> list = q.getResultList();
        if (list.isEmpty()) {
            return "No users found.";
        }

        StringBuffer sb = new StringBuffer();
        for (Usr u : list) {
            sb.append(u.toString()).append("\n");
        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String getUserShippingInfo(String userIdStr) {
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }

        Usr u = em1.find(Usr.class, userId);
        if (u == null) {
            return "ERR:no_user";
        }

        String adr = (u.getAddress() == null) ? "" : u.getAddress();
        int cityId = (u.getCityId() == null || u.getCityId().getId() == null) ? 0 : u.getCityId().getId();
        long bal = u.getBalance();
        return "OK|" + adr + "|" + cityId+ "|" + bal;
    }
    private String getUserRoles(String userIdStr) {
    userIdStr = userIdStr == null ? "" : userIdStr.trim();
    int userId;
    try { userId = Integer.parseInt(userIdStr); }
    catch (Exception e) { return "ERR:userId"; }

    Usr u = em1.find(Usr.class, userId);
    if (u == null) return "ERR:no_user";
    List<Role> roles = u.getRoleList();
    if (roles == null || roles.isEmpty()) return "OK|"; 

    StringBuilder sb = new StringBuilder();
    for (Role r : roles) {
        if (r == null) continue;
        if (sb.length() > 0) sb.append(",");
        sb.append(r.getName());
    }
    return "OK|" + sb.toString();
}

}
