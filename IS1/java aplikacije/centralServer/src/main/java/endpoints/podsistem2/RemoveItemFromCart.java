package endpoints.podsistem2;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("ps2/cart/remove/{userId}")
public class RemoveItemFromCart {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue podsistem2Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue centralReplyPS2;

    @POST
    public Response remove(@PathParam("userId") String userId,
            @FormParam("itemId") String itemId,
            @FormParam("quantity") String quantity) {
        try (JMSContext context = connectionFactory.createContext()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);
            map.put("itemId", itemId);
            map.put("quantity", quantity);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 11);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem2Queue, objMsg, centralReplyPS2);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(RemoveItemFromCart.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("GRESKA u RemoveItemFromCart.remove()").build();
        }
    }
}
