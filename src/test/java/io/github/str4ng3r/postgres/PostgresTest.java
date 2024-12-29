package io.github.str4ng3r.postgres;

import io.github.str4ng3r.common.*;
import io.github.str4ng3r.dao.UserMapper;
import io.github.str4ng3r.exceptions.InvalidCurrentPageException;
import io.github.str4ng3r.exceptions.InvalidSqlGenerationException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.io.Resources;

import java.io.IOException;

import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertEquals;

public class PostgresTest {
    @ClassRule
    public static PostgreSQLContainer postgresContainer;

    SelectorTest selectorTest = new SelectorTest();


    void interactWithDb(Consumer<Connection> cb) throws SQLException {

        try (Connection connection = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword())) {
            cb.accept(connection);
        }
    }

    @Before
    public void setup() throws IOException, SQLException {

        postgresContainer = new PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("integration-tests-db")
                .withUsername("sa")
                .withPassword("sa");
        postgresContainer.start();

        String initDb = Resources.toString(
                Objects.requireNonNull(
                        PostgresTest.class.getClassLoader().getResource("mock/postgresmock.sql")),
                Charset.defaultCharset()
        );
        interactWithDb((connection) -> {
            try {
                Statement statement = connection.createStatement();
                statement.execute(initDb);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    public void selectUsers() throws SQLException {
        interactWithDb((connection -> {
            try {
                SqlParameter sqlParameter = SelectorTest.baseQueryUsers("o", null, null).getSqlAndParameters();
                System.out.println(sqlParameter.sql);
                PreparedStatement ps = connection.prepareStatement(sqlParameter.sql);
                JDBCUtils.addParameters(ps, sqlParameter.getListParameters());
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    System.out.println(rs.getInt("id") + " " + rs.getString("name"));
                }

            } catch (InvalidSqlGenerationException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    public void selectUsersPagination() throws SQLException {
        interactWithDb((connection -> {
            try {
                Selector s = SelectorTest.baseQueryUsers("o", null, null);
                SqlParameter sqlParameter = s.getSqlAndParameters();

                int count = JDBCUtils.getCount(connection, s, sqlParameter);
                s.setPagination(sqlParameter, new Pagination(2, count, 1));

                PreparedStatement ps = connection.prepareStatement(sqlParameter.sql);
                JDBCUtils.addParameters(ps, sqlParameter.getListParameters());
                System.out.println(sqlParameter);
                ResultSet rs = ps.executeQuery();

                ArrayList<UserMapper> users = new ArrayList<>();

                while (rs.next()) {
                    users.add(new UserMapper(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
                }

                Template<List<UserMapper>> t = new Template<>(sqlParameter, users);
                System.out.println(t);

            } catch (InvalidSqlGenerationException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (InvalidCurrentPageException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
