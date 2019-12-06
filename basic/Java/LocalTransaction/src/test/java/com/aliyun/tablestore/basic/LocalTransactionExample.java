package com.aliyun.tablestore.basic;

import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.AbortTransactionRequest;
import com.alicloud.openservices.tablestore.model.BatchGetRowRequest;
import com.alicloud.openservices.tablestore.model.BatchGetRowResponse;
import com.alicloud.openservices.tablestore.model.BatchWriteRowRequest;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.CommitTransactionRequest;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.DeleteTableRequest;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.MultiRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.StartLocalTransactionRequest;
import com.alicloud.openservices.tablestore.model.StartLocalTransactionResponse;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LocalTransactionExample extends BaseExample {

    private static final String TABLE_NAME = "local_transaction_table1";
    private static final String PK_USER_ID = "user_id";
    private static final String PK_TYPE = "type";
    private static final String COLUMN_CONTENT = "content";


    @Override
    public void init() {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        tableMeta.addPrimaryKeyColumn(PK_USER_ID, PrimaryKeyType.STRING);
        tableMeta.addPrimaryKeyColumn(PK_TYPE, PrimaryKeyType.STRING);
        CreateTableRequest request = new CreateTableRequest(
                tableMeta,
                new TableOptions(-1, 1)
        );
        try {
            syncClient.createTable(request);
        } catch (TableStoreException e) {
            if (!Objects.equals(e.getErrorCode(), "OTSObjectAlreadyExist")) {
                throw e;
            }
        }
    }

    @Override
    public void tearDown() {
        syncClient.deleteTable(new DeleteTableRequest(TABLE_NAME));
    }


    // basic example
    @Test
    public void testPutMultipleRow() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";
        final String typeB = "type_b";
        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();
        // put row A
        PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
        // set transactionId in startTransactionResponse
        typeAPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeAPutRequest);

        // put row B
        PutRowRequest typeBPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeB))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_b")));
        typeBPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeBPutRequest);
        // commit transaction
        syncClient.commitTransaction(new CommitTransactionRequest(transactionId));
    }


    @Test
    public void testGetRowInTransaction() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";
        final String typeB = "type_b";
        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();
        // put row A
        PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
        // set transactionId in startTransactionResponse
        typeAPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeAPutRequest);

        // read with transactionId
        {
            GetRowRequest getRowRequest = new GetRowRequest();
            SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(
                    TABLE_NAME,
                    new PrimaryKey(Arrays.asList(
                            new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                            new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                    ))
            );
            singleRowQueryCriteria.setMaxVersions(1);
            getRowRequest.setRowQueryCriteria(singleRowQueryCriteria);
            getRowRequest.setTransactionId(transactionId);
            GetRowResponse getRowResponse = syncClient.getRow(getRowRequest);
            Assert.assertNotNull(getRowResponse.getRow());
        }
        // read without transactionId, if transaction not committed, will get old value
        {
            GetRowRequest getRowRequest = new GetRowRequest();
            SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(
                    TABLE_NAME,
                    new PrimaryKey(Arrays.asList(
                            new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                            new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                    ))
            );
            singleRowQueryCriteria.setMaxVersions(1);
            getRowRequest.setRowQueryCriteria(singleRowQueryCriteria);
            GetRowResponse getRowResponse = syncClient.getRow(getRowRequest);
            Assert.assertNull(getRowResponse.getRow());
        }

        // put row B
        PutRowRequest typeBPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeB))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_b")));
        typeBPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeBPutRequest);
        // commit transaction
        syncClient.commitTransaction(new CommitTransactionRequest(transactionId));

        // get row A and row B
        SingleRowQueryCriteria rowAQueryCriteria = new SingleRowQueryCriteria(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA)))));
        rowAQueryCriteria.setMaxVersions(1);
        GetRowResponse rowAResponse = syncClient.getRow(new GetRowRequest(rowAQueryCriteria));
        Assert.assertNotNull(rowAResponse.getRow());

        SingleRowQueryCriteria rowBQueryCriteria = new SingleRowQueryCriteria(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA)))));
        rowBQueryCriteria.setMaxVersions(1);
        GetRowResponse rowBResponse = syncClient.getRow(new GetRowRequest(rowBQueryCriteria));
        Assert.assertNotNull(rowBResponse.getRow());
    }

    @Test
    public void testStartTwoTransaction() {
        final String userId = "foo_user_id";
        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionResponse response = syncClient.startLocalTransaction(new StartLocalTransactionRequest(TABLE_NAME, transactionPK));
        try {
            syncClient.startLocalTransaction(new StartLocalTransactionRequest(TABLE_NAME, transactionPK));
            Assert.fail();
        } catch (TableStoreException e) {
            Assert.assertEquals("OTSRowOperationConflict", e.getErrorCode());
        }
        syncClient.abortTransaction(new AbortTransactionRequest(response.getTransactionID()));
    }


    @Test
    public void testPutRowWithAbortTransaction() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";
        final String typeB = "type_b";
        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();
        // put row A
        PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
        // set transactionId in startTransactionResponse
        typeAPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeAPutRequest);

        // read with transactionId
        GetRowRequest getRowRequest = new GetRowRequest();
        SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        );
        singleRowQueryCriteria.setMaxVersions(1);
        getRowRequest.setRowQueryCriteria(singleRowQueryCriteria);
        getRowRequest.setTransactionId(transactionId);
        GetRowResponse getRowResponse = syncClient.getRow(getRowRequest);
        Assert.assertNotNull(getRowResponse.getRow());

        // abort transaction
        syncClient.abortTransaction(new AbortTransactionRequest(transactionId));
        // read row A will got null
        getRowRequest = new GetRowRequest();
        singleRowQueryCriteria = new SingleRowQueryCriteria(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        );
        singleRowQueryCriteria.setMaxVersions(1);
        getRowRequest.setRowQueryCriteria(singleRowQueryCriteria);
        getRowResponse = syncClient.getRow(getRowRequest);
        Assert.assertNull(getRowResponse.getRow());

    }


    // put row A with transactionId and put row B without transaction Id
    @Test
    public void testPutRowWithoutTransactionId() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";
        final String typeB = "type_b";
        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();
        // put row A with transactionID
        PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
        // set transactionId in startTransactionResponse
        typeAPutRequest.setTransactionId(transactionId);
        syncClient.putRow(typeAPutRequest);

        // put row B without transactionID
        PutRowRequest typeBPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeB))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_b")));
        // put without transactionId, due to PK_USE_ID is locked by another transaction, this action will fail
        try {
            syncClient.putRow(typeBPutRequest);
            Assert.fail();
        } catch (TableStoreException e) {
            // ok
            Assert.assertEquals("OTSRowOperationConflict", e.getErrorCode());
        }

        syncClient.abortTransaction(new AbortTransactionRequest(transactionId));
    }

    // batch write row in transaction
    @Test
    public void testBatchWriteRow() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";
        final String typeB = "type_b";
        final String typeC = "type_c";
        final String typeD = "type_d";


        // transaction under pk user_id
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();

        // row A and row B in one batch
        {
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
            batchWriteRowRequest.addRowChange(new RowPutChange(TABLE_NAME, new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
            ))).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
            batchWriteRowRequest.addRowChange(new RowPutChange(TABLE_NAME, new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeB))
            ))).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_b")));
            batchWriteRowRequest.setTransactionId(transactionId);
            syncClient.batchWriteRow(batchWriteRowRequest);
        }

        // row C and row D in one batch
        {
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
            batchWriteRowRequest.addRowChange(new RowPutChange(TABLE_NAME, new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeC))
            ))).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_c")));
            batchWriteRowRequest.addRowChange(new RowPutChange(TABLE_NAME, new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeD))
            ))).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_d")));
            batchWriteRowRequest.setTransactionId(transactionId);
            syncClient.batchWriteRow(batchWriteRowRequest);
        }

        // batch get rows with transaction not committed
        BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();

        MultiRowQueryCriteria multiRowQueryCriteria = new MultiRowQueryCriteria(TABLE_NAME)
                .addRow(new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                )))
                .addRow(new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeB))
                )))
                .addRow(new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeC))
                )))
                .addRow(new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeD))
                )));
        multiRowQueryCriteria.setMaxVersions(1);
        batchGetRowRequest.addMultiRowQueryCriteria(multiRowQueryCriteria);

        BatchGetRowResponse batchGetRowResponse = syncClient.batchGetRow(batchGetRowRequest);
        Assert.assertEquals(4, batchGetRowResponse.getSucceedRows().size());
        for (BatchGetRowResponse.RowResult rowResult : batchGetRowResponse.getSucceedRows()) {
            Assert.assertTrue(rowResult.isSucceed());
            Assert.assertNull(rowResult.getRow());
        }
        syncClient.commitTransaction(new CommitTransactionRequest(transactionId));
        // batch get again
        batchGetRowResponse = syncClient.batchGetRow(batchGetRowRequest);
        Assert.assertEquals(4, batchGetRowResponse.getSucceedRows().size());
        for (BatchGetRowResponse.RowResult rowResult : batchGetRowResponse.getSucceedRows()) {
            Assert.assertTrue(rowResult.isSucceed());
            Assert.assertNotNull(rowResult.getRow());
        }
    }


    @Test
    public void testReadThenWriteWithConcurrentModification() {
        final String userId = "foo_user_id";
        final String typeA = "type_a";

        // prepare one row
        {
            PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                    TABLE_NAME,
                    new PrimaryKey(Arrays.asList(
                            new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                            new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                    ))
            ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_a")));
            syncClient.putRow(typeAPutRequest);
        }

        // start transaction
        PrimaryKey transactionPK = new PrimaryKey(Collections.singletonList(
                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId))
        ));
        StartLocalTransactionRequest startTransactionRequest = new StartLocalTransactionRequest(TABLE_NAME, transactionPK);
        StartLocalTransactionResponse startTransactionResponse = syncClient.startLocalTransaction(startTransactionRequest);
        final String transactionId = startTransactionResponse.getTransactionID();


        CompletableFuture<Void> anotherModifyThreadResult = new CompletableFuture<>();
        Thread anotherModifyThread = new Thread(() -> {
            try {
                // write in another thread
                PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                        TABLE_NAME,
                        new PrimaryKey(Arrays.asList(
                                new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                                new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                        ))
                ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_another_thread")));
                // without transaction id
                syncClient.putRow(typeAPutRequest);
                anotherModifyThreadResult.complete(null);
            } catch (Exception e) {
                anotherModifyThreadResult.completeExceptionally(e);
            }
        });

        // another thread tries to modify record under PK_USER_ID:userId
        anotherModifyThread.start();
        try {
            anotherModifyThreadResult.get();
            Assert.fail();
        } catch (InterruptedException | ExecutionException e) {
            // always fail
        }
        String prevContent = null;
        {
            // read
            SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(TABLE_NAME);
            singleRowQueryCriteria.setPrimaryKey(new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
            )));
            singleRowQueryCriteria.setMaxVersions(1);
            GetRowRequest getRowRequest = new GetRowRequest(singleRowQueryCriteria);
            getRowRequest.setTransactionId(transactionId);
            GetRowResponse response = syncClient.getRow(getRowRequest);
            prevContent = response.getRow().getColumn(COLUMN_CONTENT).get(0).getValue().asString();
            Assert.assertNotNull(response.getRow());
            Assert.assertEquals("content_a", response.getRow().getColumn(COLUMN_CONTENT).get(0).getValue().asString());
        }
        Assert.assertNotNull(prevContent);
        // write
        PutRowRequest typeAPutRequest = new PutRowRequest(new RowPutChange(
                TABLE_NAME,
                new PrimaryKey(Arrays.asList(
                        new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                        new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
                ))
        ).addColumn(COLUMN_CONTENT, ColumnValue.fromString("content_current_thread" + prevContent)));
        typeAPutRequest.setTransactionId(transactionId);

        syncClient.putRow(typeAPutRequest);

        // commit
        syncClient.commitTransaction(new CommitTransactionRequest(transactionId));
        {
            // read after commit
            SingleRowQueryCriteria singleRowQueryCriteria = new SingleRowQueryCriteria(TABLE_NAME);
            singleRowQueryCriteria.setPrimaryKey(new PrimaryKey(Arrays.asList(
                    new PrimaryKeyColumn(PK_USER_ID, PrimaryKeyValue.fromString(userId)),
                    new PrimaryKeyColumn(PK_TYPE, PrimaryKeyValue.fromString(typeA))
            )));
            singleRowQueryCriteria.setMaxVersions(1);
            GetRowRequest getRowRequest = new GetRowRequest(singleRowQueryCriteria);
            GetRowResponse response = syncClient.getRow(getRowRequest);
            Assert.assertNotNull(response.getRow());
            Assert.assertEquals("content_current_threadcontent_a", response.getRow().getColumn(COLUMN_CONTENT).get(0).getValue().asString());
        }
    }
}
