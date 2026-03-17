package ps2;

import entities.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
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
import javax.naming.Context;
import javax.naming.InitialContext;

public class Main {

    private ConnectionFactory cf;
    private Queue podsistem2Queue;
    private Queue centralReplyPS2;

    private EntityManager em;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try {
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            p.put(Context.PROVIDER_URL, "iiop://localhost:3700");

            Context ictx = new InitialContext(p);
            cf = (ConnectionFactory) ictx.lookup("jms/__defaultConnectionFactory");
            podsistem2Queue = (Queue) ictx.lookup("jms/podsistem2Queue");
            centralReplyPS2 = (Queue) ictx.lookup("jms/centralReplyPS2");

            System.out.println("JNDI OK: cf+queues resolved (PS2)");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ps2PU"); // <-- proveri ime!
        em = emf.createEntityManager();

        processRequests();
    }

    private void processRequests() {
        try (JMSContext context = cf.createContext()) {
            JMSConsumer consumer = context.createConsumer(podsistem2Queue);
            JMSProducer producer = context.createProducer();

            while (true) {
                System.out.println("PS2: Cekam zahtev...");
                Message msg = consumer.receive();

                if (!(msg instanceof ObjectMessage)) {
                    producer.send(centralReplyPS2, context.createTextMessage("PS2 GRESKA: nije ObjectMessage"));
                    continue;
                }

                try {
                    ObjectMessage objMsg = (ObjectMessage) msg;
                    int code = objMsg.getIntProperty("redniBrojZahteva");

                    @SuppressWarnings("unchecked")
                    HashMap<String, String> map = (HashMap<String, String>) objMsg.getObject();

                    String odgovor;
                    switch (code) {
                        case 6:
                            odgovor = createCategory(map.get("name"), map.get("parentId"));
                            break;
                        case 7:
                            odgovor = createItem(map);
                            break;
                        case 8:
                            odgovor = updateItemPrice(map.get("userId"), map.get("itemId"), map.get("price"));
                            break;
                        case 9:
                            odgovor = setItemDiscount(map.get("userId"), map.get("itemId"), map.get("discount_pct"));
                            break;
                        case 10:
                            odgovor = addItemToCart(map.get("userId"), map.get("itemId"), map.get("quantity"));
                            break;
                        case 11:
                            odgovor = removeItemFromCart(map.get("userId"), map.get("itemId"), map.get("quantity"));
                            break;
                        case 12:
                            odgovor = addItemToWishlist(map.get("userId"), map.get("itemId"));
                            break;
                        case 13:
                            odgovor = removeItemFromWishlist(map.get("userId"), map.get("itemId"));
                            break;
                        case 17:
                            odgovor = getAllCategories();
                            break;
                        case 18:
                            odgovor = getItemsBySeller(map.get("sellerId"));
                            break;
                        case 19:
                            odgovor = getCartContent(map.get("userId"));
                            break;
                        case 20:
                            odgovor = getWishlistContent(map.get("userId"));
                            break;
                        case 102:
                            odgovor = getCartForPay(map.get("userId"));
                            break;
                        case 103:
                            odgovor = clearCart(map.get("userId"));
                            break;
                        case 104:
                            odgovor = syncUserPs2(map.get("userId"), map.get("username"));
                            break;
                        default:
                            odgovor = "PS2: Los redniBrojZahteva: " + code;
                            break;
                    }

                    producer.send(centralReplyPS2, context.createTextMessage(odgovor));

                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    producer.send(centralReplyPS2,
                            context.createTextMessage("PS2 error: " + ex.getMessage()));
                }
            }
        }
    }

    private void txPersist(Object o) {
        em.getTransaction().begin();
        em.persist(o);
        em.getTransaction().commit();
        em.clear();
    }

