package io.rsps.sync.connector;

import org.apache.spark.sql.connector.write.WriterCommitMessage;

import java.io.Serializable;

public class RspsWriterCommitMessage implements WriterCommitMessage, Serializable {

    private final int partitionId;
    private final int rowCount;

    public RspsWriterCommitMessage(int partitionId, int rowCount) {
        this.partitionId = partitionId;
        this.rowCount = rowCount;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public int getRowCount() {
        return rowCount;
    }
}