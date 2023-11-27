SQL Query Builder
=================

Stop to lost time writing repeated SQL queries and let Java SQL Query Builder do the job for you. It's simple, fast and lightweight. **You don't need to make a connection with a database.** 
This project could be implement in any kind of Java Project since there's any dependency.

<a name="index_block"></a>

* [1. Installation](#block1)
* [1.1. Installation with Maven](#block1.1)
* [2. SELECT Statement](#block2)     
    * [2.1. Basic SELECT statement](#block2.1) 
    * [2.2. SELECT with Specific Fields statement](#block2.2)
    * [2.3. Aliased SELECT statement](#block2.3)
* [3. INNER JOIN statement](#block3)
    * [3.1 Simple Inner join](#block3.1)
* [4. Author](#block4)
* [5. License](#block5)

<a name="block1"></a>
## 1. Installation [↑](#index_block)
For default installation, see [Releases](https://github.com/derickfelix/jsqb/releases) section to download the .jar file and add it to the path of your project.
<a name="block1.1"></a>
### 1.1. Installation with Maven [↑](#index_block)
To install with maven

[![](https://jitpack.io/v/STR4NG3R/querybuilder4j.svg)](https://jitpack.io/#STR4NG3R/querybuilder4j)

Step 1. Add the JitPack repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
    <groupId>com.github.STR4NG3R</groupId>
    <artifactId>querybuilder4j</artifactId>
    <version>1.0.2</version>
</dependency>
```


<a name="block2"></a>
## 2. SELECT Statement [↑](#index_block)

<a name="block2.1"></a>
### 2.1. Basic SELECT statement [↑](#index_block) 
#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        Selector selector = new Selector();
        String sql = selector.select("users").write();
    
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
        Selector selector = new Selector();
        String sql = selector.select("users", "id")
        addSelect("name", "email")
        .write();
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id, users.name, users.email FROM users
```

<a name="block2.3"></a>
### 2.2. SELECT with where statement [↑](#index_block) 
Add a filter criteria to select statement

#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        String username = "a";
        String email = "anemail@example.com";

        Selector jsqb = new Selector();
        String sql = jsqb
        .select("users", "id as userId", "name as username", "email as receiver")
        .where("username LIKE \"%:username\"", selector.addParameter("username", username))
        .andWhere("email = :email", selector.addParameter("email", email, true))
        .write();
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id as userId, users.name as username, users.email as receiver FROM users
WHERE usename LIKE "%?" AND email "?"
```

<a name="block3"></a>
## 3. INNER JOIN statement [↑](#index_block)

<a name="block3.1"></a>
### 3.1. Simple Inner join [↑](#index_block)
The `join()` method expects an enum of possible type of JOIN
This method is described as:
`innerJoin(JOIN join,String table, String on)`.

#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        Selector selector = new Selector();
        String sql = selector.select("users as u", "id", "name", "email")
                             .join(JOIN.INNER, "roles as r", "r.id = u.role_id")
                             .addSelect("r.name", "r.id", "r.level")
                             .join(JOIN.RIGHT, "address as a", "a.user_id = u.id")
                             .addSelect("a.street", "a.cp", "a.number")
                             .write();
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT u.id, u.name, u.email, r.name, r.level, a.street, a.cp, a.number FROM users 
INNER JOIN roles r ON r.id = u.role_id
RIGHT JOIN address ON a a.user_id = u.id
```

<a name="block4"></a>
## 4. Authors [↑](#index_block)
Derick Felix


 - <derickfelix@zoho.com>
 - [https://github.com/derickfelix](https://github.com/derickfelix)

Pablo Eduardo Martinez Solis


 - <pablo980629@hotmail.com>
 - [https://github.com/STR4NG3R](https://github.com/STR4NG3R)


<a name="block5"></a>
## 5. License [↑](#index_block)
Java SQL Query Builder is licensed under the GPLv3 license.

```
The GPLv3 License (GPLv3)

Copyright (c) 2023 Pablo Eduardo Martinez Solis, Derick Felix

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
