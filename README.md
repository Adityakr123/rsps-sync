# RSPS Sync

**RSPS Sync** is a safe Spark-to-OLTP synchronization framework that enables reliable **INSERT, UPDATE, UPSERT, and DELETE** operations from distributed Apache Spark workloads to relational databases.

It introduces a **staging-based distributed write architecture** that solves the reliability and consistency issues commonly encountered with direct Spark JDBC writes.

---

# Overview

Modern data platforms often use distributed processing engines such as **Apache Spark** to perform large-scale data transformations.

After processing, the results frequently need to be synchronized back to operational databases such as:

* Oracle Database
* PostgreSQL
* MySQL

Typical scenarios include:

* Updating customer profiles
* Applying financial transactions to account balances
* Synchronizing analytics results with operational systems
* Maintaining application reference tables

However, writing directly from Spark to OLTP systems introduces several reliability challenges.

**RSPS Sync solves this by introducing a safe staging-based synchronization mechanism.**

---

# Problems with Direct Spark JDBC Writes

The most common Spark approach is:

```python
df.write.format("jdbc").mode("append").save()
```

While simple, this approach has major limitations in distributed environments.

---

## 1. Non-Idempotent Writes

Spark automatically retries failed tasks.

If a task fails and is retried, the same rows may be written again.

Example scenario:

```
Executor writes rows
Executor crashes
Spark retries partition
Rows inserted again
```

This can lead to **duplicate records**.

---

## 2. Partial Database Updates

Each Spark executor commits transactions independently.

If the Spark job fails midway:

```
Executor 1 → committed
Executor 2 → committed
Executor 3 → failed
```

The database ends up with **partial updates**, leaving it in an inconsistent state.

---

## 3. Database Contention

Spark clusters may launch many executors simultaneously.

Each executor opens database connections and performs write operations.

This can lead to:

* Connection exhaustion
* Row locking conflicts
* Degraded OLTP database performance

---

## 4. Unsafe Concurrent Updates

Multiple executors may attempt to update the same rows simultaneously, resulting in inconsistent updates or conflicts.

---

# RSPS Sync Solution

RSPS Sync introduces a **staging-based architecture** to safely synchronize Spark outputs with OLTP databases.

Instead of writing directly to the target table, the workflow becomes:

```
Spark → Staging Table → Controlled Database Operation → Target Table
```

This architecture isolates distributed Spark writes from transactional database updates.

---

# Supported Operations

RSPS Sync supports the following database operations.

## Insert

Adds new rows to the target table.

```
Spark Data → Staging Table → Insert into Target Table
```

---

## Update

Updates existing rows using primary keys.

```
Spark Data → Staging Table → Update Matching Rows
```

---

## Upsert

Updates existing records or inserts new records.

```
Existing row → Update
New row → Insert
```

---

## Delete

Deletes rows using keys provided by Spark.

```
Spark Delete Events → Staging Table → Delete from Target Table
```

---

# Architecture

RSPS Sync consists of three core components.

---

## 1. Spark Executors

Spark executors process DataFrame partitions and write rows into the **staging table** using batch inserts.

```
Executor → INSERT INTO STAGING_TABLE
```

Each row receives an **internal event identifier** to ensure retry safety.

---

## 2. Staging Table

The staging table acts as a buffer between distributed Spark writes and the target OLTP database.

Benefits include:

* Retry-safe writes
* Isolation from production tables
* Improved consistency
* Easier validation and auditing

Example staging schema:

```
STAGING_TABLE
--------------
event_id
operation_type
record_payload
```

---

## 3. Controlled Database Operation

After Spark finishes writing to the staging table, RSPS Sync triggers a controlled SQL operation inside the database.

Examples include:

* Insert operations
* Update operations
* Merge operations
* Delete operations

These operations run **inside the database engine** to ensure transactional consistency.

---

# Execution Workflow

A typical synchronization job follows these steps:

1. Spark processes input data and produces a DataFrame
2. Spark executors write rows into the staging table using batch inserts
3. The Spark driver detects completion of the job
4. RSPS Sync triggers a controlled SQL operation using staging data
5. The database applies changes to the target table

---

# Example Usage

Example Spark job using RSPS Sync:

```python
df.write \
  .format("rsps-sync") \
  .option("targetTable", "CUSTOMER") \
  .option("operation", "upsert") \
  .option("keyColumn", "id") \
  .save()
```

The connector automatically handles:

* Staging table writes
* Distributed execution
* Retry safety
* Controlled database updates

---

# Advantages

Compared to direct JDBC writes, RSPS Sync provides:

* Reliable distributed database writes
* Idempotent execution
* Reduced OLTP database contention
* Controlled transactional updates
* Reusable synchronization framework

---

# Use Cases

RSPS Sync can be used for:

* Spark ETL pipelines updating operational databases
* Financial transaction processing
* Synchronizing analytics outputs with application databases
* Maintaining reference tables for microservices

---

# Future Enhancements

Planned improvements include:

* Snapshot synchronization (full table sync)
* Support for additional database dialects
* Streaming support
* Performance optimizations for large-scale updates

---

# License

This project is licensed under the **Apache License 2.0**.
