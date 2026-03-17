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
@Path("ps1/users")
public class UserCreate {
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private  ConnectionFactory connectionFactory;
    
    @Resource(lookup = "jms/podsistem1Queue")
    private  Queue podsistem1Queue;
    
    @Resource(lookup = "jms/centralReplyPS1")
    private  Queue centralReplyPS1;
    @Resource(lookup = "jms/podsistem2Queue")
    private  Queue podsistem2Queue;
    
    @Resource(lookup = "jms/centralReplyPS2")
    private  Queue centralReplyPS2;
    @Resource(lookup = "jms/podsistem3Queue")
    private  Queue podsistem3Queue;
    
    @Resource(lookup = "jms/centralReplyPS3")
    private  Queue centralReplyPS3;

    @POST
    public Response createUser(@FormParam("username") String username, @FormParam("password") String password,@FormParam("firstName") String firstName, @FormParam("lastName") String lastName, @FormParam("address") String address, 
                                     @FormParam("balance") String balance, @FormParam("cityId") String cityId) {
        try {
            //System.out.println("uso u kreirajMesto");
            JMSContext context = connectionFactory.createContext();
            
            HashMap<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            map.put("firstName", firstName);
            map.put("lastName", lastName);
            map.put("address", address);
            map.put("balance", balance);
            map.put("cityId", cityId);
            ObjectMessage objMsg = context.createObjectMessage(map);
            //System.out.println("Mesto:" + mesto);
            //System.out.println("email:" + email);
            objMsg.setIntProperty("redniBrojZahteva", 3);
            
            String odgovor = MessageExchanger.razmeniPoruke(context, podsistem1Queue, objMsg, centralReplyPS1);
if (odgovor == null || !odgovor.startsWith("OK|")) {
    return Response.status(400).entity(odgovor).build();
}
String[] parts = odgovor.split("\\|");
String userId = parts[1];
String usr = parts[2];

HashMap<String,String> m2 = new HashMap<>();
m2.put("userId", userId);
m2.put("username", usr);
ObjectMessage req2 = context.createObjectMessage(m2);
req2.setIntProperty("redniBrojZahteva", 104);
MessageExchanger.razmeniPoruke(context, podsistem2Queue, req2, centralReplyPS2);

HashMap<String,String> m3 = new HashMap<>();
m3.put("userId", userId);
m3.put("username", usr);
ObjectMessage req3 = context.createObjectMessage(m3);
req3.setIntProperty("redniBrojZahteva", 105);
MessageExchanger.razmeniPoruke(context, podsistem3Queue, req3, centralReplyPS3);
            return Response
                    .ok(odgovor)
                    .build();
        } catch (JMSException ex) {
            Logger.getLogger(UserCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR in UserCreate.createUser()").build();
    }
}
