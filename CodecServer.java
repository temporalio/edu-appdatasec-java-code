package customconverter;

import java.io.IOException;

import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.http.MediaType;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import io.temporal.payload.codec.PayloadCodec;

import java.util.Map;
import java.util.HashMap;

public class CodecServer {

  // port number where this service will listen for incoming HTTP requests
  public static final int PORT_NUMBER = 8081;

  // IP address to which the service will be bound. Using a value of 0.0.0.0
  // will make it available on all available interfaces, but you could use
  // 127.0.0.1 to restrict it to the loopback interface
  public static final String SERVER_IP = "0.0.0.0";

  public static void main(String[] args) throws IOException {
    // Start the service on the specified IP address and port
    On.address(SERVER_IP).port(PORT_NUMBER);

    // Set codecs per namespace here.
    // Only handle codecs for the "default" namespace in this example.
    Map<String, PayloadCodec> codecs = new HashMap<>();
    codecs.put("default", new CustomPayloadCodec());

    codecs.forEach((namespace, codec) -> {
      On.post("/" + namespace + "/encode").json((Req req) -> {
        Resp resp = req.response();
        resp.contentType(MediaType.JSON);
        Map<String, String> headers = new HashMap();
        resp.headers().put("ACCESS_CONTROL_ALLOW_ORIGIN", req.uri());
        resp.headers().put("ACCESS_CONTROL_ALLOW_METHODS", "POST");
        resp.headers().put("ACCESS_CONTROL_ALLOW_HEADERS", "content-type,x-namespace");
        return resp;
      });
      //On.post("/" + namespace + "/decode").json(codec.decode());
    });

    // Returning the request or response object means the response was constructed
On.get("/").html((Req req) -> {
  Resp resp = req.response();
  resp.contentType(MediaType.JSON);
  resp.result("hello");
  return resp;
});
    // Also define a catch-all to return an HTTP 404 Not Found error if the URL
    // path in the request didn't match an endpoint defined above. It's essential
    // that this code remains at the end.
    On.req(
        (req, resp) -> {
          String message = String.format("Error: Invalid endpoint address '%s'", req.path());
          return req.response().result(message).code(404);
        });
  }

}
