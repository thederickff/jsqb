package io.github.str4ng3r.dao;

public class UserMapper {
    Integer id;
    String name;

    String lastName;


    public UserMapper(Integer id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }


    @Override
    public String toString() {
        return "UserMapper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
