package src;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class cs21b059_parser {
    private static String prev = "null";
    private static int numAttributes = -1;
    private static int prevAttributeNum = -1;
    private static String prevTableName = "null";
    private static ArrayList<String> dataTypesList = new ArrayList<String>(Arrays.asList("int", "float", "string", "date"));  

    private static String getIntermediateCode(String code){
        String[] lexemes = code.split("\\s+");
        if(lexemes.length <= 1) return "Error: Unrecognizable Instruction";
        
        // create table <tableName> <numAttributes>
        if(lexemes[0].equals("create") && lexemes[1].equals("table") && lexemes.length == 4){
            prev = "Create Table";
            String intCode = "create_table " + lexemes[2] + " " + lexemes[3];

            if(prevAttributeNum < numAttributes){
                return "Error: Insufficient number of attributes given for " + prevTableName;
            }
            
            try{
                numAttributes = Integer.parseInt(lexemes[3]);
                prevAttributeNum = -1;
                prevTableName = lexemes[2];
            } catch (NumberFormatException e){
                return "Error: Integer value must be given";
            }

            return intCode;
        } 
        
        // <dataType> <value>
        else if(lexemes.length == 2 && dataTypesList.contains(lexemes[0]) && prev.equals("Create Table")){
            String intCode = "add_attribute " + prevTableName + " " + lexemes[0] + " " + lexemes[1];
            prevAttributeNum = prevAttributeNum == -1 ? 1 : prevAttributeNum + 1;

            if(prevAttributeNum > numAttributes){
                return "Error: Received more attributes than specified for " + prevTableName;
            }
            else if(prevAttributeNum == numAttributes){
                // reset the values
                prevAttributeNum = -1;
                numAttributes = -1;
                prevTableName = "null";
                prev = "null";
            }

            return intCode;
        }

        // adding attribute without creating a table
        else if(lexemes.length == 2 && dataTypesList.contains(lexemes[0]) && !prev.equals("Create Table")){
            return "Error: Table not created";
        }

        // insert into <tableName> <values>
        else if(lexemes[0].equals("insert") && lexemes[1].equals("into")){
            lexemes = code.split("\\s+", 4); // create 4 lexemes
            // remove all whitespaces
            String values = lexemes[3].replaceAll("\\s+", "");
            String intCode = "insert_into " + lexemes[2] + " " + values;
            return intCode;
        }

        // select * from <tableName>
        else if(lexemes[0].equals("select") && lexemes[1].equals("*") && lexemes[2].equals("from") && lexemes.length == 4){
            String intCode = "select " + lexemes[3];
            return intCode;
        } 
        
        // join <table1name> <table2name> where <condition>
        else if(lexemes[0].equals("join")){
            lexemes = code.split("\\s+", 5); // create 5 lexemes
            String intCode = "join " + lexemes[1] + " " + lexemes[2] + " " + lexemes[4];
            return intCode;
        }

        else{
            return "Error: Unrecognizable instruction";
        }
    }
    public static void main(String[] args) {

        String queryFileName = "joinTables";
        
        File fr = new File("test/" + queryFileName + ".query");
        try{
            BufferedReader br = new BufferedReader(new FileReader(fr));
            String inst;
            int lineNum = 1;

            // generate intermediate code
            try{
                FileWriter fw = new FileWriter("test/" + queryFileName + ".query.code");
                while ((inst = br.readLine()) != null) {
                    String intCode = getIntermediateCode(inst);
                    if(intCode.substring(0, 5).equals("Error")){
                        System.out.println(intCode + " on line " + lineNum);
                        return;
                    }
                    lineNum++;
                    try{
                        fw.write(intCode + System.lineSeparator());
                    } catch (IOException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
                    }
                }
                br.close();
                fw.close();
            } catch(IOException e){
                System.out.println(e.getMessage());
                return;
            }
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
            return;
        }

        // process the intermediate code
        fr = new File("test/" + queryFileName + ".query.code");
        cs21b059_dbengine dbe = new cs21b059_dbengine();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fr));
            String intCode;

            try{
                while ((intCode = br.readLine()) != null) {
                    dbe.process(intCode);
                }
                br.close();
            } catch(IOException e){
                System.out.println(e.getMessage());
                return;
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
            return;
        }
    }
}
