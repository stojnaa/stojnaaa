/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import centralserver.config.MessageExchanger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Stefan
 */
@Provider
public class BasicAuthFilter implements ContainerRequestFilter {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory cf;

    @Resource(lookup = "jms/podsistem1Queue")
    private Queue ps1Queue;

    @Resource(lookup = "jms/centralReplyPS1")
    private Queue replyPS1;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
            String path = uriInfo.getPath();
            System.out.println("PATH: "+path);
            if (path == null) {
                path = "";
            }
            if (path.contains("ps1/users/login")) {
                            System.out.println("NISTA: ");
                return;
            }
        List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");
        if (authHeaderValues == null || authHeaderValues.isEmpty()) {
    abort(requestContext, Response.Status.UNAUTHORIZED, "Posaljite kredencijale.");
    return;
}
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            System.out.println("PASS: "+password+" USR"+username);
            String userId;
            try (JMSContext ctx = cf.createContext()) {
                HashMap<String, String> m = new HashMap<>();
                m.put("username", username);
                m.put("password", password);

                ObjectMessage req = ctx.createObjectMessage(m);
                req.setIntProperty("redniBrojZahteva", 1);

                String r = MessageExchanger.razmeniPoruke(ctx, ps1Queue, req, replyPS1);
                if (r == null || !r.startsWith("OK|")) {
                    abort(requestContext, Response.Status.UNAUTHORIZED, "Username/password not valid.");
                    return;
                }

                String[] parts = r.trim().split("\\|");
                if (parts.length < 3) {
                    abort(requestContext, Response.Status.UNAUTHORIZED, "Bad login reply format.");
                    return;
                }

                userId = parts[1];

                if (path.contains("/ps1/") || path.startsWith("ps1/")) {
                    HashMap<String, String> mRoles = new HashMap<>();
                    mRoles.put("userId", userId);

                    ObjectMessage reqRoles = ctx.createObjectMessage(mRoles);
                    reqRoles.setIntProperty("redniBrojZahteva", 106);

                    String rr = MessageExchanger.razmeniPoruke(ctx, ps1Queue, reqRoles, replyPS1);
                    if (rr == null || !rr.startsWith("OK|")) {
                        abort(requestContext, Response.Status.UNAUTHORIZED, "Cannot read roles.");
                        return;
                    }
                    String rolesCsv = rr.length() >= 3 ? rr.substring(3) : "";
                    boolean isAdmin = false;
                    for (String role : rolesCsv.split(",")) {
                        if ("ADMIN".equalsIgnoreCase(role.trim())) {
                                                        System.out.println("ISADMIIN: ");
                            isAdmin = true;
                            break;
                        }
                    }
                                                                            System.out.println("ADMIIN: "+isAdmin);
                    if (!isAdmin) {
                        abort(requestContext, Response.Status.FORBIDDEN, "Admin privileges required for PS1.");
                        return;
                    }
                }
            
            return;

        } catch (JMSException ex) {
            Logger.getLogger(BasicAuthFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String msg) {
        ctx.abortWith(Response.status(status).entity(msg).build());
    }

}
