package mentormate.json;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
 
public class MentormateJson
{
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException
   {
        // Declare parser, writer and file  instancse
        JSONParser parser = new JSONParser();
        String outputFilePath = System.getProperty("user.home") + "/Desktop" + "/Result"; 
        File file = new File(outputFilePath);  
        FileWriter outputfile = new FileWriter(file); 
        CSVWriter writer = new CSVWriter(outputfile); 
        Object obj;
        
        // Decraling variables to store the contents of the report file
        boolean useExprienceMultiplier = false;
        long topPerformersThreshold = 0;
        long periodLimit = 0;
        
        // Declaring variables to store the contents of the data file
        String name;
        long totalSales;
        long salesPeriod;
        double exp;
        
        // Declaring variables which get derived from the pervious ones
        double score;
        long numberOfEmployees = 0;
        long topPerformers;
     
        // Declaring a hashmap which stores evey name together with their score
        HashMap<String, Double> HMap = new LinkedHashMap<>();      
        
        // Writing the first line of the Result file
        String[] header = { "Name", " Score"}; 
        writer.writeNext(header, false);
        
        try {
        
        // Parse the file whose path is the second string value entered in the command line into a JSONObject
        obj = parser.parse(new FileReader(args[1]));
        JSONObject jsonObject = (JSONObject) obj;
                
                // Store the contents of the object in the defined variables
                topPerformersThreshold = (Long) jsonObject.get("topPerformersThreshold");
                useExprienceMultiplier = (boolean) jsonObject.get("useExprienceMultiplier");
                periodLimit = (Long) jsonObject.get("periodLimit");
       
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            
            //Parse the file whose path is the first string value entered in the command line into a JSONArray
            obj = parser.parse(new FileReader(args[0]));
            JSONArray jsonObjects =  (JSONArray) obj;
            
            
            for (Object o : jsonObjects) {
                JSONObject jsonObject = (JSONObject) o;
                
                //Store each array element's atributes in the defined variables
                name = (String) jsonObject.get("name");
                totalSales = (Long) jsonObject.get("totalSales");
                salesPeriod = (Long) jsonObject.get("salesPeriod");
                exp = (Double) jsonObject.get("experienceMultiplier");
       
               //Derive the score for each element
               if (useExprienceMultiplier) 
                    score = totalSales / salesPeriod * exp;
               else 
                   score = totalSales/salesPeriod;
               
               // Store each element in a hashmap whose score is above the desired limit
               if (salesPeriod >= periodLimit) {                  
                HMap.put(name, score);            
               }              
               
               // Keep track of how many elements (employees) we have
               numberOfEmployees++;
               }
            
            // Compute the number of employeese to write in the file based on the entered percentage
            topPerformers = Math.round((numberOfEmployees * topPerformersThreshold) / 100.0);
            int numberOfTopPerformers = Math.round(topPerformers);

            // In cases with a low number of employees in the data file, set number of top performers to 1 to output the top performer
            if (numberOfTopPerformers == 0)
                numberOfTopPerformers = 1;
            
            // Write the top performers from the Hashmap into the file
            writeTopScores(HMap, numberOfTopPerformers, writer);
               
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } 
    }
   
    public static void writeTopScores(HashMap <String, Double> HMap, int numberOfTopScores, CSVWriter writer) throws IOException {
        
     Map.Entry<String, Double> maxEntry = null;
     String [] line = new String[2];
     
     // Loop for the number of top performeres we need to write
     for (int i = 0; i < numberOfTopScores; i++) 
        {
            // Find the max value for each top performer
            for (Map.Entry<String, Double> entry : HMap.entrySet())
            {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)               
                    maxEntry = entry;                    
            }
            
            // Write the name and score of the top performer into the file and set his score to 0 (so that he is ignored in the next loop)
            line[0] = maxEntry.getKey();
            line[1] = " " + maxEntry.getValue().toString();
            writer.writeNext(line, false);
            maxEntry.setValue(0.0);
        }
     writer.close(); 
 }   
}