package customconverter;

import customconverter.codec.CustomPayloadCodec;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import java.util.Collections;

public class Starter {
  public static void main(String[] args) throws Exception {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
        .setDataConverter(
          new CodecDataConverter(
            DefaultDataConverter.newDefaultInstance(),
            Collections.singletonList(new CustomPayloadCodec()), true)) 
        .build());

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setWorkflowId("codecserver-workflow")
        .setTaskQueue("codecserver-tasks")
        .build();

    ConverterWorkflow workflow = client.newWorkflowStub(ConverterWorkflow.class, options);

    String input = "Plain text input";

    String greeting = workflow.run(input);

    System.out.printf("Workflow result: %s\n", greeting);
    System.exit(0);
  }
}
