/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.impl.map;

import com.hazelcast.impl.spi.AbstractOperation;
import com.hazelcast.impl.spi.NonBlockingOperation;
import com.hazelcast.impl.spi.OperationContext;
import com.hazelcast.impl.spi.ResponseHandler;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MapTxnBackupPrepareOperation extends AbstractOperation implements NonBlockingOperation {
    TransactionLog txnLog;

    public MapTxnBackupPrepareOperation(TransactionLog txnLog) {
        this.txnLog = txnLog;
    }

    public MapTxnBackupPrepareOperation() {
    }

    public void run() {
        OperationContext context = getOperationContext();
        int partitionId = context.getPartitionId();
        MapService mapService = (MapService) context.getService();
        System.out.println(context.getNodeService().getThisAddress() + " backupPrepare " + txnLog.txnId);
        mapService.getPartitionContainer(partitionId).putTransactionLog(txnLog.txnId, txnLog);
        ResponseHandler responseHandler = context.getResponseHandler();
        responseHandler.sendResponse(null);
    }

    @Override
    public void writeData(DataOutput out) throws IOException {
        super.writeData(out);
        txnLog.writeData(out);
    }

    @Override
    public void readData(DataInput in) throws IOException {
        super.readData(in);
        txnLog = new TransactionLog();
        txnLog.readData(in);
    }
}
