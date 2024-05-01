# Exercise 1: Implement a Custom Codec

During this exercise, you will:

- Output typical payloads from a Temporal Workflow using the default Data Converter
- Implement a Custom Codec that compresses Workflow output
- Enable a Failure Converter and demonstrate parsing its output

Make your changes to the code in the `practice` subdirectory (look for
`TODO` comments that will guide you to where you should make changes to
the code). If you need a hint or want to verify your changes, look at
the complete version in the `solution` subdirectory.

### GitPod Environment Shortcuts

If you are executing the exercises in the provided GitPod environment, you
can take advantage of certain aliases to aid in navigation and execution of
the code.

| Command | Action                                                                                                                          |
| :------ | :------------------------------------------------------------------------------------------------------------------------------ |
| `ex1`   | Change to Exercise 1 Practice Directory                                                                                         |
| `ex1s`  | Change to Exercise 1 Solution Directory                                                                                         |
| `ex1w`  | Execute the Exercise 1 Worker. Must be within the appropriate directory for this to succeed. (either `practice` or `solution`)  |
| `ex1st` | Execute the Exercise 1 Starter. Must be within the appropriate directory for this to succeed. (either `practice` or `solution`) |

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
   temporal workflow show -w converters_workflowID
   ```

   ```
   Progress:
     ID          Time                     Type
      1  2024-03-14T16:01:21Z  WorkflowExecutionStarted
      2  2024-03-14T16:01:21Z  WorkflowTaskScheduled
      3  2024-03-14T16:01:21Z  WorkflowTaskStarted
      4  2024-03-14T16:01:21Z  WorkflowTaskCompleted
      5  2024-03-14T16:01:21Z  ActivityTaskScheduled
      6  2024-03-14T16:01:21Z  ActivityTaskStarted
      7  2024-03-14T16:01:21Z  ActivityTaskCompleted
      8  2024-03-14T16:01:21Z  WorkflowTaskScheduled
      9  2024-03-14T16:01:21Z  WorkflowTaskStarted
     10  2024-03-14T16:01:21Z  WorkflowTaskCompleted
     11  2024-03-14T16:01:21Z  WorkflowExecutionCompleted

   Result:
     Status: COMPLETED
     Output: ["Received Plain text input"]
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
   Result:
     Status: COMPLETED
     Output: [encoding binary/snappy: payload encoding is not supported]
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

   1. Add the following code after executing your Activity.

   ```java
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

   Result:
     Status: FAILED
     Failure: &Failure{Message:Encoded failure,Source:JavaSDK,StackTrace:,Cause:nil,FailureType:Failure_ApplicationFailureInfo,}
   ```

### This is the end of the exercise.
