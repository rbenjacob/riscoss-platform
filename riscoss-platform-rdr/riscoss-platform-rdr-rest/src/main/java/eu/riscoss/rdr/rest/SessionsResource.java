package eu.riscoss.rdr.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.riscoss.rdr.Utils;
import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.api.model.Session;

@Path("/sessions")
public class SessionsResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam(value = "offset") int offset,
        @QueryParam(value = "limit") @DefaultValue(value = "20") int limit)
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryProvider.getRiskDataRepository();

        List<Session> sessions = riskDataRepository.getSessions(offset, limit);

        /* Build the response representation */
        JsonObject response = new JsonObject();
        response.addProperty("offset", offset);
        response.addProperty("limit", limit);

        JsonArray sessionsArray = new JsonArray();
        for (Session session : sessions) {
            JsonObject sessionObject = new JsonObject();
            sessionObject.addProperty("id", session.getId());
            sessionObject.addProperty("startDate", session.getStartDate().getTime());
            if (session.getEndDate() != null) {
                sessionObject.addProperty("endDate", session.getEndDate().getTime());
            }
            sessionsArray.add(sessionObject);
        }

        response.add("results", sessionsArray);

        return Utils.getGson().toJson(response);
    }

    @POST
    public Response post()
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryProvider.getRiskDataRepository();

        Session session = riskDataRepository.createSession();

        /* Build the response representation */
        JsonObject response = new JsonObject();
        response.addProperty("id", session.getId());
        response.addProperty("date", session.getStartDate().getTime());

        return Response.created(UriBuilder.fromResource(SessionResource.class).build(session.getId())).build();
    }
}
