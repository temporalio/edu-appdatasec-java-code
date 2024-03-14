package customconverter;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

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

    // TODO Part B: Add the artificial error here.

    logger.info("Converter workflow completed: " + result);

    return result;
    
  }
}
