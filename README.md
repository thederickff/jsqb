SQL Query Builder
=================

Stop losing time writing repeated SQL queries and let Java SQL Query Builder do the job for you. It's simple, fast and lightweight. **You don't need a database connection to build the query.**
This project can be used in any kind of Java project since it has no runtime dependencies. It generates parameterized SQL (using `?` placeholders) plus the ordered list of parameters, ready to feed into a `PreparedStatement` or an ORM.

## Features

* **Fluent builders** for `SELECT`, `INSERT`, `UPDATE` and `DELETE`.
* **Parameterized output**: named tokens (`:name`) bound through lambdas — user input never gets concatenated into the SQL, so it is safe from SQL injection.
* **Automatic `IN (...)` expansion**: a bound `Collection` becomes one `?` per element.
* **JOINs**: `INNER`, `LEFT`, `RIGHT` and `CROSS` (no dangling `ON` for cross joins).
* **Multiple base tables** in `FROM` (comma-separated) for `SELECT` and `DELETE`.
* **Incremental building**: add fields (`addSelect`), tables (`addFrom`), filters (`andWhere`), order columns (`orderBy` accumulates) and `GROUP BY` / `HAVING`.
* **`UPDATE` helpers**: ordered `SET` via `LinkedHashMap` and column exclusion with `excludeColumns`.
* **Dialect-aware pagination** for MySQL, Oracle, PostgreSQL and generic SQL.
* **Safety guards**: mandatory `WHERE` for `UPDATE`/`DELETE`, column/value symmetry check for `INSERT`, input validation (null/empty), and an immutable parameter list.
* **Two output shapes**: an ordered list of parameters or a name→value dictionary.
* **Zero runtime dependencies** and no database connection required to build queries.

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
    * [3.1. Inner / Left / Right joins](#block3.1)
    * [3.2. CROSS JOIN](#block3.2)
    * [3.3. Multiple base tables](#block3.3)
* [4. INSERT statement](#block4)
* [5. UPDATE statement](#block5)
    * [5.1. Excluding columns](#block5.1)
* [6. DELETE statement](#block6)
* [7. Dialects & pagination](#block7)
* [8. Real-world usage](#block8)
    * [8.1. Plain JDBC](#block8.1)
    * [8.2. Pagination with `Template<T>`](#block8.2)
    * [8.3. Spring `JdbcTemplate`](#block8.3)
    * [8.4. A reusable dynamic-filter repository](#block8.4)
* [9. API reference & validation](#block9)
* [10. Authors](#block10)
* [11. License](#block11)

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

<a name="block3.1"></a>
### 3.1. Inner / Left / Right joins [↑](#index_block)
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

<a name="block3.2"></a>
### 3.2. CROSS JOIN [↑](#index_block)
`crossJoin(table)` adds a `CROSS JOIN` with no `ON` clause.
```java
SqlParameter r = new Selector()
        .select("colores c", "c.id", "t.id")
        .crossJoin("tallas t")
        .getSqlAndParameters();
// SELECT c.id, t.id FROM colores c CROSS JOIN tallas t
```

<a name="block3.3"></a>
### 3.3. Multiple base tables [↑](#index_block)
Extra base tables added with `select(...)` varargs or `addFrom(...)` are joined with a comma in the `FROM` (independently from any `JOIN`).
```java
SqlParameter r = new Selector()
        .select("usuarios u", "u.id")
        .addFrom("roles r")
        .where("r.id = u.rol_id", p -> {})
        .getSqlAndParameters();
// SELECT u.id FROM usuarios u, roles r WHERE r.id = u.rol_id
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

<a name="block5.1"></a>
### 5.1. Excluding columns [↑](#index_block)
`excludeColumns(...)` removes columns from the generated `SET` (and from the
parameter list), which is handy when you build the column map generically but
want to keep some fields untouched.
```java
SqlParameter r = new Update()
        .from("usuarios u")
        .excludeColumns("u.password")          // never updated
        .setColumnsValuesToUpdate(cols -> {
            cols.put("u.nombre",   "Ana");
            cols.put("u.password", "secreto"); // dropped from SET
        })
        .where("u.id = :id", p -> p.put("id", 1))
        .getSqlAndParameters();
// UPDATE usuarios u SET u.nombre = ?  WHERE u.id = ?
// parameters: [Ana, 1]   (u.password and "secreto" are excluded)
```

<a name="block6"></a>
## 6. DELETE statement [↑](#index_block)
A `WHERE` is mandatory here too. Joins are supported and the primary table is not duplicated in the `FROM`. You may also pass several base tables to `from(...)` (comma-separated, e.g. MySQL multi-table deletes).
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

`Pagination(pageSize, totalCount, currentPage)` computes the offset/limit and each
dialect renders them in its own syntax (here with `pageSize = 10`, page `2`, so
offset `10`):

| Dialect  | Generated clause                                   |
|----------|----------------------------------------------------|
| Oracle   | `OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY`           |
| MySQL    | `LIMIT 10, 10`  (i.e. `LIMIT offset, count`)       |
| Postgres | `LIMIT 10 OFFSET 10`                               |
| Sql      | `LIMIT 10 OFFSET 10`                               |

`getCount(sql)` wraps a query in `SELECT COUNT(*) FROM ( ... )` (adding a
`temp_count` alias for MySQL) so you can obtain the total before paginating.
A `currentPage` below `1` throws `InvalidCurrentPageException`.

<a name="block8"></a>
## 8. Real-world usage [↑](#index_block)

Since the builder only produces a SQL string plus an ordered list of parameters,
it plugs into any data-access layer. These examples show the same builder output
consumed from plain JDBC, Spring and a small reusable repository.

<a name="block8.1"></a>
### 8.1. Plain JDBC [↑](#index_block)
Bind the ordered parameters into a `PreparedStatement` one by one. Because they
are already in SQL order, a simple index loop is enough.
```java
import io.github.str4ng3r.common.Join;
import io.github.str4ng3r.common.Selector;
import io.github.str4ng3r.common.SqlParameter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public List<User> findActiveUsers(Connection connection, String namePart) throws SQLException {
    SqlParameter query = new Selector()
            .select("users u", "u.id", "u.name", "u.email")
            .join(Join.LEFT, "user_address ua", "ua.user_id = u.id")
            .where("u.active = :active", p -> p.put("active", true))
            .andWhere("u.name LIKE :name", p -> p.put("name", "%" + namePart + "%"))
            .orderBy("u.name", false)
            .getSqlAndParameters();

    List<User> users = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(query.getSql())) {
        List<Object> params = query.getListParameters();
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));   // JDBC indexes are 1-based
        }
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        }
    }
    return users;
}
```

<a name="block8.2"></a>
### 8.2. Pagination with `Template<T>` [↑](#index_block)
`getCount(...)` wraps the query in a `SELECT COUNT(*)` so you can compute the
total, then `setPagination(...)` mutates the `SqlParameter` to add the dialect's
`LIMIT`/`OFFSET`. `Template<T>` bundles the fetched page together with the
pagination metadata (page size, total count, current page, total pages).
```java
import io.github.str4ng3r.common.*;

public Template<List<User>> findUsersPage(Connection connection, int page, int pageSize) throws Exception {
    Selector s = new Selector()
            .select("users u", "u.id", "u.name", "u.email")
            .setDialect(Constants.SqlDialect.Postgres);

    SqlParameter query = s.getSqlAndParameters();

    // 1) total rows for the same filters
    int total;
    try (PreparedStatement ps = connection.prepareStatement(s.getCount(query.getSql()))) {
        for (int i = 0; i < query.getListParameters().size(); i++)
            ps.setObject(i + 1, query.getListParameters().get(i));
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            total = rs.getInt(1);
        }
    }

    // 2) add LIMIT/OFFSET for the requested page
    s.setPagination(query, new Pagination(pageSize, total, page));

    // 3) fetch the page
    List<User> users = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(query.getSql())) {
        for (int i = 0; i < query.getListParameters().size(); i++)
            ps.setObject(i + 1, query.getListParameters().get(i));
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
        }
    }

    // Template<T> carries the data + pagination info (great for JSON responses)
    return new Template<>(query, users);
}
```

<a name="block8.3"></a>
### 8.3. Spring `JdbcTemplate` [↑](#index_block)
The ordered parameter list maps directly onto the varargs of `JdbcTemplate`,
and a `RowMapper` turns each row into your domain object.
```java
import io.github.str4ng3r.common.Selector;
import io.github.str4ng3r.common.SqlParameter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

public List<User> search(JdbcTemplate jdbc, List<Integer> roleIds) throws Exception {
    SqlParameter query = new Selector()
            .select("users u", "u.id", "u.name", "u.email")
            .where("u.role_id IN (:roles)", p -> p.put("roles", roleIds))  // expands to (?,?,...)
            .andWhere("u.active = :active", p -> p.put("active", true))
            .getSqlAndParameters();

    // getListParameters() is already in the right order for the '?' placeholders
    return jdbc.query(
            query.getSql(),
            query.getListParameters().toArray(),
            (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
}
```
For an `UPDATE`/`INSERT`/`DELETE` you would call `jdbc.update(query.getSql(), query.getListParameters().toArray())` instead.

<a name="block8.4"></a>
### 8.4. A reusable dynamic-filter repository [↑](#index_block)
A common ORM-style use case: build a query whose filters depend on which
arguments are present. `andWhere(...)` is only added when the value is non-null,
so the generated SQL stays minimal.
```java
import io.github.str4ng3r.common.Selector;
import io.github.str4ng3r.common.SqlParameter;

public class UserRepository {

    public SqlParameter buildSearch(String name, String email, Boolean active) {
        Selector s = new Selector().select("users u", "u.id", "u.name", "u.email");

        if (name != null)
            s.andWhere("u.name LIKE :name", p -> p.put("name", "%" + name + "%"));
        if (email != null)
            s.andWhere("u.email = :email", p -> p.put("email", email));
        if (active != null)
            s.andWhere("u.active = :active", p -> p.put("active", active));

        return s.orderBy("u.name", false).getSqlAndParameters();
    }
}

// buildSearch("ana", null, true) ->
//   SELECT u.id, u.name, u.email FROM users u
//   WHERE u.name LIKE ? AND u.active = ? ORDER BY u.name ASC
//   params: ["%ana%", true]
```
The `:name` token is never string-concatenated into the SQL; only the `%ana%`
value travels as a bound parameter, which keeps the query safe from SQL injection.

<a name="block9"></a>
## 9. API reference & validation [↑](#index_block)

Quick reference of the public builder API and the guards enforced when building.

**Common to every builder** (`Selector`, `Update`, `Delete`)

| Method | Description |
|--------|-------------|
| `where(criteria, lambda)` | First filter; resets any previous `WHERE`. |
| `andWhere(criteria, lambda)` | Appends a filter with `AND`. |
| `join(Join, table, on)` | `INNER` / `LEFT` / `RIGHT` join. |
| `crossJoin(table)` | `CROSS JOIN` (no `ON`). |
| `addFrom(table)` | Adds another base table (comma-separated `FROM`). |
| `setDialect(SqlDialect)` | Target dialect for pagination/quoting. |
| `getSqlAndParameters()` | Returns `SqlParameter` with SQL + ordered params. |
| `getSqlAndParametersDictionarie()` | Returns `SqlParameter` with a name→value map. |

**`Selector` only:** `select(table, fields...)`, `addSelect(fields...)`,
`orderBy(col, descending)` (accumulates), `groupBy(cols)`,
`having(criteria, lambda)`, `andHaving(criteria, lambda)`,
`getCount(sql)`, `setPagination(sqlParameter, pagination)`.

**`Insert`:** `setTable(t)` / `new Insert(t)`, `setColumns(cols...)`,
`setValues(vals...)`, `getSql()`.

**`Update`:** `from(tables...)`, `setColumnsValuesToUpdate(lambda)` (ordered
`LinkedHashMap`), `excludeColumns(cols...)`.

**`Delete`:** `from(tables...)`.

**`SqlParameter` output:** `getSql()`, `getListParameters()` (immutable ordered
list), `dictionarieParameters()` (name→value map when built with the dictionary
variant).

### Validation & safety guards

The builder fails fast with a clear exception instead of emitting broken SQL:

| Situation | Result |
|-----------|--------|
| `UPDATE` / `DELETE` without `WHERE` | `InvalidSqlGenerationException` |
| `INSERT` column/value count mismatch, or missing table | `InvalidSqlGenerationException` |
| No table provided to the query | `InvalidSqlGenerationException` |
| `null`/empty table, join table or filter criteria | `IllegalArgumentException` |
| `currentPage < 1` in pagination | `InvalidCurrentPageException` |

Bound values are always emitted as `?` placeholders (never concatenated), a bound
`Collection` expands to one `?` per element, and `getListParameters()` returns an
immutable list so the built query cannot be mutated by accident.

<a name="block10"></a>
## 10. Authors [↑](#index_block)
Derick Felix

 - <derickfelix@zoho.com>
 - [https://github.com/derickfelix](https://github.com/derickfelix)

Pablo Eduardo Martinez Solis

 - <pablo980629@hotmail.com>
 - [https://github.com/STR4NG3R](https://github.com/STR4NG3R)

<a name="block11"></a>
## 11. License [↑](#index_block)
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
