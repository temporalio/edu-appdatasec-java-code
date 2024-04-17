package customconverter;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ConverterActivities {

  String activity(String input);

}
