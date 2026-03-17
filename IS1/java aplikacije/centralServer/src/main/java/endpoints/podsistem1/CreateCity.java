/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints.podsistem1;

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
@Path("ps1/cities")
public class CreateCity {
        
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "jms/podsistem1Queue")
    private Queue podsistem1Queue;
    
    @Resource(lookup = "jms/centralReplyPS1")
    private Queue centralReplyPS1;
    
    @POST
    public Response createCity(@FormParam("name") String name) {
        try {
            //System.out.println("uso u kreirajMesto");
            JMSContext context = connectionFactory.createContext();
            
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            ObjectMessage objMsg = context.createObjectMessage(map);
            objMsg.setIntProperty("redniBrojZahteva", 2);
            
            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
            
            return Response
                    .ok(odgovor)
                    .build();
        } catch (JMSException ex) {
            Logger.getLogger(CreateCity.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR in CreateCity.createCity()").build();
    }
}
