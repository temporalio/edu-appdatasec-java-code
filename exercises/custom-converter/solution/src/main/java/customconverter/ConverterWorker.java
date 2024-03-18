package customconverter;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import java.util.Collections;

import customconverter.codec.CustomPayloadCodec;

public class ConverterWorker {

  public static void main(String[] args) {
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder()
        .setDataConverter(
            new CodecDataConverter(
                DefaultDataConverter.newDefaultInstance(),
                Collections.singletonList(new CustomPayloadCodec()), true))
        .build());

    WorkerFactory factory = WorkerFactory.newInstance(client);

    Worker worker = factory.newWorker("converter-tasks");

    worker.registerWorkflowImplementationTypes(ConverterWorkflowImpl.class);

    worker.registerActivitiesImplementations(new ConverterActivitiesImpl());

    factory.start();
  }
}
