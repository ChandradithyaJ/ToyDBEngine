package src;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

class Table{
    public String tableName; 
    public ArrayList<String> attributes = new ArrayList<>();
    public Hashtable<String, String> types = new Hashtable<>(); // attribute - type mapping
    public ArrayList<Hashtable<String, String>> rows = new ArrayList<>();
}

public class cs21b059_dbengine{
    private Table t;

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

    private Table load_table(File fileName){
        Table load = new Table();
        load.tableName = fileName.getName();

        if(fileName.length() == 0) return load;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int numLines = 0;
            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) return load; // empty file

                if (numLines == 0) {
                    String[] values = line.split("[;-]+");
                    for (int i = 0; i < values.length; i += 2) {
                        load.attributes.add(values[i]);
                        load.types.put(values[i], values[i+1]);
                    }
                } else {
                    String[] values = line.split(";");
                    Hashtable<String, String> row = new Hashtable<>();
                    for (int i = 0; i < values.length; i++) {
                        row.put(load.attributes.get(i), values[i]);
                    }
                    load.rows.add(row);
                }
                numLines++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        System.out.println("Loaded table " + load.tableName.substring(0, load.tableName.length()-4) + " from disk");
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

        System.out.println("Saved table " + table.tableName.substring(0, table.tableName.length() - 4) + " to disk");
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
            return;
        }

        Hashtable<String, String> newRow = new Hashtable<>();
        for(int i = 0; i < t.attributes.size(); i++) newRow.put(t.attributes.get(i), newRowValues[i]);
        t.rows.add(newRow);

        System.out.println("Inserted to table " + t.tableName.substring(0, t.tableName.length() - 4));
    }

    private void select(Table table){
        // display the fields first, and then the rows
        for (String attr : table.attributes) System.out.print(attr + "\t|");
        System.out.println();

        for (int i = 0; i < table.rows.size(); i++) {
            for (String attr : table.attributes) System.out.print(table.rows.get(i).get(attr) + "\t|");
            System.out.println();
        }
    }

    private void join_tables(String tab1, String tab2, String condition){
        File f1 = new File("db/" + tab1 + ".txt");
        File f2 = new File("db/" + tab2 + ".txt");
        Table t1 = load_table(f1);
        Table t2 = load_table(f2);

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

        // insert rows into the joined table to display
        for(Hashtable<String, String> h1 : t1.rows){
            for(Hashtable<String, String> h2 : t2.rows){
                if(evalCondition(t1.tableName, t2.tableName, t1.types, t2.types, h1, h2, condition)){
                    Hashtable<String, String> newRow = new Hashtable<>();
                    for(String attr : h1.keySet()) newRow.put(tab1 + "." + attr, h1.get(attr));
                    for (String attr : h2.keySet()) newRow.put(tab2 + "." + attr, h2.get(attr));
                    t.rows.add(newRow);
                }
            }
        }

        // display
        select(t);
        // reset
        t = new Table(); 
    }

    public void process(String inst){
        String[] args = inst.split(" ");
        if(args[0].equals("create_table")) {
            create_table(args[1]);
            File fileName = new File("db/" + args[1] + ".txt");
            t = load_table(fileName);
        }
        else if(args[0].equals("add_attribute")) {
            add_attribute(args[2], args[3]);
            save_table(t);
        }
        else if(args[0].equals("insert_into")) {
            File fileName = new File("db/" + args[1] + ".txt");
            t = load_table(fileName);
            insert_into(t, args[2]);
            save_table(t);
            t = new Table(); // reset
        }
        else if(args[0].equals("select")) {
            File fileName = new File("db/" + args[1] + ".txt");
            t = load_table(fileName);
            select(t);
            t = new Table(); // reset
        }
        else if(args[0].equals("join")){
            args = inst.split(" ", 4);
            // args = [<join>, <tab1>, <tab2>, <condition>]
            join_tables(args[1], args[2], args[3]);
        }
    }
}