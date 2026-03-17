package endpoints.podsistem1;

import centralserver.config.MessageExchanger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("ps1/users/roles")
public class GetAllUserRoles {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    @GET
    public Response getRoles(@QueryParam("userId") String userId) {
        try (JMSContext context = connectionFactory.createContext()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("userId", userId);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 106); 

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(GetAllUserRoles.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error in GetAllUserRoles.getRoles()").build();
        }
    }
}