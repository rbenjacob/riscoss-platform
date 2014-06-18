/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.riscoss.rdr;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.rest.RiskDataRepositoryProvider;

import static org.junit.Assert.assertEquals;

public class RiskDataRepositoryRESTAPITest
{
    final String TARGET = "target";

    private static final int PORT = 8888;

    private static final String BASE_URL = "http://localhost:" + PORT + "/";

    private static Server server;

    private static Gson gson;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        RiskDataRepository riskDataRepository = RiskDataRepositoryFactory.create(new HibernateSessionProviderImpl());
        RiskDataRepositoryProvider.setRiskDataRepository(riskDataRepository);

        server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        ServletHolder holder = new ServletHolder(new HttpServletDispatcher());
        holder.setInitParameter("javax.ws.rs.Application", "eu.riscoss.rdr.RiskDataRepositoryApplication");
        context.addServlet(holder, "/*");
        server.setHandler(context);
        server.start();

        gson = new Gson();
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
        server.stop();
    }

    private JsonArray getRiskData()
    {
        JsonArray array = new JsonArray();

        JsonObject object = new JsonObject();
        object.addProperty("id", "id");
        object.addProperty("target", TARGET);
        object.addProperty("type", "NUMBER");
        object.addProperty("value", 10d);

        array.add(object);

        object = new JsonObject();
        object.addProperty("id", "id2");
        object.addProperty("target", TARGET);
        object.addProperty("type", "EVIDENCE");
        JsonElement evidenceArray = gson.toJsonTree(new double[]{ 1.0d, 2.0d });
        object.add("value", evidenceArray);

        array.add(object);

        return array;
    }

    @Test
    public void postAndGetRiskDataTest() throws Exception
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();

        JsonArray riskData = getRiskData();

        HttpPost post = new HttpPost(BASE_URL);
        post.setHeader("Content-type", MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(gson.toJson(riskData)));
        CloseableHttpResponse response = client.execute(post);

        assertEquals(response.getStatusLine().getStatusCode(), Status.ACCEPTED.getStatusCode());

        HttpGet get = new HttpGet(BASE_URL + TARGET);
        response = client.execute(get);

        assertEquals(response.getStatusLine().getStatusCode(), Status.OK.getStatusCode());

        JsonObject jsonObject = gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);

        JsonArray riskDataArray = jsonObject.getAsJsonArray("results");

        assertEquals(2, riskDataArray.size());

        get = new HttpGet(BASE_URL + TARGET + "?id=id2");
        response = client.execute(get);

        assertEquals(response.getStatusLine().getStatusCode(), Status.OK.getStatusCode());

        jsonObject = gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);

        riskDataArray = jsonObject.getAsJsonArray("results");

        assertEquals(1, riskDataArray.size());
    }
}
