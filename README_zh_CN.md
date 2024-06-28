# json2table

基于java的工具，可以将一段JSON格式的数据转换为表格格式。

## 基本用法

```
Table table = JsonToTableConverter.toTable(jsonStr);
String markdown = table.toMarkdown();
```

我们可以将`markdown`输出到md文件，或者通过编程的方式访问table实例。

关于table实例，请看[这里](#table)。

## 先看一些例子

### 单个JSON对象

```json
{
  "firstName": "John",
  "lastName": "doe",
  "age": 26
}
```

markdown输出表格：

| firstName | lastName | age |
|-----------|----------|-----|
| John      | doe      | 26  |

### JSON数组

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

markdown输出表格：

| firstName | lastName | age |
|-----------|----------|-----|
| John      | doe      | 26  |
| Jane      | doe      | 24  |

#### 如果数组中schema不一致

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

markdown输出表格：

| firstName | lastName | age | birthday   |
|-----------|----------|-----|------------|
| John      | doe      | 26  |            |
| Jane      | doe      |     | 2000-04-02 |

表格会聚合所有的字段，如果某个字段在某个对象中不存在，那么用null表示。

后续示例我们都会以数组展示，但是单个对象也是支持的。

### json对象中如果有二级对象

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

markdown输出表格：

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | birthday   |
|-----------|----------|-----|-----------------------|--------------|--------------------|------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           |            |
| Jane      | Smith    |     | Cherry Lane           | Kyoto        | 600-8000           | 1994-04-02 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           |            |

address中属性会被展开到上一级，如果有多级对象，也会被展开。

### json对象中如果有数组

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

markdown输出表格：

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | phones                                                                                   |
|-----------|----------|-----|-----------------------|--------------|--------------------|------------------------------------------------------------------------------------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | [{"type":"iPhone","number":"0123-4567-8888"},{"type":"home","number":"0123-4567-8910"}]  |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | [{"type":"Android","number":"0987-6543-2211"},{"type":"work","number":"0987-6543-2212"}] |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | [{"type":"iPhone","number":"0765-4321-5566"},{"type":"home","number":"0765-4321-5567"}]  |

此时，phones字段就是json数组的字符串。

如果我们想要展开phones字段，需要这样:

```
Table table = JsonToTableConverter.toTable(jsonStr, "$.phones");
String markdown = table.toMarkdown();
```

markdown输出表格：

| firstName | lastName | age | address.streetAddress | address.city | address.postalCode | phones.type | phones.number  |
|-----------|----------|-----|-----------------------|--------------|--------------------|-------------|----------------|
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | iPhone      | 0123-4567-8888 |
| John      | Doe      | 26  | Naist Street          | Nara         | 630-0192           | home        | 0123-4567-8910 |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | Android     | 0987-6543-2211 |
| Jane      | Smith    | 30  | Cherry Lane           | Kyoto        | 600-8000           | work        | 0987-6543-2212 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | iPhone      | 0765-4321-5566 |
| Alice     | Brown    | 28  | Sunset Boulevard      | Osaka        | 530-0011           | home        | 0765-4321-5567 |

"$.phones"类似jsonpath的选择器，但是phones必须是数组类型，否则的话，代码会抛出一个IllegalStateException。
此时，表格会展开到所有对象的phones的二级对象，类似一级对象和phone对象的left join的结果。

#### 如果有多个同级的数组字段

我们无法在一个表格中同时展开多个同级数组，只能通过选择器参数指定其中一个数组。

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

既然选择了"$.phones"，那么emails字段就会被"收起"，只有phones字段会被"展开"。

### 支持递归

如果你的json层次很深，也是支持的。

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
设置展开路径为"$.phones.details"

markdown输出表格：

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

介绍如何以编程的方式访问table实例。

### 获取列头标题

```
final List<TableHeader> headers = table.getHeaders();

for (TableHeader header : headers) {
    System.out.println(header.getAlias());
}
```

### 获取行数据

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

大家还希望以怎样的方式使用Table实例，以List<List<Object>>，还是CSV格式字符串？可以提issue。

