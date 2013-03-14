import cflow.PreProcessor;

public class Main {
  public static void main(String[] args) {
    PreProcessor myFile = new PreProcessor(args[1]);
    String input = myFile.read_file(args[0]);
    System.out.println(input);
    myFile.write_new_file(args[0].substring(0, args[0].lastIndexOf('.'))+"_cflow.java", input);
  }
}