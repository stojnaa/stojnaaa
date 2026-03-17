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

@Path("ps2/wishlist/add/{userId}")
public class AddItemToWishlist {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue podsistem2Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue centralReplyPS2;

    @POST
    public Response add(@PathParam("userId") String userId,
            @FormParam("itemId") String itemId) {
        try (JMSContext context = connectionFactory.createContext()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);
            map.put("itemId", itemId);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 12);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem2Queue, objMsg, centralReplyPS2);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(AddItemToWishlist.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("GRESKA u AddItemToWishlist.add()").build();
        }
    }
}
