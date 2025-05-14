package src;

import java.util.Arrays;
import javafx.application.Application;
import src.CLI.CLI;
import src.GUI.GUI;

public class Main 
{
    /**
     * Main method to run the application
     * Uses CLI if -cli is passed as an argument. Usage: java -jar IQPuzzlePro.jar -cli or ./gradlew run --args="-cli"
     * Otherwise, uses GUI. Usage: java -jar IQPuzzlePro.jar or ./gradlew run
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) 
    {
        if (Arrays.asList(args).contains("-cli")) 
        {
            CLI.main(args);
        } 
        else 
        {
            Application.launch(GUI.class, args);
        }
    }
}