package org.bingwei.db.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class Column {
    private String columnName;
    @EqualsAndHashCode.Exclude
    private String dataType;
    @EqualsAndHashCode.Exclude
    private boolean nullable;
    @EqualsAndHashCode.Exclude
    private int columnId;
}
