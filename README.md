# ToyDBEngine
[Demo Video](https://www.youtube.com/watch?v=w1IdHESAFAE)

Currently supporting the following operations:
  - create table {tableName}
  - {dataType} {fieldName} (to add attributes to the created table; example, string name)
  - insert into {tableName} {values} (values must be in this format: (a, b, c), i.e., comma-separated and within parenthesis, following the order of the field names)
  - select * from {tableName} (display all the rows in the table chosen)
  - join {table1Name} {table2Name} where {condition}

Modifiable buffer size to reduce the number of records loaded into RAM at once

Run cs21b059_parser.java to execute.