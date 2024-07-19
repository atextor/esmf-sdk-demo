package com.example;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.mockserver.api.PartAsPlannedApiMockServer;

public class PartAsPlannedApiMockTest {
   private final int port = 2345;
   private WireMockServer server;

   @BeforeEach
   void setup() {
      server = new WireMockServer( options().port( port ) );
      server.stubFor( PartAsPlannedApiMockServer.stubGetPartAsPlanned200(
            PartAsPlannedApiMockServer.getPartAsPlanned200ResponseSample1() ) );
      server.start();
   }

   @AfterEach
   void teardown() {
      server.stop();
      server.resetAll();
   }

   @Test
   void testPartAsPlannedApiMock() throws URISyntaxException, IOException, InterruptedException {
      final HttpRequest request = HttpRequest
            .newBuilder( new URI( "http://localhost:" + port + "/part-as-planned" ) )
            .headers( "Accept", "application/json" )
            .GET().build();
      final HttpClient httpClient = HttpClient.newHttpClient();
      final HttpResponse<String> response = httpClient
            .send( request, HttpResponse.BodyHandlers.ofString() );

      // Render the received JSON
      final ObjectMapper mapper = new ObjectMapper();
      final Object jsonResponse = mapper.readValue( response.body(), Object.class );
      System.out.println( "Received from mock server:" );
      System.out.println( mapper.writerWithDefaultPrettyPrinter().writeValueAsString( jsonResponse ) );
   }
}
