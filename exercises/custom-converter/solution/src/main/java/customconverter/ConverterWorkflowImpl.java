package customconverter;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.failure.ApplicationFailure;

import org.slf4j.Logger;
import java.time.Duration;

public class ConverterWorkflowImpl implements ConverterWorkflow {

  public static final Logger logger = Workflow.getLogger(ConverterWorkflowImpl.class);

  private final ActivityOptions options =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .build();

  private final ConverterActivities activities =
      Workflow.newActivityStub(ConverterActivities.class, options);

  @Override
  public String run(String input) {
    logger.info("Converter workflow started: " + input);

    String result = activities.activity(input);

    throw ApplicationFailure.newFailure("This is an artificial error", "Artificial Error");

    //logger.info("Converter workflow completed: " + result);

    //return result;
    
  }
}
