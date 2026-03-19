package io.rsps.sync.connector;

import org.apache.spark.sql.connector.write.BatchWrite;
import org.apache.spark.sql.connector.write.DataWriterFactory;
import org.apache.spark.sql.connector.write.WriterCommitMessage;
import org.apache.spark.sql.connector.write.PhysicalWriteInfo;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public class RspsBatchWrite implements BatchWrite {

    private final StructType schema;
    private final CaseInsensitiveStringMap options;
    private final String executionId;

    public RspsBatchWrite(
            StructType schema,
            CaseInsensitiveStringMap options,
            String executionId) {

        this.schema = schema;
        this.options = options;
        this.executionId = executionId;
    }

    /**
     * This method is called on the DRIVER.
     * It creates a factory that will be sent to executors.
     */
    @Override
    public DataWriterFactory createBatchWriterFactory(PhysicalWriteInfo info) {

        int numPartitions = info.numPartitions();

        System.out.println("Starting batch write");
        System.out.println("Execution ID: " + executionId);
        System.out.println("Number of partitions: " + numPartitions);

        return new RspsDataWriterFactory(schema, options, executionId);
    }

    /**
     * Called when ALL executors succeed.
     */
    @Override
    public void commit(WriterCommitMessage[] messages) {

        System.out.println("Batch write SUCCESS for execution: " + executionId);

        System.out.println("Total partitions committed: " + messages.length);

        //  FUTURE:
        // Here we will:
        // 1. Run MERGE SQL
        // 2. Move data from staging → target table
        // 3. Ensure idempotency
    }

    /**
     * Called when ANY executor fails.
     */
    @Override
    public void abort(WriterCommitMessage[] messages) {

        System.out.println("Batch write FAILED for execution: " + executionId);

        // FUTURE:
        // 1. Cleanup staging data
        // 2. Mark execution as failed
    }
}