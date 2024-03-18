package customconverter;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface ConverterWorkflow {

  @WorkflowMethod
  String run(String input);

}
