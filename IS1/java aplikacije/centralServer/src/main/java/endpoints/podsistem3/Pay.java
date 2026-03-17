/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints.podsistem3;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("ps3/pay/{userId}")
public class Pay {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue ps1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue replyPS1;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue ps2Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue replyPS2;

    @Resource(lookup = "jms/podsistem3Queue")
    private Queue ps3Queue;

    @Resource(lookup = "jms/centralReplyPS3")
    private Queue replyPS3;

    @POST
    public Response pay(@PathParam("userId") String userId) {
        try (JMSContext ctx = connectionFactory.createContext()) {
            HashMap<String, String> m1 = new HashMap<>();
            m1.put("userId", userId);
            ObjectMessage req1 = ctx.createObjectMessage(m1);
            req1.setIntProperty("redniBrojZahteva", 101);
            String r1 = MessageExchanger.razmeniPoruke(ctx, ps1Queue, req1, replyPS1);
                        System.out.println("PAY r1=" + r1);

            if (r1 == null || !r1.startsWith("OK|")) {
                return Response.status(400).entity("PS1:" + r1).build();
            }

            String[] a = r1.split("\\|");
            String address = a.length > 1 ? a[1] : "";
            String cityId = a.length > 2 ? a[2] : "0";
            String balanceStr = a.length > 3 ? a[3] : "0";
            HashMap<String, String> m2 = new HashMap<>();
            m2.put("userId", userId);
            ObjectMessage req2 = ctx.createObjectMessage(m2);
            req2.setIntProperty("redniBrojZahteva", 102);
            String r2 = MessageExchanger.razmeniPoruke(ctx, ps2Queue, req2, replyPS2);
            System.out.println("PAY r2=" + r2);

            if (r2 == null || !r2.startsWith("OK|")) {
                return Response.status(400).entity("PS2:" + r2).build();
            }

            String[] b = r2.split("\\|");
            String totalStr = b.length > 1 ? b[1] : "0";
            String items = b.length > 2 ? b[2] : "";
            long balance;
long total;
try {
    balance = Long.parseLong(balanceStr.trim());
    total = Math.round(Double.parseDouble(totalStr.trim()));
} catch (Exception e) {
    return Response.status(500).entity("Bad numbers from PS1/PS2").build();
}

if (total <= 0) return Response.status(400).entity("PS2: total<=0").build();
if (balance < total) return Response.status(400).entity("Not enough money. balance=" + balance + ", total=" + total).build();

// 1) SKINI NOVAC U PS1 (106)
HashMap<String,String> mDed = new HashMap<>();
mDed.put("userId", userId);
mDed.put("amount", String.valueOf(total));
ObjectMessage reqDed = ctx.createObjectMessage(mDed);
reqDed.setIntProperty("redniBrojZahteva", 107);

String rDed = MessageExchanger.razmeniPoruke(ctx, ps1Queue, reqDed, replyPS1);
if (rDed == null || !rDed.startsWith("OK|")) {
    return Response.status(400).entity("PS1 deduct failed: " + rDed).build();
}
            HashMap<String, String> m3 = new HashMap<>();
            m3.put("userId", userId);
            m3.put("address", address);
            m3.put("cityId", cityId);
            m3.put("total", String.valueOf(total));
            m3.put("items", items);
                        System.out.println("PAY -> PS3 map=" + m3);

            ObjectMessage req3 = ctx.createObjectMessage(m3);
            req3.setIntProperty("redniBrojZahteva", 14);
            String r3 = MessageExchanger.razmeniPoruke(ctx, ps3Queue, req3, replyPS3);
System.out.println("PAY r3=  " + r3);

            if (r3 == null) return Response.status(500).entity("PS3: null reply").build();
if (!r3.startsWith("OK:")) return Response.status(400).entity("PS3:" + r3).build();
            HashMap<String, String> m4 = new HashMap<>();
            m4.put("userId", userId);
            ObjectMessage req4 = ctx.createObjectMessage(m4);
            req4.setIntProperty("redniBrojZahteva", 103);
            String r4 = MessageExchanger.razmeniPoruke(ctx, ps2Queue, req4, replyPS2);
            System.out.println("PAY clearCart r4=" + r4);

            return Response.ok(r3).build();

        } catch (Exception ex) {
    ex.printStackTrace();
    return Response.status(500).entity("Pay.pay() EX: " + ex.getClass().getSimpleName() + " - " + ex.getMessage()).build();
}
    }
}