    private void txCommitOrRollback() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private String createCategory(String name, String parentIdStr) {
        name = name == null ? "" : name.trim();
        parentIdStr = parentIdStr == null ? "" : parentIdStr.trim();

        if (name.isEmpty()) {
            return "PS2: Greska - name fali.";
        }

        Category parent = null;
        if (!parentIdStr.isEmpty()) {
            try {
                int pid = Integer.parseInt(parentIdStr);
                parent = em.find(Category.class, pid);
                if (parent == null) {
                    return "PS2: Ne postoji parent category id=" + pid;
                }
            } catch (NumberFormatException e) {
                return "PS2: parentId mora biti broj.";
            }
        }
        Long exists = em.createQuery("SELECT COUNT(c) FROM Category c WHERE c.name = :n", Long.class)
                .setParameter("n", name)
                .getSingleResult();
        if (exists != null && exists > 0) {
            return "PS2: Kategorija vec postoji: " + name;
        }

        Category c = new Category();
        c.setName(name);
        c.setParentCategoryId(parent);

        try {
            txPersist(c);
            return "PS2: Kreirana kategorija id=" + c.getCategoryId() + ", name=" + c.getName()
                    + (parent != null ? ", parentId=" + parent.getCategoryId() : "");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri kreiranju kategorije.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String getAllCategories() {
        String odgovor = "KATEGORIJE: \n";
        List<Category> list = em.createQuery("SELECT c FROM Category c ORDER BY c.categoryId", Category.class)
                .getResultList();
        if (list.isEmpty()) {
            return "PS2: Nema kategorija.";
        }

        StringBuffer sb = new StringBuffer();
        for (Category c : list) {
            sb.append(c.toString() + "\n");

        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String createItem(HashMap<String, String> map) {
        String name = val(map.get("name"));
        String desc = map.get("description");
        String priceStr = val(map.get("price"));
        String discStr = val(map.get("discount_pct"));
        String categoryIdStr = val(map.get("categoryId"));
        String sellerIdStr = val(map.get("sellerId"));

        if (name.isEmpty()) {
            return "PS2: Greska - item name fali.";
        }
        if (priceStr.isEmpty()) {
            return "PS2: Greska - price fali.";
        }
        if (discStr.isEmpty()) {
            discStr = "0";
        }
        if (categoryIdStr.isEmpty()) {
            return "PS2: Greska - categoryId fali.";
        }
        if (sellerIdStr.isEmpty()) {
            return "PS2: Greska - sellerId fali.";
        }

        double price, discount;
        int categoryId, sellerId;

        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            return "PS2: price mora biti broj.";
        }
        try {
            discount = Double.parseDouble(discStr);
        } catch (Exception e) {
            return "PS2: discount_pct mora biti broj.";
        }
        try {
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (Exception e) {
            return "PS2: categoryId mora biti broj.";
        }
        try {
            sellerId = Integer.parseInt(sellerIdStr);
        } catch (Exception e) {
            return "PS2: sellerId mora biti broj.";
        }

        if (price < 0) {
            return "PS2: price ne moze biti negativan.";
        }
        if (discount < 0 || discount > 100) {
            return "PS2: discount_pct mora biti 0..100.";
        }

        Category cat = em.find(Category.class, categoryId);
        if (cat == null) {
            return "PS2: Ne postoji kategorija id=" + categoryId;
        }

        UserPs2 seller = em.find(UserPs2.class, sellerId);
        if (seller == null) {
            return "PS2: Ne postoji seller user id=" + sellerId;
        }

        Item it = new Item();
        it.setName(name);
        it.setDescription(desc);
        it.setPrice(price);
        it.setDiscountPct(discount);
        it.setCategoryId(cat);
        it.setSellerUserId(seller);

        try {
            txPersist(it);
            return "PS2: Kreiran item id=" + it.getItemId() + ", name=" + it.getName();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri kreiranju item-a.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String updateItemPrice(String userIdStr, String itemIdStr, String priceStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);
        priceStr = val(priceStr);

        int userId, itemId;
        double price;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "PS2: userId mora biti broj.";
        }

        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "PS2: itemId mora biti broj.";
        }
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            return "PS2: price mora biti broj.";
        }
        if (price < 0) {
            return "PS2: price ne moze biti negativan.";
        }

        Item it = em.find(Item.class, itemId);
        if (it == null) {
            return "PS2: Ne postoji item id=" + itemId;
        }
        int sellerId = it.getSellerUserId().getUserId();
        if (sellerId != userId) {
            return "PS2: FORBIDDEN - only seller can change price. sellerId=" + sellerId;
        }

        try {
            em.getTransaction().begin();
            Item managed = em.find(Item.class, itemId);
            managed.setPrice(price);
            em.getTransaction().commit();
            em.clear();
            return "PS2: Update price OK, itemId=" + itemId + ", price=" + price;
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri update price.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String setItemDiscount(String userIdStr, String itemIdStr, String discStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);
        discStr = val(discStr);

        int itemId, userId;
        double disc;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "PS2: userId mora biti broj.";
        }
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "PS2: itemId mora biti broj.";
        }
        try {
            disc = Double.parseDouble(discStr);
        } catch (Exception e) {
            return "PS2: discount_pct mora biti broj.";
        }
        if (disc < 0 || disc > 100) {
            return "PS2: discount_pct mora biti 0..100.";
        }

        Item it = em.find(Item.class, itemId);
        if (it == null) {
            return "PS2: Ne postoji item id=" + itemId;
        }
        int sellerId = it.getSellerUserId().getUserId();
        if (sellerId != userId) {
            return "PS2: FORBIDDEN - only seller can change discount. sellerId=" + sellerId;
        }

        try {
            em.getTransaction().begin();
            Item managed = em.find(Item.class, itemId);
            managed.setDiscountPct(disc);
            em.getTransaction().commit();
            em.clear();
            return "PS2: Discount OK, itemId=" + itemId + ", discount_pct=" + disc;
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri set discount.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String getItemsBySeller(String sellerIdStr) {
        String odgovor = "ARTIKLI KOJE PRODAJE KORISNIK: \n";
        sellerIdStr = val(sellerIdStr);
        int sellerId;
        try {
            sellerId = Integer.parseInt(sellerIdStr);
        } catch (Exception e) {
            return "PS2: sellerId mora biti broj.";
        }

        List<Item> list = em.createQuery(
                "SELECT i FROM Item i WHERE i.sellerUserId.userId = :sid ORDER BY i.itemId",
                Item.class).setParameter("sid", sellerId).getResultList();

        if (list.isEmpty()) {
            return "PS2: Nema item-a za sellerId=" + sellerId;
        }

        StringBuffer sb = new StringBuffer();
        for (Item it : list) {
            sb.append(it.toString() + "\n");

        }
        odgovor += sb.toString();
        return odgovor;
    }

    private String addItemToCart(String userIdStr, String itemIdStr, String qtyStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);
        qtyStr = val(qtyStr);

        int userId, itemId, qty;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "PS2: userId mora biti broj.";
        }
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "PS2: itemId mora biti broj.";
        }
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            return "PS2: quantity mora biti broj.";
        }
        if (qty <= 0) {
            return "PS2: quantity mora biti > 0.";
        }

