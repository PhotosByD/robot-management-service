package si.photos.by.d.robot.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.photos.by.d.robot.models.entities.RobotPicture;
import si.photos.by.d.robot.services.beans.RobotBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Log
@ApplicationScoped
@Path("/robots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RobotResource {
    @Inject
    private RobotBean robotBean;

    @Context
    UriInfo uriInfo;

    @GET
    public Response getUsers() {
        List<RobotPicture> robotPictures = robotBean.getExistingPictures();
        return Response.ok(robotPictures).build();
    }

    @GET
    @Path("/{hash}")
    public Response getOrCreateRobotPicture(@PathParam("hash") String hash) {
        RobotPicture robotPicture = robotBean.getOrCreteRobotPicture(hash);

        if(robotPicture == null) {
            return  Response.serverError().build();
        }

        return Response.ok(robotPicture).build();
    }
}
