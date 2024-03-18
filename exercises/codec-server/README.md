# Exercise 2: Deploy a Codec Server and Integrate with the Web UI

During this exercise, you will:

- Review a Codec Server implementation
- Configure a Codec Server to share your custom converter logic
- Enable CORS and review other deployment parameters
- Integrate your Codec Server with the Temporal Web UI
- Securely return decoded results in the CLI and the Web UI

Make your changes to the code in the `practice` subdirectory (look for
`TODO` comments that will guide you to where you should make changes to
the code). If you need a hint or want to verify your changes, look at
the complete version in the `solution` subdirectory.

## Part A: Configure a Codec Server to Use Your Data Converter

1. First, you'll review a Codec Server implementation in Java, and make
   the necessary changes to integrate the custom data converter from Exercise 1.
   Examine the following things:

   1. Open `codecserver/AbstractRemoteDataEncoder.java`
      1. This file contains constants used for creating the routes, appropriate
         metadata, as well as objects for handling the conversion of Protobufs into
         JSON and vice-versa.
   1. Open `codecserver/DataEncoderHandler.java`
      1. This file handles processing the request, determining if the data should
         be encoded or decoded, calling the appropriate methods to process the data,
         handle constructing the appropriate Payload, and transmitting that payload.
   1. Open `codecserver/RDEHttpServer.java`
      1. This file creates and handles the routes for the HTTP server. The server
         crafts the paths using the data found in `AbstractRemoteDataEncoder.java`.
         It creates an `/encode` and `/decode` route for the given namespaces.
   1. `codecserver/StartServer.java`
      1. This file initializes and starts the codec server.

   These are the baseline requirements for a Temporal Codec Server, which can be
   implemented using standard HTTP functionality in any language of your choosing.

2. Temporal Codec Servers need, at minimum, one additional configuration detail
   before they can be deployed from sample code. Specifically, Codec Servers
   need to import the Converter logic from your own application, and then map
   the Converter logic on a per-Namespace basis. Uncomment the `import` block at
   the top of `codecserver/StartServer.java` to import your custom payload codec.
3. Next, create write a loop to iterate over the `NAMESPACES` variable in
   `codecserver/AbstractRemoteDataEncoderCodec` to craft the routes to each
   namespace in the list. By default, you only need to create the routes for the
   `default` namespace from this example.
   1. Hint: Here is how you would create the route if you were hardcoding the routes.
   ```java
      server.createContext("/default/encode", new DataEncoderHandler(codecs));
   ```
4. After making these additions, you should have a functioning Codec Server,
   integrated with your application logic. Again, everything else in here is
   configured as generically as possible — note that this example Codec Server
   listens on port 8081, which is usually used in testing configurations — but
   this fulfills all the requirements of a Temporal Codec Server, and you could
   incorporate any other authentication requirements on top of HTTP as needed.
   Perform the next steps to run your Codec Server:
   1. Compile your code
   ```bash
   mvn clean compile
   ```
   1. Run your server using mvn:
   ```bash
   mvn exec:java -Dexec.mainClass="customconverter.codecserver.StartServer"
   ```
   This will block the terminal it runs in, and await connections.
5. Now you can retrieve the decoded output of your Workflow Execution from the
   previous Exercise. From another terminal window, run the following:

   ```
   temporal workflow show \
      -w converters_workflowID \
      --codec-endpoint 'http://localhost:8081/{namespace}'`
   ```

   It should retain the same Event History as before, with the decoded result
   appended to the output:

   ```
   ...
   Result:
     Status: COMPLETED
     Output: ["Received Plain text input"]
   ```

   You now have a working Codec Server implementation. In the following steps,
   you'll learn how to integrate it more closely with a Temporal Cluster for
   production environments.

## Part B: Enable CORS and Configure Temporal Web UI Integration

1. The next step is to enable Codec Server integration with the Temporal Web UI.
   This isn't necessary if you don't plan to use the Web UI to view your
   Workflow output, but it provides a stock example of how to integrate Codec
   Server requests into a web app, and is supported by Temporal Cloud. Without
   Codec Server integration, the Temporal Web UI cannot decode output, and
   results are displayed encoded:

   ![Encoded Workflow Output in Web UI](images/encoded-output.png)

   To do this, you first need to enable
   [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing), a common
   HTTP feature for securely making cross-domain requests.
   `codecserver/DataEncoderHandler.java` contains a method call to `exchange.getResponseHeaders().add()`
   which will add the necessary headers to your HTTP requests to support CORS,
   but it is currently disabled. Uncomment these lines to enable CORS.

2. Now you can proceed to integrate your Codec Server with the Web UI. You
   should already have a local Temporal Cluster running that you can access in a
   browser at `http://localhost:8080` by default. In the top-right corner of the
   Web UI, you should see a 3D glasses icon, where you can access the Codec
   Server settings:

   ![Codec Server settings icon](images/configure-codec-server-button.png)

   In the Codec Server settings menu, add the path to your Codec Server, which
   should be `http://localhost:8081/default` by default. You do not need to toggle the
   user access token settings if you aren't using authentication.

   ![Codec Server settings](images/codec-server-settings.png)

   Note that you can toggle the "Use Cluster-level setting" option to save this
   Codec Server for all users of this cluster, or only for you, which would be
   especially relevant if you were running a `localhost` Codec Server with a
   remote Temporal Cluster. Click the "Apply" button. The 3D glasses in the
   top nav should now be colorized, indicating a successful connection:

   ![Codec Server enabled](images/codec-server-enabled.png)

3. When you navigate back to your Workflow History and scroll to the "Input
   and Results" section, you should find your payload automatically decoded by
   your Codec Server:

   ![Decoded Workflow Output in Web UI](images/decoded-output.png)

   You now have a working Codec Server integration with the Temporal Web UI.

### This is the end of the exercise.
