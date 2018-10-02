SQL Query Builder
=================

Stop to lost time writing repeated SQL queries and let Java SQL Query Builder do the job for you. It's simple, fast and lightweight. **Works without establishing a connection to the database.** 

<a name="index_block"></a>

* [1. Installation](#block1)
* [2. SELECT Statement](#block2)     
        * [2.1. Basic SELECT statement](#block2.1) 
        * [2.2. SELECT with Specific Fields statement](#block2.2)
        * [2.3. Aliased SELECT statement](#block2.3)
* [3. Author](#block3)
* [4. License](#block4)

<a name="block1"></a>
## 1. Installation [↑](#index_block)
See [Releases](https://github.com/derickfelix/jsqb/releases) section to download the .jar file and add it to the path of your project.

<a name="block2"></a>
## 2. SELECT Statement [↑](#index_block)

<a name="block2.1"></a>
### 2.1. Basic SELECT statement [↑](#index_block) 
#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();
        String sql = jsqb.select().from("users");
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.* FROM users
```

<a name="block2.2"></a>
### 2.2. SELECT with Specific Fields [↑](#index_block) 
#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();
        String sql = jsqb.select("id", "name", "email").from("users");
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id, users.name, users.email FROM users
```

<a name="block2.3"></a>
### 2.2. Aliased SELECT statement [↑](#index_block) 
The same of the previous one but with more information.

#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        JSqlQueryBuilder jsqb = new JSqlQueryBuilder();
        String sql = jsqb.select("id AS userId", "name AS username", "email AS receiver").from("users");
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id AS userId, users.name AS username, users.email AS receiver FROM users
```

<a name="block3"></a>
## 3. Author [↑](#index_block)
Derick Felix

 - <derickfelix@zoho.com>
 - [https://github.com/derickfelix](https://github.com/derickfelix)


<a name="block4"></a>
## 4. License [↑](#index_block)
Java SQL Query Builder is licensed under the Apache license.

```
Copyright 2018 derickfelix.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```