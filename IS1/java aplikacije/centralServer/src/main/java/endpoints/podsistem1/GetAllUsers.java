package endpoints.podsistem1;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("ps1/users")
public class GetAllUsers {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    @GET
    public Response getAllUsers() {
        try (JMSContext context = connectionFactory.createContext()) {

            ObjectMessage objMsg = context.createObjectMessage(new HashMap<String, String>());
            objMsg.setIntProperty("redniBrojZahteva", 16);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(GetAllUsers.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("ERROR in GetAllUsers.getAllUsers()").build();
        }
    }
}
