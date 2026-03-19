package io.rsps.sync.connector;

import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.SupportsWrite;
import org.apache.spark.sql.connector.catalog.TableCapability;
import org.apache.spark.sql.connector.write.LogicalWriteInfo;
import org.apache.spark.sql.connector.write.WriteBuilder;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Set;

public class RspsTable implements Table, SupportsWrite {

    private final StructType schema;
    private final CaseInsensitiveStringMap options;

    public RspsTable(StructType schema, Map<String, String> properties) {
        this.schema = schema;

        // Convert Map → CaseInsensitiveStringMap here
        this.options = new CaseInsensitiveStringMap(properties);
    }

    @Override
    public String name() {
        return "rsps-sync-table";
    }

    @Override
    public StructType schema() {
        return schema;
    }

    @Override
    public Set<TableCapability> capabilities() {
        return Set.of(TableCapability.BATCH_WRITE);
    }

    @Override
    public WriteBuilder newWriteBuilder(LogicalWriteInfo info) {
        return new RspsWriteBuilder(info.schema(), options);
    }
}