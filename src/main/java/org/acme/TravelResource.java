package org.acme;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RolesAllowed("user")
@Path("/travel")
public class TravelResource {
  @Inject
  Cluster cluster;
  @ConfigProperty(name = "query")
  String query;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<JsonObject> run() {
    // Get a reference to a particular Couchbase bucket and its default collection
    var list = new ArrayList<JsonObject>();
    // Perform a N1QL query
    var queryResult = cluster.query(query);
    queryResult.rowsAsObject().forEach(row -> {
      list.add(row.getObject("travel-sample"));
      System.out.println(row);
    });
    return list;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("marriotts")
  public List<Hotel> getMarriotts() {

    var travel = cluster.bucket("travel-sample");
    var inventory = travel.scope("inventory");

    var queryResult = inventory.query("SELECT hotel.* FROM hotel WHERE name LIKE \"Marriott%\"");
    List<Hotel> hotels = queryResult.rowsAs(Hotel.class);

    return hotels;

  }

}
