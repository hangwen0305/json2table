# json2table

A Java-based tool that converts JSON-formatted data into table format.

## Basic Usage

```
Table table = JsonToTableConverter.toTable(jsonStr);
String markdown = table.toMarkdown();
```

You can output the markdown to a markdown file or access the table instance programmatically.

For information about the table instance, see [here](#table).


## Let's look at some examples

### Single JSON Object

```json
{
  "firstName": "John",
  "lastName": "doe",
  "age": 26
}
```

Markdown output table:

| firstName | lastName | age |
|-----------|----------|-----|
| John      | doe      | 26  |

### JSON Array

```json
[
  {
    "firstName": "John",
    "lastName": "doe",
    "age": 26
  },
  {
    "firstName": "Jane",
    "lastName": "doe",
    "age": 24
  }
]
```

Markdown output table:

| firstName | lastName | age |
|-----------|----------|-----|
| John      | doe      | 26  |
| Jane      | doe      | 24  |

#### If the array schema is inconsistent

```json
[
  {
    "firstName": "John",
    "lastName": "doe",
    "age": 26
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "birthday": "1994-04-02"
  }
]
```

Markdown output table:

| firstName | lastName | age | birthday   |
|-----------|----------|-----|------------|
| John      | doe      | 26  |            |
| Jane      | doe      |     | 2000-04-02 |

The table will aggregate all fields, and if a field does not exist in an object, it will be represented as null.

Subsequent examples will be displayed as arrays, but single objects are also supported.

### JSON Object with Nested Objects

```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "age": 26,
    "address": {
      "streetAddress": "Naist Street",
      "city": "Nara",
      "postalCode": "630-0192"
    }
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "birthday": "1994-04-02",
    "address": {
      "streetAddress": "Cherry Lane",
      "city": "Kyoto",
      "postalCode": "600-8000"
    }
  },
  {
    "firstName": "Alice",
    "lastName": "Brown",
    "age": 28,
    "address": {
      "streetAddress": "Sunset Boulevard",
      "city": "Osaka",
      "postalCode": "530-0011"
    }
  }
]

```

Markdown output table:

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | birthday   |
|-----------|----------|-----|-----------------------|--------------|--------------------|------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           |            |
| Jane      | Smith    |     | Cherry Lane           | Kyoto        | 600-8000           | 1994-04-02 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           |            |

Properties in the address field will be expanded to the top level. If there are multiple levels of nested objects, they will also be expanded.

### JSON Object with Arrays

```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "age": 26,
    "address": {
      "streetAddress": "Naist Street",
      "city": "Nara",
      "postalCode": "630-0192"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0123-4567-8888"
      },
      {
        "type": "home",
        "number": "0123-4567-8910"
      }
    ]
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "age": 30,
    "address": {
      "streetAddress": "Cherry Lane",
      "city": "Kyoto",
      "postalCode": "600-8000"
    },
    "phones": [
      {
        "type": "Android",
        "number": "0987-6543-2211"
      },
      {
        "type": "work",
        "number": "0987-6543-2212"
      }
    ]
  },
  {
    "firstName": "Alice",
    "lastName": "Brown",
    "age": 28,
    "address": {
      "streetAddress": "Sunset Boulevard",
      "city": "Osaka",
      "postalCode": "530-0011"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0765-4321-5566"
      },
      {
        "type": "home",
        "number": "0765-4321-5567"
      }
    ]
  }
]

```

Markdown output table:

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | phones                                                                                   |
|-----------|----------|-----|-----------------------|--------------|--------------------|------------------------------------------------------------------------------------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | [{"type":"iPhone","number":"0123-4567-8888"},{"type":"home","number":"0123-4567-8910"}]  |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | [{"type":"Android","number":"0987-6543-2211"},{"type":"work","number":"0987-6543-2212"}] |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | [{"type":"iPhone","number":"0765-4321-5566"},{"type":"home","number":"0765-4321-5567"}]  |

At this point, the phones field is a JSON array string.

If we want to expand the phones field, we need to do this:

```
Table table = JsonToTableConverter.toTable(jsonStr, "$.phones");
String markdown = table.toMarkdown();
```

Markdown output table:

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | phones.type | phones.number  |
|-----------|----------|-----|-----------------------|--------------|--------------------|-------------|----------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | iPhone      | 0123-4567-8888 |
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | home        | 0123-4567-8910 |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | Android     | 0987-6543-2211 |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | work        | 0987-6543-2212 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | iPhone      | 0765-4321-5566 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | home        | 0765-4321-5567 |

"$.phones" is similar to a JSONPath selector, but phones must be an array type; otherwise, the code will throw an IllegalStateException.
At this point, the table will expand to all objects 'phones' sub-objects, similar to a left join result of top-level objects 
and phone objects.

#### If there are multiple sibling array fields
We cannot expand multiple sibling arrays in a single table. We can only specify one array through the selector parameter.

```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "age": 26,
    "address": {
      "streetAddress": "Naist Street",
      "city": "Nara",
      "postalCode": "630-0192"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0123-4567-8888"
      },
      {
        "type": "home",
        "number": "0123-4567-8910"
      }
    ],
    "emails": [
      "john.doe@example.com",
      "j.doe@example.com"
    ]
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "age": 30,
    "address": {
      "streetAddress": "Cherry Lane",
      "city": "Kyoto",
      "postalCode": "600-8000"
    },
    "phones": [
      {
        "type": "Android",
        "number": "0987-6543-2211"
      },
      {
        "type": "work",
        "number": "0987-6543-2212"
      }
    ],
    "emails": [
      "jane.smith@example.com",
      "j.smith@example.com"
    ]
  },
  {
    "firstName": "Alice",
    "lastName": "Brown",
    "age": 28,
    "address": {
      "streetAddress": "Sunset Boulevard",
      "city": "Osaka",
      "postalCode": "530-0011"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0765-4321-5566"
      },
      {
        "type": "home",
        "number": "0765-4321-5567"
      }
    ],
    "emails": [
      "alice.brown@example.com",
      "a.brown@example.com"
    ]
  }
]

```

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | emails | phones.type | phones.number |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | ["john.doe@example.com","j.doe@example.com"] | iPhone | 0123-4567-8888 |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | ["john.doe@example.com","j.doe@example.com"] | home | 0123-4567-8910 |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | ["jane.smith@example.com","j.smith@example.com"] | Android | 0987-6543-2211 |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | ["jane.smith@example.com","j.smith@example.com"] | work | 0987-6543-2212 |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | ["alice.brown@example.com","a.brown@example.com"] | iPhone | 0765-4321-5566 |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | ["alice.brown@example.com","a.brown@example.com"] | home | 0765-4321-5567 |

Since "emails" field is specified as "collapsed," only the "phones" field will be "expanded."

### Recursive Support

If your JSON has deep levels, it is supported.

```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "age": 26,
    "address": {
      "streetAddress": "Naist Street",
      "city": "Nara",
      "postalCode": "630-0192"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0123-4567-8888",
        "details": [
          {
            "description": "Primary contact number",
            "verified": true
          },
          {
            "description": "In use since 2018",
            "verified": true
          }
        ]
      },
      {
        "type": "home",
        "number": "0123-4567-8910",
        "details": [
          {
            "description": "Home landline",
            "verified": false
          },
          {
            "description": "Secondary contact number",
            "verified": false
          }
        ]
      }
    ]
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "age": 30,
    "address": {
      "streetAddress": "Cherry Lane",
      "city": "Kyoto",
      "postalCode": "600-8000"
    },
    "phones": [
      {
        "type": "Android",
        "number": "0987-6543-2211",
        "details": [
          {
            "description": "Personal mobile number",
            "verified": true
          },
          {
            "description": "In use since 2019",
            "verified": true
          }
        ]
      },
      {
        "type": "work",
        "number": "0987-6543-2212",
        "details": [
          {
            "description": "Work contact number",
            "verified": true
          },
          {
            "description": "For office hours",
            "verified": true
          }
        ]
      }
    ]
  },
  {
    "firstName": "Alice",
    "lastName": "Brown",
    "age": 28,
    "address": {
      "streetAddress": "Sunset Boulevard",
      "city": "Osaka",
      "postalCode": "530-0011"
    },
    "phones": [
      {
        "type": "iPhone",
        "number": "0765-4321-5566",
        "details": [
          {
            "description": "Personal contact number",
            "verified": true
          },
          {
            "description": "In use since 2020",
            "verified": true
          }
        ]
      },
      {
        "type": "home",
        "number": "0765-4321-5567",
        "details": [
          {
            "description": "Family landline",
            "verified": false
          },
          {
            "description": "Used for emergencies",
            "verified": false
          }
        ]
      }
    ]
  }
]
```

```
Table table = JsonToTableConverter.toTable(jsonStr, "$.phones.details");
String markdown = table.toMarkdown();
```
Set the expansion path to "$.phones.details"

Markdown output table:

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | phones.type | phones.number | phones.details.description | phones.details.verified |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | iPhone | 0123-4567-8888 | Primary contact number | true |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | iPhone | 0123-4567-8888 | In use since 2018 | true |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | home | 0123-4567-8910 | Home landline | false |
| John | Doe | 26 | Naist Street | Nara | 630-0192 | home | 0123-4567-8910 | Secondary contact number | false |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | Android | 0987-6543-2211 | Personal mobile number | true |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | Android | 0987-6543-2211 | In use since 2019 | true |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | work | 0987-6543-2212 | Work contact number | true |
| Jane | Smith | 30 | Cherry Lane | Kyoto | 600-8000 | work | 0987-6543-2212 | For office hours | true |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | iPhone | 0765-4321-5566 | Personal contact number | true |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | iPhone | 0765-4321-5566 | In use since 2020 | true |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | home | 0765-4321-5567 | Family landline | false |
| Alice | Brown | 28 | Sunset Boulevard | Osaka | 530-0011 | home | 0765-4321-5567 | Used for emergencies | false |



## <span id='table'>Table</span>

Introducing how to access the table instance programmatically. 

### Get Table Headers

```
final List<TableHeader> headers = table.getHeaders();

for (TableHeader header : headers) {
    System.out.println(header.getAlias());
}
```

### Get Row Data

```
final List<TableRow> rows = table.getRows();

//get the first row as example
final TableRow row = rows.get(0);

//get the first cell of the first row
Object value = row.getValue(0);
value = row.getValue(headers.get(0));

//iterate over the cells of the first row
for (TableRow.Cell cell : row) {
    System.out.println(cell.getValue());
}
```

How else would you like to use the Table instance, as List<List<Object>>, or as a CSV format string? You can submit an issue.

