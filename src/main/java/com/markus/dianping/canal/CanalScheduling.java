package com.markus.dianping.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.markus.dianping.dal.ShopModelMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:markusZhang
 * degree of proficiency:
 * Date:Create in 2020/4/15 22:15
 */
@Component
public class CanalScheduling implements Runnable, ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Resource
    private CanalConnector canalConnector;
    @Autowired
    private ShopModelMapper shopModelMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    @Scheduled(fixedDelay = 100)
    public void run() {
        long batchId = -1;
        try{
            int batchSize = 1000;//一次拉取1000条数据
            Message message = canalConnector.getWithoutAck(batchSize);
            batchId = message.getId();//批数据的id
            List<CanalEntry.Entry> entries = message.getEntries();
            if(batchId!=-1 && entries.size()>0){
                //说明有数据
                entries.forEach(entry -> {
                    if(entry.getEntryType() == CanalEntry.EntryType.ROWDATA){
                        //解析处理
                        publishCanalEvent(entry);
                    }
                });
            }
            canalConnector.ack(batchId);
        }catch (Exception e){
            e.printStackTrace();
            canalConnector.rollback();
        }
    }
    private void publishCanalEvent(CanalEntry.Entry entry){
        CanalEntry.EventType entryType = entry.getHeader().getEventType();//EventType是一个枚举类，得到数据库的操作是哪种类型
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        CanalEntry.RowChange change = null;//对应数据改变是哪些变化
        try {
            change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }
        change.getRowDatasList().forEach(rowData -> {
            List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
            String primaryKey = "id";
            CanalEntry.Column idColumn = columns.stream().filter(column -> column.getIsKey()
                && primaryKey.equals(column.getName())).findFirst().orElse(null);
            Map<String,Object> dataMap = parseColumnToMap(columns);
            try {
                indexES(dataMap,database,table);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private Map<String,Object> parseColumnToMap(List<CanalEntry.Column> columns){
        Map<String,Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if(column==null){
                return;
            }
            jsonMap.put(column.getName(),column.getValue());
        });
        return jsonMap;
    }
    private void indexES(Map<String,Object> dataMap,String database,String table) throws IOException {
        if(!StringUtils.equals("dianping",database)){
            return ;
        }
        List<Map<String,Object>> resultMap = new ArrayList<>();
        if(StringUtils.equals("seller",table)){
            resultMap = shopModelMapper.buildESQuery(new Integer((String) dataMap.get("id")),null,null);
        }else if(StringUtils.equals("category",table)){
            resultMap = shopModelMapper.buildESQuery(null,new Integer((String) dataMap.get("id")),null);
        }else if(StringUtils.equals("shop",table)){
            resultMap = shopModelMapper.buildESQuery(null,null,new Integer((String) dataMap.get("id")));
        }
        for (Map<String, Object> map : resultMap) {
            IndexRequest indexRequest = new IndexRequest("shop");
            indexRequest.id(String.valueOf(map.get("id")));
            indexRequest.source(map);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
