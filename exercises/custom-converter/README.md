# Exercise 1: Implement a Custom Codec

During this exercise, you will:

- Output typical payloads from a Temporal Workflow using the default Data Converter
- Implement a Custom Codec that compresses Workflow output
- Enable a Failure Converter and demonstrate parsing its output

Make your changes to the code in the `practice` subdirectory (look for
`TODO` comments that will guide you to where you should make changes to
the code). If you need a hint or want to verify your changes, look at
the complete version in the `solution` subdirectory.

## Part A: Implement a Custom Codec

1. Defining a Custom Codec is a straightforward change to your existing Worker
   and Starter code. The example in the `practice` subdirectory of this exercise
   is missing the necessary change to use a Custom Codec, meaning that you can
   run it out of the box, and produce JSON output using the Default Data
   Converter. You'll do this first, so you have an idea of the expected output.
   First, compile the code:

   ```shell
   mvn clean compile
   ```

2. Next, start the Worker

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.ConverterWorker"
   ```

3. Next, run the Workflow starter:

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.Starter"
   ```

4. After that, you can use the `temporal` CLI to show the Workflow result:

   ```shell
   temporal workflow show -w converter-workflow
   ```

   ```
   Progress:
     ID           Time                     Type
       1  2024-08-26T20:28:51Z  WorkflowExecutionStarted
       2  2024-08-26T20:28:51Z  WorkflowTaskScheduled
       3  2024-08-26T20:28:51Z  WorkflowTaskStarted
       4  2024-08-26T20:28:51Z  WorkflowTaskCompleted
       5  2024-08-26T20:28:51Z  ActivityTaskScheduled
       6  2024-08-26T20:28:51Z  ActivityTaskStarted
       7  2024-08-26T20:28:51Z  ActivityTaskCompleted
       8  2024-08-26T20:28:51Z  WorkflowTaskScheduled
       9  2024-08-26T20:28:51Z  WorkflowTaskStarted
      10  2024-08-26T20:28:51Z  WorkflowTaskCompleted
      11  2024-08-26T20:28:51Z  WorkflowExecutionCompleted

   Results:
     Status          COMPLETED
     Result          "Received Plain text input"
     ResultEncoding  json/plain
   ```

   You should now have an idea of how this Workflow runs ordinarily — it outputs
   the string "Received Plain text input". In the next step, you'll add a Custom
   Data Converter.

5. To add a Custom Codec, you don't need to change anything in your
   Workflow code. You only need to add a `CodecDataConverter` parameter to
   `WorkflowClient client = WorkflowClient.newInstance(service);` where it is used
   in both `ConverterWorker.java` and `Starter.java`.
   1. Replace `WorkflowClient client = WorkflowClient.newInstance(service);` in
      both files with
   ```java
   WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
    .setDataConverter(
      new CodecDataConverter(
        DefaultDataConverter.newDefaultInstance(),
        Collections.singletonList(new CustomPayloadCodec())))
    .build());
   ```
6. Next, review `CustomPayloadCodec.java`. This contains the Custom Codec
   code you'll be using. The `encode()` method applies the `encodePayload()` method
   to each element in the payload. The `encodePayload()` method compresses the payload
   using Java's [snappy](https://github.com/google/snappy) codec, and sets the
   file metadata. The `decode()` and `decodePayload()` methods do the same thing,
   but in reverse. Add the missing calls to the `encode` method (you can use the
   `decode()` function as a hint).
7. Now you can re-run the Workflow with your Custom Codec.

   1. Stop your Worker (with `Ctrl+C` in a blocking terminal)
   1. Recompile your code

   ```shell
   mvn clean compile
   ```

   1. Restart the worker using

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.ConverterWorker"
   ```

   1. Rerun the workflow with

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.Starter"
   ```

   1. Finally, get the result again with

   ```shell
   temporal workflow show -w converter-workflow
   ```

   This time, your output will be encoded:

   ```shell
   ...
   Results:
      Status          COMPLETED
      Result          {"metadata":{"encoding":"YmluYXJ5L3NuYXBweQ=="},"data":"NdAKFgoIZW5jb2RpbmcSCmpzb24vcGxhaW4SGyJSZWNlaXZlZCBQbGFpbiB0ZXh0IGlucHV0Ig=="}
      ResultEncoding  binary/snappy
   ```

The `payload encoding is not supported` message is normal — the Temporal
Cluster itself can't use the `decode` method directly without a Codec
Server, which you'll create in the next exercise. In the meantime, you have
successfully customized a Data Converter, and in the next step, you'll
add more features to it.

## Part B: Implement a Failure Converter

1. The next feature you may add is a Failure Converter. To do this, you can override the
   default Failure Converter with a single additional parameter, `true`.
   1. Locate the `WorkflowClient.newInstance...` code you added in the previous exercise.
   2. Add the value `true` as the last value in the `new DataCodecConverter()` constructor.
   3. Do this in both `ConverterWorker.java` and `Starter.java`
2. To test your Failure Converter, change your Workflow to return an artificial
   error.

   1. Add the following import statement to your Workflow and code after executing your Activity in `ConverterWorkflowImpl.Java`.

   ```java
   import io.temporal.failure.ApplicationFailure;
   ...
   throw ApplicationFailure.newFailure("Artificial Error", "Artificial Error");
   ```

   2. Comment out all code after the `throw` statement as that code will
      become unreachable.
   3. Stop your worker using `Ctrl-C`
   4. Recompile your code

   ```shell
   mvn clean compile
   ```

   5. Restart the worker using

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.ConverterWorker"
   ```

   6. Rerun the workflow with

   ```shell
   mvn exec:java -Dexec.mainClass="customconverter.Starter"
   ```

   7. You should see a stack trace appear. This was expected. Stop the execution
      using `Ctrl-C`.

   8. Finally, get the result again with to get the status of your failed Workflow.

   ```shell
   temporal workflow show -w converter-workflow
   ```

   Notice that the `Failure:` field should now display an encoded
   result, rather than a plain text error:

   ```
   Progress:
     ID          Time                    Type
     1  2024-03-14T17:08:20Z  WorkflowExecutionStarted
     2  2024-03-14T17:08:20Z  WorkflowTaskScheduled
     3  2024-03-14T17:08:20Z  WorkflowTaskStarted
     4  2024-03-14T17:08:20Z  WorkflowTaskCompleted
     5  2024-03-14T17:08:20Z  ActivityTaskScheduled
     6  2024-03-14T17:08:20Z  ActivityTaskStarted
     7  2024-03-14T17:08:20Z  ActivityTaskCompleted
     8  2024-03-14T17:08:20Z  WorkflowTaskScheduled
     9  2024-03-14T17:08:20Z  WorkflowTaskStarted
    10  2024-03-14T17:08:20Z  WorkflowTaskCompleted
    11  2024-03-14T17:08:20Z  WorkflowExecutionFailed

   Results:
      Status   FAILED
      Failure
         Message: Encoded failure
   ```

### This is the end of the exercise.
