/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints.podsistem2;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Lenovo
 */
@Path("ps2/items")

public class CreateItem {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue podsistem2Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue centralReplyPS2;

    @POST
    public Response createItem(@FormParam("name") String name, @FormParam("description") String description, @FormParam("price") String price, @FormParam("discount_pct") String discount_pct, @FormParam("categoryId") String categoryId,
            @FormParam("sellerId") String sellerId) {
        try {
            //System.out.println("uso u kreirajMesto");
            JMSContext context = connectionFactory.createContext();

            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("description", description);
            map.put("price", price);
            map.put("discount_pct", discount_pct);
            map.put("categoryId", categoryId);
            map.put("sellerId", sellerId);
            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 7);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem2Queue, objMsg, centralReplyPS2);

            return Response
                    .ok(odgovor)
                    .build();
        } catch (JMSException ex) {
            Logger.getLogger(CreateItem.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("GRESKA u CreateItem.createItem()").build();
    }

}
