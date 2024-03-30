package io.github.str4ng3r.ddl;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey {
    String onUpdate;
    List<String> columnName;
    List<String> referencedColumnNames;
    String referencedTableName;
    String onDelete;

    ForeignKey addColumnName(String... columns) {
        if (columnName == null)
            columnName = new ArrayList<String>();
        for (String c : columns)
            columnName.add(c);
        return this;
    }

    ForeignKey addReferenceColumnName(String... referenceColumn) {
        if (referencedColumnNames == null)
            referencedColumnNames = new ArrayList<String>();
        for (String c : referenceColumn)
            referencedColumnNames.add(c);
        return this;
    }

    ForeignKey setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    ForeignKey setOnDelete(String onDelete) {
        this.onDelete = onDelete;
        return this;
    }

    ForeignKey setReferenceTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
        return this;
    }
}
