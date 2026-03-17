package endpoints.podsistem1;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("ps1/users")
public class UserUpdate {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String userId,
            @FormParam("address") String address,
            @FormParam("cityId") String cityId) {
        try {
            JMSContext context = connectionFactory.createContext();

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);
            map.put("address", address);
            map.put("cityId", cityId);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 5);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            return Response.ok(odgovor).build();

        } catch (JMSException ex) {
            Logger.getLogger(UserUpdate.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("ERROR in UserUpdate.update()").build();
        }
    }
}
