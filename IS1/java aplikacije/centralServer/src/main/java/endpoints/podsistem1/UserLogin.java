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
import javax.ws.rs.core.Response;

@Path("ps1/users/login")
public class UserLogin {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;

    @POST
    public Response login(@FormParam("username") String username,
            @FormParam("password") String password) {
        try {
            JMSContext context = connectionFactory.createContext();

            HashMap<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);

            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 1);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            return Response.ok(odgovor).build();

        } catch (JMSException ex) {
            Logger.getLogger(UserLogin.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("ERROR in:UserLogin.login()").build();
        }
    }
}
