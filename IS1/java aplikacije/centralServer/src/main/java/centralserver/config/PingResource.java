/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralserver.config;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Lenovo
 */
@Path("ping")
@RequestScoped
public class PingResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PingResource
     */
    public PingResource() {
    }

    /**
     * Retrieves representation of an instance of rs.as.bg.etf.is1.centralserver.PingResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
    return "OK";
}

    /**
     * PUT method for updating or creating an instance of PingResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
