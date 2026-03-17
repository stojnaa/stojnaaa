package endpoints.podsistem1;

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

@Path("ps1/users")
public class UserMoney {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    @POST
    @Path("{id}/money")
    public Response addMoney(@PathParam("id") String userId,
            @FormParam("amount") String amount) {
        try {
            JMSContext context = connectionFactory.createContext();

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);
            map.put("amount", amount);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 4);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            return Response.ok(odgovor).build();

        } catch (JMSException ex) {
            Logger.getLogger(UserMoney.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("ERROR in UserMoney.addMoney()").build();
        }
    }
}
