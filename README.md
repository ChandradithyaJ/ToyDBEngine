# ToyDBEngine
[Demo Video](https://www.youtube.com/watch?v=w1IdHESAFAE)

Currently supporting the following operations:
  - create table {tableName}
  - {dataType} {fieldName} (to add attributes to the created table; example, string name)
  - insert into {tableName} {values} (values must be in this format: (a, b, c), i.e., comma-separated and within parenthesis, following the order of the field names)
  - select * from {tableName} (display all the rows in the table chosen)
  - join {table1Name} {table2Name} where {condition}

## Other features
Modifiable buffer size to reduce the number of records loaded into RAM at once

## Tests
In the test folder, query files are present which are read and converted to intermediate code (.query.code files).

Change the ```queryFileName``` variable in cs21b059_parser.java to a query file of your choice.

```cs21b059.query``` is to create two tables with random values.
```joinTables.query``` is to perform a join operation with a condition.

## Running
Clone the repo to your local machine and run the runDBEngine shell file by going to the root directory and typing ```./runDBEngine.sh``` in the terminal.