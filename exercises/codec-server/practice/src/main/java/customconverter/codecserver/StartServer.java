package customconverter.codecserver;

import java.io.IOException;
import java.util.Collections;

// TODO Add import for custom payload codec
//import customconverter.codec.CustomPayloadCodec;

import customconverter.codecserver.RDEHttpServer;

public class StartServer {

  public static void main(String[] args){
    try {
      System.out.println("Start...");
      new RDEHttpServer(Collections.singletonList(new CustomPayloadCodec()), 8081).start();
    }
    catch (IOException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
    }
  }
  
}
