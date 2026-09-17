SQL Query Builder
=================

Stop losing time writing repeated SQL queries and let Java SQL Query Builder do the job for you. It's simple, fast and lightweight. **You don't need a database connection to build the query.**
This project can be used in any kind of Java project since it has no runtime dependencies. It generates parameterized SQL (using `?` placeholders) plus the ordered list of parameters, ready to feed into a `PreparedStatement` or an ORM.

<a name="index_block"></a>

* [1. Installation](#block1)
    * [1.1. Installation with Maven](#block1.1)
* [2. SELECT statement](#block2)
    * [2.1. Basic SELECT](#block2.1)
    * [2.2. SELECT with specific fields](#block2.2)
    * [2.3. SELECT with WHERE](#block2.3)
    * [2.4. WHERE IN with a collection](#block2.4)
    * [2.5. GROUP BY / HAVING](#block2.5)
    * [2.6. ORDER BY](#block2.6)
* [3. JOIN statement](#block3)
* [4. INSERT statement](#block4)
* [5. UPDATE statement](#block5)
* [6. DELETE statement](#block6)
* [7. Dialects & pagination](#block7)
* [8. Authors](#block8)
* [9. License](#block9)

<a name="block1"></a>
## 1. Installation [↑](#index_block)
For a manual installation, see the [Releases](https://github.com/derickfelix/jsqb/releases) section to download the `.jar` file and add it to your project's classpath.

<a name="block1.1"></a>
### 1.1. Installation with Maven [↑](#index_block)

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

### API at a glance

Every builder (`Selector`, `Insert`, `Update`, `Delete`) produces a `SqlParameter` through `getSqlAndParameters()`. That object exposes:

* `getSql()` — the generated SQL string with `?` placeholders.
* `getListParameters()` — the parameter values in the exact order they appear in the SQL.

Parameters are bound with named tokens (`:name`) inside your criteria, and the value is supplied through a lambda (`Consumer`) so you never concatenate user input into the SQL.

<a name="block2"></a>
## 2. SELECT statement [↑](#index_block)

<a name="block2.1"></a>
### 2.1. Basic SELECT [↑](#index_block)
```java
import io.github.str4ng3r.common.Selector;
import io.github.str4ng3r.common.SqlParameter;

SqlParameter r = new Selector()
        .select("productos")
        .getSqlAndParameters();

System.out.println(r.getSql());
// SELECT * FROM productos
```

<a name="block2.2"></a>
### 2.2. SELECT with specific fields [↑](#index_block)
```java
SqlParameter r = new Selector()
        .select("usuarios", "id", "nombre", "email")
        .getSqlAndParameters();

System.out.println(r.getSql());
// SELECT id, nombre, email FROM usuarios
```
You can keep adding fields with `addSelect(...)`:
```java
SqlParameter r = new Selector()
        .select("usuarios", "id")
        .addSelect("nombre", "email")
        .getSqlAndParameters();
// SELECT id, nombre, email FROM usuarios
```

<a name="block2.3"></a>
### 2.3. SELECT with WHERE [↑](#index_block)
Use `where(...)` for the first criteria and `andWhere(...)` for the following ones. Each named token (`:id`, `:rol`) is bound through the lambda.
```java
SqlParameter r = new Selector()
        .select("usuarios")
        .where("activo = :activo", p -> p.put("activo", true))
        .andWhere("rol = :rol", p -> p.put("rol", "admin"))
        .getSqlAndParameters();

System.out.println(r.getSql());          // SELECT * FROM usuarios WHERE activo = ? AND rol = ?
System.out.println(r.getListParameters()); // [true, admin]
```

<a name="block2.4"></a>
### 2.4. WHERE IN with a collection [↑](#index_block)
When a bound value is a `Collection`, the builder expands one `?` per element.
```java
import java.util.Arrays;

SqlParameter r = new Selector()
        .select("productos")
        .where("id IN (:ids)", p -> p.put("ids", Arrays.asList(1, 2, 3)))
        .getSqlAndParameters();

System.out.println(r.getSql());          // SELECT * FROM productos WHERE id IN (?,?,?)
System.out.println(r.getListParameters()); // [1, 2, 3]
```

<a name="block2.5"></a>
### 2.5. GROUP BY / HAVING [↑](#index_block)
```java
SqlParameter r = new Selector()
        .select("ventas", "categoria", "COUNT(*) cnt")
        .groupBy("categoria")
        .having("COUNT(*) > :min", p -> p.put("min", 5))
        .getSqlAndParameters();
// SELECT categoria, COUNT(*) cnt FROM ventas GROUP BY categoria HAVING COUNT(*) > ?
```

<a name="block2.6"></a>
### 2.6. ORDER BY [↑](#index_block)
`orderBy(column, descending)` can be called multiple times; the columns accumulate in order.
```java
SqlParameter r = new Selector()
        .select("pedidos")
        .orderBy("fecha", true)   // DESC
        .orderBy("total", false)  // ASC
        .getSqlAndParameters();
// SELECT * FROM pedidos ORDER BY fecha DESC, total ASC
```

<a name="block3"></a>
## 3. JOIN statement [↑](#index_block)
The `join(...)` method takes a `Join` enum value (`Join.INNER`, `Join.LEFT`, `Join.RIGHT`, `Join.CROSS`), the table to join and the `ON` condition. Its signature is `join(Join join, String table, String on)`.
```java
import io.github.str4ng3r.common.Join;

SqlParameter r = new Selector()
        .select("usuarios u", "u.id", "u.nombre")
        .join(Join.INNER, "roles r", "r.id = u.rol_id")
        .join(Join.LEFT,  "direcciones d", "d.usuario_id = u.id")
        .addSelect("r.nombre", "d.calle")
        .getSqlAndParameters();
// SELECT u.id, u.nombre, r.nombre, d.calle FROM usuarios u
// INNER JOIN roles r ON r.id = u.rol_id
// LEFT JOIN direcciones d ON d.usuario_id = u.id
```

<a name="block4"></a>
## 4. INSERT statement [↑](#index_block)
`setColumns(...)` and `setValues(...)` must have the same count, otherwise `getSql()` throws `InvalidSqlGenerationException`.
```java
import io.github.str4ng3r.common.Insert;

String sql = new Insert()
        .setTable("usuarios")
        .setColumns("id", "nombre", "email")
        .setValues(1, "Pablo", "pablo@ejemplo.com")
        .getSql();
// INSERT INTO usuarios ( id,nombre,email )  VALUES (?,?,? )
```
You can also pass the table to the constructor: `new Insert("usuarios")`.

<a name="block5"></a>
## 5. UPDATE statement [↑](#index_block)
`setColumnsValuesToUpdate(...)` receives a `LinkedHashMap` so the SET order is preserved. A `WHERE` is mandatory (an update without one throws `InvalidSqlGenerationException`). Columns passed to `excludeColumns(...)` are removed from the SET clause.
```java
import io.github.str4ng3r.common.Update;

SqlParameter r = new Update()
        .from("usuarios u")
        .setColumnsValuesToUpdate(cols -> {
            cols.put("u.nombre", "Carlos");
            cols.put("u.email",  "carlos@ejemplo.com");
        })
        .where("u.id = :id", p -> p.put("id", 7))
        .getSqlAndParameters();
// UPDATE usuarios u SET u.nombre = ?, u.email = ?  WHERE u.id = ?
// parameters: [Carlos, carlos@ejemplo.com, 7]
```

<a name="block6"></a>
## 6. DELETE statement [↑](#index_block)
A `WHERE` is mandatory here too. Joins are supported and the primary table is not duplicated in the `FROM`.
```java
import io.github.str4ng3r.common.Delete;

SqlParameter r = new Delete()
        .from("sesiones")
        .where("usuario_id = :uid", p -> p.put("uid", 10))
        .andWhere("activa = :activa", p -> p.put("activa", false))
        .getSqlAndParameters();
// DELETE FROM sesiones WHERE usuario_id = ? AND activa = ?
```

<a name="block7"></a>
## 7. Dialects & pagination [↑](#index_block)
Set the target dialect with `setDialect(...)` (`Mysql`, `Oracle`, `Postgres`, `Sql`). Pagination is dialect-aware.
```java
import io.github.str4ng3r.common.Constants;
import io.github.str4ng3r.common.Pagination;

Selector s = new Selector()
        .select("usuarios")
        .setDialect(Constants.SqlDialect.Oracle);

SqlParameter sp = s.getSqlAndParameters();
// pageSize = 10, total count = 100, current page = 2
s.setPagination(sp, new Pagination(10, 100, 2));

System.out.println(sp.getSql());
// SELECT * FROM usuarios OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY
```

<a name="block8"></a>
## 8. Authors [↑](#index_block)
Derick Felix

 - <derickfelix@zoho.com>
 - [https://github.com/derickfelix](https://github.com/derickfelix)

Pablo Eduardo Martinez Solis

 - <pablo980629@hotmail.com>
 - [https://github.com/STR4NG3R](https://github.com/STR4NG3R)

<a name="block9"></a>
## 9. License [↑](#index_block)
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
