package io.rsps.sync.connector;

import org.apache.spark.sql.connector.write.DataWriter;
import org.apache.spark.sql.connector.write.WriterCommitMessage;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.apache.spark.sql.catalyst.InternalRow;

import java.io.Serializable;

public class RspsDataWriter implements DataWriter<InternalRow>, Serializable {

    private final StructType schema;
    private final CaseInsensitiveStringMap options;
    private final String executionId;
    private final int partitionId;

    private int rowCount = 0;

    public RspsDataWriter(
            StructType schema,
            CaseInsensitiveStringMap options,
            String executionId,
            int partitionId) {

        this.schema = schema;
        this.options = options;
        this.executionId = executionId;
        this.partitionId = partitionId;
    }

    /**
     * Called for EACH row in this partition
     */
    @Override
    public void write(InternalRow record) {

        rowCount++;

        // For now just print (later we will write to DB)
        System.out.println("Partition " + partitionId + " writing row #" + rowCount);

        // Example: access first column
        // int id = record.getInt(0);
    }

    /**
     * Called when partition finishes successfully
     */
    @Override
    public WriterCommitMessage commit() {

        System.out.println("Partition " + partitionId + " committed. Rows: " + rowCount);

        return new RspsWriterCommitMessage(partitionId, rowCount);
    }

    /**
     * Called when partition fails
     */
    @Override
    public void abort() {

        System.out.println("Partition " + partitionId + " aborted.");

        // Later:
        // cleanup partial writes
    }

    @Override
    public void close() {
        // optional cleanup
    }
}