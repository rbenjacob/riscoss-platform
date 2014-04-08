package eu.riscoss.rdr.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.riscoss.rdr.SessionClosedException;
import eu.riscoss.rdr.Utils;
import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.api.model.RiskData;
import eu.riscoss.rdr.api.model.RiskDataType;
import eu.riscoss.rdr.api.model.Session;

@Path("/sessions/{id}")
public class SessionResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(value = "id") String id, @QueryParam(value = "offset") int offset,
        @QueryParam(value = "limit") @DefaultValue(value = "20") int limit)
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryProvider.getRiskDataRepository();

        Session session = null;
        if ("lastclosed".compareToIgnoreCase(id) == 0) {
            List<Session> sessions = riskDataRepository.getClosedSessions(0, 1);
            if (sessions.size() > 0) {
                session = sessions.get(0);
            }
        } else {
            session = riskDataRepository.getSession(id);
        }

        if (session == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        /* Build response */
        JsonObject response = new JsonObject();
        response.addProperty("id", session.getId());
        response.addProperty("startDate", session.getStartDate().getTime());
        Date endDate = session.getEndDate();
        if (endDate != null) {
            response.addProperty("endDate", endDate.getTime());
        }
        response.addProperty("open", session.isOpen());

        return Response.ok(Utils.getGson().toJson(response)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@PathParam(value = "id") String id, String body)
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryProvider.getRiskDataRepository();

        Session session = riskDataRepository.getSession(id);
        if (session == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        JsonArray riskDataArray = null;
        try {
            riskDataArray = Utils.getGson().fromJson(body, JsonArray.class);
        } catch (Exception e) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }

        for (int i = 0; i < riskDataArray.size(); i++) {
            JsonObject riskDataObject = riskDataArray.get(i).getAsJsonObject();

            RiskData riskData = new RiskData();

            if (riskDataObject.get("id") != null) {
                riskData.setId(riskDataObject.get("id").getAsString());
            }

            if (riskDataObject.get("target") != null) {
                riskData.setTarget(riskDataObject.get("target").getAsString());
            }

            riskData.setSession(session);
            riskData.setDate(new Date());

            if (riskDataObject.get("type") != null) {
                riskData.setType(RiskDataType.valueOf(riskDataObject.get("type").getAsString().toUpperCase()));
            }

            if (riskDataObject.get("value") != null) {
                riskData.setValue(riskDataObject.get("value").getAsString());
            }

            try {
                riskDataRepository.storeRiskData(session, riskData);
            } catch (SessionClosedException e) {
                return Response.status(Status.CONFLICT).build();
            }
        }

        return Response.status(Status.ACCEPTED).build();
    }

    @DELETE
    public Response delete(@PathParam(value = "id") String id)
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryProvider.getRiskDataRepository();

        Session session = riskDataRepository.getSession(id);
        if (session == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        riskDataRepository.closeSession(session);

        return Response.status(Status.ACCEPTED).build();
    }

}
