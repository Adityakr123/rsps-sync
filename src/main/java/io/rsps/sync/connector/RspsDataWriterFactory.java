package io.rsps.sync.connector;

import org.apache.spark.sql.connector.write.DataWriterFactory;
import org.apache.spark.sql.connector.write.DataWriter;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import java.io.Serializable;

public class RspsDataWriterFactory implements DataWriterFactory, Serializable {

    private final StructType schema;
    private final CaseInsensitiveStringMap options;
    private final String executionId;

    public RspsDataWriterFactory(
            StructType schema,
            CaseInsensitiveStringMap options,
            String executionId) {

        this.schema = schema;
        this.options = options;
        this.executionId = executionId;
    }

    @Override
    public DataWriter<org.apache.spark.sql.catalyst.InternalRow> createWriter(
            int partitionId,
            long taskId) {

        System.out.println("Creating writer for partition: " + partitionId);

        return new RspsDataWriter(schema, options, executionId, partitionId);
    }
}