package endpoints.podsistem2;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("ps2/items/price")
public class UpdateItemPrice {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue podsistem2Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue centralReplyPS2;

    @PUT
    public Response updatePrice(@FormParam("userId") String userId,@FormParam("itemId") String itemId,
            @FormParam("price") String price) {
        try (JMSContext context = connectionFactory.createContext()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);
            map.put("itemId", itemId);
            map.put("price", price);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 8);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem2Queue, objMsg, centralReplyPS2);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(UpdateItemPrice.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("GRESKA u UpdateItemPrice.updatePrice()").build();
        }
    }
}
