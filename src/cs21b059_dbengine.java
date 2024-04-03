package src;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

class Table{
    public int bufferSize = 30; // max number of rows to load into the table
    public String tableName; 
    public ArrayList<String> attributes = new ArrayList<>();
    public Hashtable<String, String> types = new Hashtable<>(); // attribute-type mapping
    public ArrayList<Hashtable<String, String>> rows = new ArrayList<>();
}

public class cs21b059_dbengine{
    private Table t;
    // number of lines read from the file; helps when bufferSize < number of lines in the file
    private int linesRead = 0;
    // false when file isn't read completely
    private boolean fileReadComplete = false;

    private boolean evalCondition(String tname1, String tname2, Hashtable<String, String> types1, Hashtable<String, String> types2, Hashtable<String, String> h1, Hashtable<String, String> h2, String condition){
        String[] tokens = condition.split(" ");
        tname1 = tname1.substring(0, tname1.length()-4);
        tname2 = tname2.substring(0, tname2.length()-4);

        // currently only supporting simple '==' operations
        if(tokens[1].equals("==")){
            String[] lhs = tokens[0].split("\\.");
            String[] rhs = tokens[2].split("\\.");

            // check for valid table names
            if (lhs[0].equals(tname1)) {
                if (rhs[0].equals(tname1)) {
                    // check for type mismatch
                    if (types1.get(lhs[1]).equals(types1.get(rhs[1]))) {
                        String type = types1.get(lhs[1]);
                        switch (type) {
                            case "int":
                                if (Integer.parseInt(h1.get(lhs[1])) == Integer.parseInt(h1.get(rhs[1])))
                                    return true;
                            case "string":
                                if (h1.get(lhs[1]).equals(h1.get(rhs[1])))
                                    return true;
                            case "date":
                                if (h1.get(lhs[1]).equals(h1.get(rhs[1])))
                                    return true;
                            case "float":
                                if (Float.parseFloat(h1.get(lhs[1])) == Float.parseFloat(h1.get(rhs[1])))
                                    return true;
                        }
                    }
                } else if (rhs[0].equals(tname2)) {
                    // check for type mismatch
                    if (types1.get(lhs[1]).equals(types2.get(rhs[1]))) {
                        String type = types1.get(lhs[1]);
                        switch (type) {
                            case "int":
                                if (Integer.parseInt(h1.get(lhs[1])) == Integer.parseInt(h2.get(rhs[1])))
                                    return true;
                            case "string":
                                if (h1.get(lhs[1]).equals(h2.get(rhs[1])))
                                    return true;
                            case "date":
                                if (h1.get(lhs[1]).equals(h2.get(rhs[1])))
                                    return true;
                            case "float":
                                if (Float.parseFloat(h1.get(lhs[1])) == Float.parseFloat(h2.get(rhs[1])))
                                    return true;
                        }
                    }
                }
            } else if (lhs[0].equals(tname2)) {
                if (rhs[0].equals(tname1)) {
                    // check for type mismatch
                    if (types2.get(lhs[1]).equals(types1.get(rhs[1]))) {
                        String type = types2.get(lhs[1]);
                        switch (type) {
                            case "int":
                                if (Integer.parseInt(h2.get(lhs[1])) == Integer.parseInt(h1.get(rhs[1])))
                                    return true;
                            case "string":
                                if (h2.get(lhs[1]).equals(h1.get(rhs[1])))
                                    return true;
                            case "date":
                                if (h2.get(lhs[1]).equals(h1.get(rhs[1])))
                                    return true;
                            case "float":
                                if (Float.parseFloat(h2.get(lhs[1])) == Float.parseFloat(h1.get(rhs[1])))
                                    return true;
                        }
                    }
                } else if (rhs[0].equals(tname2)) {
                    // check for type mismatch
                    if (types2.get(lhs[1]).equals(types2.get(rhs[1]))) {
                        String type = types2.get(lhs[1]);
                        switch (type) {
                            case "int":
                                if (Integer.parseInt(h2.get(lhs[1])) == Integer.parseInt(h2.get(rhs[1])))
                                    return true;
                            case "string":
                                if (h2.get(lhs[1]).equals(h2.get(rhs[1])))
                                    return true;
                            case "date":
                                if (h2.get(lhs[1]).equals(h2.get(rhs[1])))
                                    return true;
                            case "float":
                                if (Float.parseFloat(h2.get(lhs[1])) == Float.parseFloat(h2.get(rhs[1])))
                                    return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Table load_table(File fileName, boolean attrOnly){
        Table load = new Table();
        load.tableName = fileName.getName();

        if(fileName.length() == 0) return load; // empty file

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int numLines = 0;
            while ((line = reader.readLine()) != null) {
                if (numLines == 0) {
                    String[] values = line.split("[;-]+");
                    for (int i = 0; i < values.length; i += 2) {
                        load.attributes.add(values[i]);
                        load.types.put(values[i], values[i+1]);
                    }
                    if(attrOnly) return load;
                } else {
                    if(numLines > linesRead){
                        String[] values = line.split(";");
                        Hashtable<String, String> row = new Hashtable<>();
                        for (int i = 0; i < values.length; i++) {
                            row.put(load.attributes.get(i), values[i]);
                        }
                        load.rows.add(row);
                    }
                }
                numLines++;

                // first iteration, one extra line read for field names
                if((numLines-1-linesRead == load.bufferSize && linesRead == 0) || (numLines-linesRead == load.bufferSize && linesRead != 0)){
                    linesRead = numLines;
                    break;
                }
            }
            
            // EOF
            if((line = reader.readLine()) == null){
                fileReadComplete = true;
                linesRead = 0;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return load;
    }

    private void save_table(Table table){
        try (FileWriter writer = new FileWriter("db/" + table.tableName)) {
            // field names as the header
            String header = "";
            for(String attr : table.attributes) header += attr + "-" + table.types.get(attr) + ";";
            writer.write(header);

            for(int i = 0; i < table.rows.size(); i++){
                String row = "";
                for(String attr : table.attributes) row += table.rows.get(i).get(attr) + ";";
                writer.write(System.lineSeparator());
                writer.write(row);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error inserting to table: " + e.getMessage());
            return;
        }
    }

    private void create_table(String tableName){
        File tableFile = new File("db/" + tableName + ".txt");

        if(!tableFile.exists()){
            try{
                tableFile.createNewFile();
            } catch(IOException e){
                System.err.println("Error creating table: " + e.getMessage());
                return;
            }
            System.out.println("Created table " + tableName);
        } else{
            System.out.println("Table " + tableName + " already exists");
        }
    }

    private void add_attribute(String dataType, String fieldName){
        t.attributes.add(fieldName);
        t.types.put(fieldName, dataType);

        System.out.println("Added field " + fieldName + " to table " + t.tableName.substring(0, t.tableName.length()-4));
    }

    private void insert_into(Table table, String values){
        values = values.substring(1, values.length()-1);
        String[] newRowValues = values.split(",");

        if(newRowValues.length != table.attributes.size()){
            System.out.println("Trying to insert unequal values to attributes in " + t.tableName.substring(0, t.tableName.length() - 4) + " - " + values);
            for(int i = 0; i < 100000; i++){}
            return;
        }

        try (FileWriter writer = new FileWriter("db/" + table.tableName, true)) {
            String newRow = "";
            for(String value : newRowValues) newRow += value + ";";
            writer.write(System.lineSeparator());
            writer.write(newRow);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error inserting to table: " + e.getMessage());
            return;
        }

        System.out.println("Inserted to table " + t.tableName.substring(0, t.tableName.length() - 4));
    }

    private void select(Table table, boolean fields){
        if((linesRead == 0 && !fileReadComplete) || fields){
            // display the fields first, and then the rows
            for (String attr : table.attributes)
                System.out.print(attr + "\t|");
            System.out.println();
        }

        for (int i = 0; i < table.rows.size(); i++) {
            for (String attr : table.attributes) System.out.print(table.rows.get(i).get(attr) + "\t|");
            System.out.println();
        }
    }

    private void join_tables(String tab1, String tab2, String condition){
        File f1 = new File("db/" + tab1 + ".txt");
        File f2 = new File("db/" + tab2 + ".txt");
        Table t1 = load_table(f1, true);
        Table t2 = load_table(f2, true);

        boolean printFieldNames = true;

        t = new Table(); // to store the joined table
        // create the attributes and map their types for the joined table
        for(String attr : t1.attributes){
            t.attributes.add(tab1 + "." + attr);
            t.types.put(tab1 + "." + attr, t1.types.get(attr));
        }
        for(String attr : t2.attributes) {
            t.attributes.add(tab2 + "." + attr);
            t.types.put(tab2 + "." + attr, t2.types.get(attr));
        }

        boolean table1FileReadComplete = false;
        int table1LinesRead = 0;
        while(!table1FileReadComplete){
            t1 = load_table(f1, false);
            // store file pointer locally
            table1LinesRead = linesRead;
            table1FileReadComplete = fileReadComplete;

            fileReadComplete = false;
            linesRead = 0;

            while(!fileReadComplete){
                // load t2 and process the join until t2 has been read completely
                t2 = load_table(f2, false);
                // insert rows into the joined table to display
                for (Hashtable<String, String> h1 : t1.rows) {
                    for (Hashtable<String, String> h2 : t2.rows) {
                        if (evalCondition(t1.tableName, t2.tableName, t1.types, t2.types, h1, h2, condition)) {
                            Hashtable<String, String> newRow = new Hashtable<>();
                            for (String attr : h1.keySet())
                                newRow.put(tab1 + "." + attr, h1.get(attr));
                            for (String attr : h2.keySet())
                                newRow.put(tab2 + "." + attr, h2.get(attr));
                            t.rows.add(newRow);

                            // if buffer full, display and dump
                            if(t.rows.size() == t.bufferSize){
                                select(t, printFieldNames);
                                t.rows = new ArrayList<>();
                            }
                        }
                    }
                }
            }

            linesRead = table1LinesRead;
            fileReadComplete = table1FileReadComplete;
        }

        // display the rest
        select(t, printFieldNames);
        // reset
        t = new Table(); 
    }

    public void process(String inst){
        String[] args = inst.split(" ");
        if(args[0].equals("create_table")) {
            create_table(args[1]);
            File fileName = new File("db/" + args[1] + ".txt");
            t = load_table(fileName, false);
        }
        else if(args[0].equals("add_attribute")) {
            add_attribute(args[2], args[3]);
            save_table(t);
        }
        else if(args[0].equals("insert_into")) {
            File fileName = new File("db/" + args[1] + ".txt");
            t = load_table(fileName, true);
            insert_into(t, args[2]);
            t = new Table(); // reset
        }
        else if(args[0].equals("select")) {
            File fileName = new File("db/" + args[1] + ".txt");
            while(!fileReadComplete){
                t = load_table(fileName, false);
                select(t, false);
            }
            fileReadComplete = false; // reset boolean
            t = new Table(); // reset table instance
        }
        else if(args[0].equals("join")){
            args = inst.split(" ", 4);
            // args = [<join>, <tab1>, <tab2>, <condition>]
            join_tables(args[1], args[2], args[3]);
        }
    }
}