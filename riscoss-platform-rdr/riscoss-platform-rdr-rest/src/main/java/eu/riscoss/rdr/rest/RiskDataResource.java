package eu.riscoss.rdr.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.riscoss.rdr.Utils;
import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.api.model.RiskData;
import eu.riscoss.rdr.api.model.Session;

@Path("/sessions/{id}/data")
public class RiskDataResource
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

        List<RiskData> riskData = riskDataRepository.getRiskData(session, offset, limit);

        /* Build response */
        JsonObject response = new JsonObject();
        response.addProperty("id", session.getId());
        response.addProperty("startDate", session.getStartDate().getTime());
        Date endDate = session.getEndDate();
        if (endDate != null) {
            response.addProperty("endDate", endDate.getTime());
        }
        response.addProperty("open", session.isOpen());
        
        JsonObject riskDataObject = new JsonObject();
        
        riskDataObject.addProperty("offset", offset);
        riskDataObject.addProperty("limit", limit);

        JsonArray results = new JsonArray();
        for (RiskData rd : riskData) {
            JsonObject object = new JsonObject();
            object.addProperty("id", rd.getId());
            object.addProperty("target", rd.getTarget());
            object.addProperty("date", rd.getDate().getTime());
            object.addProperty("type", rd.getType().toString());
            object.addProperty("value", rd.getValue().toString());

            results.add(object);
        }
        
        riskDataObject.add("results", results);

        response.add("riskData", riskDataObject);

        return Response.ok(Utils.getGson().toJson(response)).build();
    }

}
