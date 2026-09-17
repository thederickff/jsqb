/*
 * The GPLv3 License (GPLv3)
 *
 * Copyright (c) 2023 Pablo Eduardo Martinez Solis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.str4ng3r.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Pablo Eduardo Martinez Solis
 */
final class Parameter {
    public static String p = "(:[a-zA-Z0-9]+)";
    public static Pattern pattern = Pattern.compile(p);
    protected HashMap<String, Object> parameters = new HashMap<>();

    private String sql;

    public Parameter addParameter(String column,  Object value) {
        this.parameters.put(column, value);
        return this;
    }

    public List<String> getIndexesOfOccurrences(String sql) {
        List<String> indexes = new ArrayList<>();
        Matcher m = Parameter.pattern.matcher(sql);
        StringBuffer result = new StringBuffer();

        while (m.find()) {
            String key = m.group().substring(1);
            Object value = parameters.get(key);
            if (value instanceof Collection<?>) {
                int size = ((Collection<?>) value).size();
                // Agregar el nombre de clave una vez por cada elemento de la colección
                for (int i = 0; i < size; i++) indexes.add(key);
                String placeholders = String.join(",", java.util.Collections.nCopies(size, "?"));
                m.appendReplacement(result, placeholders);
            } else {
                indexes.add(key);
                m.appendReplacement(result, "?");
            }
        }
        m.appendTail(result);
        this.sql = result.toString();
        return indexes;
    }

    public String getSql() {
        return sql;
    }

    static String setParameter(String sql, String... parameters) {
        Matcher m = Parameter.pattern.matcher(sql);
        int c = 0, groupPosition = 1;
        StringBuffer sb = new StringBuffer();

        while (m.find() && c < parameters.length) {
            StringBuilder buf = new StringBuilder(m.group());
            buf.replace(m.start(groupPosition) - m.start(), m.end(groupPosition) - m.start(), parameters[c++]);
            m.appendReplacement(sb, buf.toString());
        }

        m.appendTail(sb);
        return sb.toString();
    }

    List<Object> sortParameters(List<String> indexes) {
        // Usamos un Set de claves ya vistas para no añadir el mismo Collection dos veces
        // pero sí añadir valores escalares repetidos (e.g. mismo param en varios WHERE)
        List<Object> result = new ArrayList<>();
        java.util.Set<String> expandedKeys = new java.util.HashSet<>();
        for (String key : indexes) {
            Object value = parameters.get(key);
            if (value instanceof Collection<?>) {
                // Solo expandimos la primera vez que aparece esta clave; las siguientes
                // repeticiones del mismo nombre ya fueron contadas en getIndexesOfOccurrences
                if (expandedKeys.add(key)) {
                    result.addAll((Collection<?>) value);
                }
            } else {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Filter parameters
     *
     * @param parameterToRemove
     */
    void filterParameter(List<String> parameterToRemove) {
        parameterToRemove.parallelStream().forEach(p -> parameters.remove(p));
    }
}
