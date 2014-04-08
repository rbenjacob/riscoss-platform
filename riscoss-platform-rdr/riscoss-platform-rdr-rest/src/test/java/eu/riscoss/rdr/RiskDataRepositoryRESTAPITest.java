package eu.riscoss.rdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
import com.google.gson.JsonObject;

import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.rest.RiskDataRepositoryProvider;

public class RiskDataRepositoryRESTAPITest
{
    private static final int PORT = 8888;

    private static final String BASE_URL = "http://localhost:" + PORT;

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

    @Test
    public void getSessionsTest() throws Exception
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(BASE_URL + "/sessions");
        CloseableHttpResponse response = client.execute(get);

        JsonObject jsonObject = gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);

        assertNotNull(jsonObject.get("offset"));
        assertNotNull(jsonObject.get("limit"));
        assertNotNull(jsonObject.get("results"));

    }

    public Header[] createSession() throws Exception
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:8888/sessions");
        post.setHeader("Content-type", MediaType.APPLICATION_JSON);
        CloseableHttpResponse response = client.execute(post);

        return (response.getHeaders("Location"));
    }

    @Test
    public void createSessionTest() throws Exception
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(BASE_URL + "/sessions");
        CloseableHttpResponse response = client.execute(post);

        Header[] headers = createSession();
        assertNotNull(headers);
    }

    private JsonArray getRiskData()
    {
        JsonArray array = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("id", "id");
        object.addProperty("target", "target");
        object.addProperty("type", "NUMBER");
        object.addProperty("value", "10");
        array.add(object);

        return array;
    }

    @Test
    public void postRiskDataTest() throws Exception
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();

        JsonArray riskData = getRiskData();

        Header[] headers = createSession();
        assertNotNull(headers);

        HttpPost post = new HttpPost(headers[0].getValue());
        post.setHeader("Content-type", MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(gson.toJson(riskData)));
        CloseableHttpResponse response = client.execute(post);

        assertEquals(response.getStatusLine().getStatusCode(), Status.ACCEPTED.getStatusCode());
    }

    @Test
    public void getSessionTest() throws Exception
    {
        Header[] headers = createSession();
        assertNotNull(headers);

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpGet get = new HttpGet(headers[0].getValue());
        CloseableHttpResponse response = client.execute(get);

        JsonObject jsonObject = gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);

        assertNotNull(jsonObject.get("id"));
        assertNotNull(jsonObject.get("startDate"));
        assertNotNull(jsonObject.get("open"));
    }

    @Test
    public void postRiskDataToClosedSession() throws Exception
    {
        Header[] headers = createSession();
        assertNotNull(headers);

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpDelete delete = new HttpDelete(headers[0].getValue());
        CloseableHttpResponse response = client.execute(delete);

        assertEquals(response.getStatusLine().getStatusCode(), Status.ACCEPTED.getStatusCode());

        JsonArray riskData = getRiskData();

        HttpPost post = new HttpPost(headers[0].getValue());
        post.setHeader("Content-type", MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(gson.toJson(riskData)));
        response = client.execute(post);

        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatusLine().getStatusCode());
    }

    @Test
    public void getLastClosedSession() throws Exception
    {
        Header[] headers = createSession();
        assertNotNull(headers);

        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpDelete delete = new HttpDelete(headers[0].getValue());
        CloseableHttpResponse response = client.execute(delete);

        assertEquals(response.getStatusLine().getStatusCode(), Status.ACCEPTED.getStatusCode());

        HttpGet get = new HttpGet(BASE_URL + "/sessions/lastclosed");
        response = client.execute(get);

        JsonObject jsonObject = gson.fromJson(IOUtils.toString(response.getEntity().getContent()), JsonObject.class);

        assertTrue(headers[0].getValue().contains(jsonObject.get("id").getAsString()));

        get = new HttpGet(BASE_URL + "/sessions/lastclosed/data");
        response = client.execute(get);

        assertEquals(Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }
}
