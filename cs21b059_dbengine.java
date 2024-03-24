import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

class Table{
    public String tableName; 
    public ArrayList<String> attributes = new ArrayList<>();
    public Hashtable<String, String> types = new Hashtable<>();
    public ArrayList<Hashtable<String, String>> rows = new ArrayList<>();
}

public class cs21b059_dbengine{
    private Table t;

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
        try (FileWriter writer = new FileWriter(table.tableName)) {
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
        File tableFile = new File(tableName + ".txt");

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
            System.out.println("Trying to insert unequal values than attributes in " + t.tableName.substring(0, t.tableName.length() - 4) + " - " + values);
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

    public void process(String inst){
        String[] args = inst.split(" ");
        if(args[0].equals("create_table")) {
            create_table(args[1]);
            File fileName = new File(args[1] + ".txt");
            t = load_table(fileName);
        }
        else if(args[0].equals("add_attribute")) {
            add_attribute(args[2], args[3]);
            save_table(t);
        }
        else if(args[0].equals("insert_into")) {
            File fileName = new File(args[1] + ".txt");
            t = load_table(fileName);
            insert_into(t, args[2]);
            save_table(t);
            t = new Table(); // reset
        }
        else if(args[0].equals("select")) {
            File fileName = new File(args[1] + ".txt");
            t = load_table(fileName);
            select(t);
            t = new Table(); // reset
        }
    }
}