package com.pinzhikeji;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pinzhikeji.service.DealService;

import java.net.InetSocketAddress;
import java.util.List;

public class CanalClient {

    private static final Log log = LogFactory.get();

    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("127.0.0.1", 11111),
                "example", "", "");
        try {
            canalConnector.connect();
//            canalConnector.subscribe("*.*");
            canalConnector.subscribe("canal_test.*");
            while (true) {
                Message message = canalConnector.get(10);
                dealData(message);
            }
        } finally {
            canalConnector.disconnect();
        }
    }

    private static void dealData(Message message) throws InterruptedException, InvalidProtocolBufferException {
        List<CanalEntry.Entry> entries = message.getEntries();
        if (entries.size() == 0) {
            log.info("暂无数据");
            Thread.sleep(3000);
        } else {
            for (CanalEntry.Entry entry : entries) {
                String tableName = entry.getHeader().getTableName();
                CanalEntry.EntryType entryType = entry.getEntryType();
                ByteString storeValue = entry.getStoreValue();
                if (CanalEntry.EntryType.ROWDATA.equals(entryType)) {
                    CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(storeValue);
                    boolean isDdl = rowChange.getIsDdl();
                    CanalEntry.EventType eventType = rowChange.getEventType();
                    log.info("sql: " + rowChange.getSql());
                    log.info("isddl: " + isDdl);
                    if (isDdl) {
                        DealService service =new DealService();
                        service.executeDdl(rowChange.getSql());
                    } else {
                        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
                        for (CanalEntry.RowData rowData : rowDatasList) {
                            if (CanalEntry.EventType.INSERT.equals(eventType)) {
                                DealService service =new DealService();
                                service.executeInsert(tableName, rowData.getAfterColumnsList());
                            } else if (CanalEntry.EventType.UPDATE.equals(eventType)) {
                                DealService service =new DealService();
                                service.executeUpdate(tableName, rowData.getAfterColumnsList());
                            } else if (CanalEntry.EventType.DELETE.equals(eventType)) {
                                DealService service =new DealService();
                                service.executeDelete(tableName, rowData.getBeforeColumnsList());
                            }

                            JSONObject beforeData = new JSONObject();
                            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
                            for (CanalEntry.Column column : beforeColumnsList) {
                                beforeData.put(column.getName(), column.getValue());
                            }
                            JSONObject afterData = new JSONObject();
                            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
                            for (CanalEntry.Column column : afterColumnsList) {
                                afterData.put(column.getName(), column.getValue());
                            }
                            System.out.println("Table:" + tableName +
                                    ",\n EventType:" + eventType +
                                    ",\n Before:" + beforeData +
                                    ",\n After:" + afterData);
                        }
                    }
                } else {
                    log.info("当前操作类型为:" + entryType);
                }
            }
        }
    }
}
