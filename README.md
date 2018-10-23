SQL Query Builder
=================

Stop to lost time writing repeated SQL queries and let Java SQL Query Builder do the job for you. It's simple, fast and lightweight. **Works without establishing a connection to the database.** 

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
To install with maven, you can use the [Jitpack](https://jitpack.io/) for that.

Step 1. Add the JitPack repository to your build file
```xml
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependencies>
    ...
    <dependency>
        <groupId>com.github.derickfelix</groupId>
        <artifactId>jsqb</artifactId>
        <version>LATEST</version>
    </dependency>
</dependencies>
```
If the project doesn't have any GitHub Releases you can use the short commit hash or 'master-SNAPSHOT' as the version. Check the [Jitpack](https://jitpack.io/) page for more details. 



<a name="block2"></a>
## 2. SELECT Statement [↑](#index_block)

<a name="block2.1"></a>
### 2.1. Basic SELECT statement [↑](#index_block) 
#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        Jsqb jsqb = new Jsqb();
        String sql = jsqb.select("users").write();
    
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
        Jsqb jsqb = new Jsqb();
        String sql = jsqb.select("users", "id", "name", "email").write();
    
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
        Jsqb jsqb = new Jsqb();
        String sql = jsqb.select("users", "id as userId", "name as username", "email as receiver").write();
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id as userId, users.name as username, users.email as receiver FROM users
```

<a name="block3"></a>
## 3. INNER JOIN statement [↑](#index_block)

<a name="block3.1"></a>
### 3.1. Simple Inner join [↑](#index_block)
The `innerJoin()` method expects the `tableName`, and the `on` detail, and the `fields` parameter is optional.
This method is described as:
`innerJoin(String tableName, String on, String... fields)`.

#### Usage:
```java
public class Usage {
    public static void main(String[] args)
    {
        Jsqb jsqb = new Jsqb();
        String sql = jsqb.select("users", "id", "name", "email")
                             .innerJoin("roles", "roles.id = users.role_id", "name", "level")
                             .write();
    
        System.out.println(sql);
    }
}
```
#### Output:
```sql
SELECT users.id, users.name, users.email, roles.name, roles.level FROM users 
INNER JOIN roles on roles.id = users.role_id
```

<a name="block4"></a>
## 4. Author [↑](#index_block)
Derick Felix

 - <derickfelix@zoho.com>
 - [https://github.com/derickfelix](https://github.com/derickfelix)


<a name="block5"></a>
## 5. License [↑](#index_block)
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
