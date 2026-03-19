package io.rsps.sync.connector;

import org.apache.spark.sql.connector.write.BatchWrite;
import org.apache.spark.sql.connector.write.WriteBuilder;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class RspsWriteBuilder implements WriteBuilder {

    private final StructType schema;
    private final CaseInsensitiveStringMap options;

    public RspsWriteBuilder(StructType schema, CaseInsensitiveStringMap options) {
        this.schema = schema;
        this.options = options;
    }

    @Override
    public BatchWrite buildForBatch() {

        // Create execution id here
        String executionId = java.util.UUID.randomUUID().toString();

        return new RspsBatchWrite(schema, options, executionId);
    }
}