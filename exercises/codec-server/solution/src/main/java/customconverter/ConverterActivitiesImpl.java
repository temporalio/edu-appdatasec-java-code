package customconverter;

public class ConverterActivitiesImpl implements ConverterActivities {

  @Override
  public String activity(String input) {
    return "Received " + input;    
  }

}
