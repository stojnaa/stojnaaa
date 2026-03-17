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
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Lenovo
 */
@Path("ps2/categories")
public class GetAllCategories {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/podsistem2Queue")
    private Queue podsistem1Queue;

    @Resource(lookup = "jms/centralReplyPS2")
    private Queue centralReplyPS2;

    @GET
    public Response getAllCategories() {
        try (JMSContext context = connectionFactory.createContext()) {

            ObjectMessage objMsg = context.createObjectMessage(new HashMap<String, String>());
            objMsg.setIntProperty("redniBrojZahteva", 17);

            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS2);
            return Response.ok(odgovor).build();

        } catch (Exception ex) {
            Logger.getLogger(GetAllCategories.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("GRESKA u GetAllCategories.getAllCategories()").build();
        }
    }
}
