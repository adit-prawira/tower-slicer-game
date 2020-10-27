import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class EventFileReader {
    private String filePath;
    private static final int MAX_DATA = 1000;
    private String[] inputEvents = new String[MAX_DATA];

    /**
     * Read and store waves.txt file
     *
     * @param filePath file directory
     */
    public EventFileReader(String filePath){this.filePath = filePath;}

    /**
     * Extract events from file logic
     */
    public String[] getEvents(){
        try(Scanner fileReader = new Scanner(new FileReader(filePath))) {
            inputEvents = inputFIle(fileReader);
        }catch(IOException e) {
            e.printStackTrace();
        }
        return inputEvents;
    }

    public String[] inputFIle(Scanner fileReader){
        String inputText = "";
        while (fileReader.hasNext()){
            inputText += fileReader.next() + " ";
        }
        inputEvents = inputText.split(" ");
        return inputEvents;

    }
}
