package be.dabla.boot.grizzly.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello")
public interface HelloResource {
    @GET
    @Path("{response}")
    String hello(@PathParam("response") String response);
}