        UserPs2 user = em.find(UserPs2.class, userId);
        if (user == null) {
            return "PS2: Ne postoji user id=" + userId;
        }

        Item item = em.find(Item.class, itemId);
        if (item == null) {
            return "PS2: Ne postoji item id=" + itemId;
        }

        double unitPrice = item.getPrice() * (1.0 - item.getDiscountPct() / 100.0);

        try {
            em.getTransaction().begin();
            Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid", Cart.class)
                    .setParameter("uid", userId)
                    .getResultStream().findFirst().orElse(null);

            if (cart == null) {
                cart = new Cart();
                cart.setUserId(em.find(UserPs2.class, userId));
                cart.setTotalPrice(0.0);
                em.persist(cart);
                em.flush();
            }

            Cart managedCart = em.find(Cart.class, cart.getCartId());

            CartItemPK pk = new CartItemPK(managedCart.getCartId(), itemId);
            CartItem ci = em.find(CartItem.class, pk);

            if (ci == null) {
                ci = new CartItem();
                ci.setCartItemPK(pk);
                ci.setCart(managedCart);
                ci.setItem(em.find(Item.class, itemId));
                ci.setQuantity(qty);
                em.persist(ci);
            } else {
                ci.setQuantity(ci.getQuantity() + qty);
            }

            managedCart.setTotalPrice(managedCart.getTotalPrice() + unitPrice * qty);

            em.getTransaction().commit();
            em.clear();

            return "PS2: Dodat item " + itemId + " x" + qty + " u cart userId=" + userId;

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri add to cart.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String removeItemFromCart(String userIdStr, String itemIdStr, String qtyStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);
        qtyStr = val(qtyStr);

        int userId, itemId, qty;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "PS2: userId mora biti broj.";
        }
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "PS2: itemId mora biti broj.";
        }
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            return "PS2: quantity mora biti broj.";
        }
        if (qty <= 0) {
            return "PS2: quantity mora biti > 0.";
        }

        Item item = em.find(Item.class, itemId);
        if (item == null) {
            return "PS2: Ne postoji item id=" + itemId;
        }

        double unitPrice = item.getPrice() * (1.0 - item.getDiscountPct() / 100.0);

        try {
            em.getTransaction().begin();

            Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid", Cart.class)
                    .setParameter("uid", userId)
                    .getResultStream().findFirst().orElse(null);

            if (cart == null) {
                em.getTransaction().commit();
                return "PS2: Cart ne postoji za userId=" + userId;
            }

            Cart managedCart = em.find(Cart.class, cart.getCartId());
            CartItemPK pk = new CartItemPK(managedCart.getCartId(), itemId);
            CartItem ci = em.find(CartItem.class, pk);

            if (ci == null) {
                em.getTransaction().commit();
                return "PS2: Item nije u cart-u.";
            }

            int oldQty = ci.getQuantity();
            int newQty = oldQty - qty;

            if (newQty > 0) {
                ci.setQuantity(newQty);
                managedCart.setTotalPrice(Math.max(0.0, managedCart.getTotalPrice() - unitPrice * qty));
            } else {
                managedCart.setTotalPrice(Math.max(0.0, managedCart.getTotalPrice() - unitPrice * oldQty));
                em.remove(ci);
            }

            em.getTransaction().commit();
            em.clear();

            return "PS2: Uklonjeno item " + itemId + " x" + qty + " iz cart userId=" + userId;

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "PS2: Greska pri remove from cart.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String getCartContent(String userIdStr) {
        userIdStr = val(userIdStr);
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "userId must be number.";
        }

        Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid", Cart.class)
                .setParameter("uid", userId)
                .getResultStream().findFirst().orElse(null);

        if (cart == null) {
            return "Cart does not exist for userId=" + userId;
        }

        List<CartItem> items = em.createQuery(
                "SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cid ORDER BY ci.item.itemId",
                CartItem.class).setParameter("cid", cart.getCartId()).getResultList();

        StringBuilder sb = new StringBuilder("CART userId=" + userId + " (total=" + cart.getTotalPrice() + ")\n");
        if (items.isEmpty()) {
            return sb.append("(empty)\n").toString();
        }

        for (CartItem ci : items) {
            sb.append("itemId=").append(ci.getItem().getItemId())
                    .append(", qty=").append(ci.getQuantity())
                    .append(", name=").append(ci.getItem().getName())
                    .append("\n");
        }
        return sb.toString();
    }

    private String addItemToWishlist(String userIdStr, String itemIdStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);

        int userId, itemId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "userId must be number.";
        }
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "itemId must be number.";
        }

        UserPs2 user = em.find(UserPs2.class, userId);
        if (user == null) {
            return "Not exist user id=" + userId;
        }

        Item item = em.find(Item.class, itemId);
        if (item == null) {
            return "Does not exist item id=" + itemId;
        }

        try {
            em.getTransaction().begin();

            Wishlist wl = em.createQuery("SELECT w FROM Wishlist w WHERE w.userId.userId = :uid", Wishlist.class)
                    .setParameter("uid", userId)
                    .getResultStream().findFirst().orElse(null);

            if (wl == null) {
                wl = new Wishlist();
                wl.setUserId(em.find(UserPs2.class, userId));
                wl.setCreatedAt(new Date());
                em.persist(wl);
                em.flush();
            }

            Wishlist managedWl = em.find(Wishlist.class, wl.getWishlistId());

            WishlistItemPK pk = new WishlistItemPK(managedWl.getWishlistId(), itemId);
            WishlistItem wi = em.find(WishlistItem.class, pk);

            if (wi != null) {
                em.getTransaction().commit();
                return "Item is already in wishlist.";
            }

            wi = new WishlistItem();
            wi.setWishlistItemPK(pk);
            wi.setWishlist(managedWl);
            wi.setItem(em.find(Item.class, itemId));
            wi.setAddedAt(new Date());
            em.persist(wi);

            em.getTransaction().commit();
            em.clear();

            return "Add item " + itemId + " in wishlist userId=" + userId;

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "Error add to wishlist.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String removeItemFromWishlist(String userIdStr, String itemIdStr) {
        userIdStr = val(userIdStr);
        itemIdStr = val(itemIdStr);

        int userId, itemId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "userId must be number.";
        }
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            return "itemId must be number.";
        }

        try {
            em.getTransaction().begin();

            Wishlist wl = em.createQuery("SELECT w FROM Wishlist w WHERE w.userId.userId = :uid", Wishlist.class)
                    .setParameter("uid", userId)
                    .getResultStream().findFirst().orElse(null);

            if (wl == null) {
                em.getTransaction().commit();
                return "Wishlist does not exist for userId=" + userId;
            }

            Wishlist managedWl = em.find(Wishlist.class, wl.getWishlistId());
            WishlistItemPK pk = new WishlistItemPK(managedWl.getWishlistId(), itemId);
            WishlistItem wi = em.find(WishlistItem.class, pk);

            if (wi == null) {
                em.getTransaction().commit();
                return "Item does not in wishlist.";
            }

            em.remove(wi);
            em.getTransaction().commit();
            em.clear();

            return "Remove item " + itemId + " from wishlist userId=" + userId;

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "Error  remove from wishlist.";
        } finally {
            txCommitOrRollback();
        }
    }

    private String getWishlistContent(String userIdStr) {
        String odgovor = "Wishlist content  : \n";

        userIdStr = val(userIdStr);
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "userId must be number.";
        }

        Wishlist wl = em.createQuery("SELECT w FROM Wishlist w WHERE w.userId.userId = :uid", Wishlist.class)
                .setParameter("uid", userId)
                .getResultStream().findFirst().orElse(null);

        if (wl == null) {
            return " Wishlist  does not exist for userId=" + userId;
        }

        List<WishlistItem> items = em.createQuery(
                "SELECT wi FROM WishlistItem wi WHERE wi.wishlist.wishlistId = :wid ORDER BY wi.item.itemId",
                WishlistItem.class).setParameter("wid", wl.getWishlistId()).getResultList();

        StringBuffer sb = new StringBuffer();
        if (items.isEmpty()) {
            return odgovor + "(prazna)\n";
        }
        for (WishlistItem wi : items) {
            sb.append(wi.toString() + "\n");

        }
        odgovor += sb.toString();
        return odgovor;

    }

    private String getCartForPay(String userIdStr) {
        userIdStr = val(userIdStr);
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }

        Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid", Cart.class)
                .setParameter("uid", userId)
                .getResultStream().findFirst().orElse(null);
        if (cart == null) {
            return "ERR:no_cart";
        }

        List<CartItem> items = em.createQuery(
                "SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cid ORDER BY ci.item.itemId",
                CartItem.class).setParameter("cid", cart.getCartId()).getResultList();

        if (items.isEmpty()) {
            return "ERR:empty";
        }

        StringBuilder sb = new StringBuilder();
        for (CartItem ci : items) {
            Item it = ci.getItem();
            double unit = it.getPrice() * (1.0 - it.getDiscountPct() / 100.0);

            sb.append(it.getItemId()).append(":")
                    .append(ci.getQuantity()).append(":")
                    .append(unit).append(";");
        }

        return "OK|" + cart.getTotalPrice() + "|" + sb.toString();
    }

    private String clearCart(String userIdStr) {
        userIdStr = val(userIdStr);
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }

        try {
            em.getTransaction().begin();

            Cart cart = em.createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid", Cart.class)
                    .setParameter("uid", userId)
                    .getResultStream().findFirst().orElse(null);

            if (cart == null) {
                em.getTransaction().commit();
                return "OK:already_empty";
            }
            em.createQuery("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cid")
                    .setParameter("cid", cart.getCartId())
                    .executeUpdate();
            Cart managed = em.find(Cart.class, cart.getCartId());
            managed.setTotalPrice(0.0);

            em.getTransaction().commit();
            em.clear();
            return "OK";

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return "ERR:db";
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    private String syncUserPs2(String userIdStr, String username) {
        userIdStr = val(userIdStr);
        username = username == null ? "" : username.trim();
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            return "ERR:userId";
        }

        if (username.isEmpty()) {
            return "ERR:username";
        }

        UserPs2 existing = em.find(UserPs2.class, userId);
        if (existing != null) {
            return "OK:exists";
        }

        try {
            em.getTransaction().begin();

            UserPs2 u = new UserPs2();
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

    private String val(String s) {
        return s == null ? "" : s.trim();
    }
}
