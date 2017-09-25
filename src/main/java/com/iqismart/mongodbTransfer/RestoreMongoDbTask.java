package com.iqismart.mongodbTransfer;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sty on 2017/9/19.
 */
@Component
@Scope("singleton")
public class RestoreMongoDbTask {
    /**
     * 获取配置参数
     */
    @Value("${rmt.from_db.host}")
    private String from_host ;

    @Value("${rmt.from_db.port}")
    private Integer from_port ;

    @Value("${rmt.to_db.host}")
    private String to_host ;

    @Value("${rmt.to_db.port}")
    private Integer to_port ;

    @Value("${rmt.database}")
    private String database ;

    @Value("${rmt.collections}")
    private String[] collections;

    @Value("${rmt.oneByOne}")
    private Boolean oneByOne ;

    @Value("${rmt.batchCount}")
    private Integer batchCount ;
    /**
     * 声明Mongodb客户端
     */
    private MongoDatabase mongoDatabaseFrom = null;
    private MongoDatabase mongoDatabaseTo = null;

    public RestoreMongoDbTask() {

    }

    protected Logger log = LoggerFactory.getLogger(RestoreMongoDbTask.class);
    
    void start(){
        mongoDatabaseFrom = new MongoClient( from_host , from_port ).getDatabase(database);
        mongoDatabaseTo = new MongoClient( to_host , to_port ).getDatabase(database);

        log.info("Connect to database successfully");

        for(int i=0;i<collections.length;i++){
            String item = collections[i];
            Long startTIme = System.currentTimeMillis();
            MongoCollection<Document> fromCollection = mongoDatabaseFrom.getCollection(item);
            MongoCollection<Document> fromCollectionFailed = mongoDatabaseFrom.getCollection(item+"_failed");
            MongoCollection<Document> toCollection = mongoDatabaseTo.getCollection(item);
            log.info("集合"+item+",选择成功");

            //检索所有文档
            /**
             * 1. 获取迭代器FindIterable<Document>
             * 2. 获取游标MongoCursor<Document>
             * 3. 通过游标遍历检索出的文档集合
             * */
            FindIterable<Document> findIterable = fromCollection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            int successful = 0;
            int failed = 0;
            if(oneByOne){
                while(mongoCursor.hasNext()){
                    Document fromDocument = mongoCursor.next();
                    try {
                        toCollection.insertOne(fromDocument);
                        fromCollection.updateOne(Filters.eq("_id", fromDocument.get("_id")), new Document("$set",new Document("transfered",1)));
                        log.info(fromDocument.get("_id")+" transfer successful , added flag transfered ");
                        successful++;
                    } catch (Exception e) {
                        log.error(fromDocument.get("_id")+" transfer filed , "+e.getLocalizedMessage());
                    }
                }
                Long endTime = System.currentTimeMillis();
                log.info("集合"+item+",共处理了"+successful+"个文档,耗时:"+(endTime-startTIme)/1000 +"s");
            }else{
                List<Document> tempDocumentList = new ArrayList<>();
                while(mongoCursor.hasNext()){
                    Long startTime = System.currentTimeMillis();
                    Document fromDocument = mongoCursor.next();
                    tempDocumentList.add(fromDocument);
                    try {
                        if(tempDocumentList.size() >= batchCount){
                            try {
                                toCollection.insertMany(tempDocumentList);
                                successful += tempDocumentList.size();
                                log.info("集合"+item+batchCount+"个文档批量传输成功,use:"+(System.currentTimeMillis() - startTime)+"ms");
                            } catch (Exception e) {
                                fromCollectionFailed.insertMany(tempDocumentList);
                                failed += tempDocumentList.size();
                                log.error("集合"+item+batchCount+"个文档批量传输失败："+e.getLocalizedMessage());
                            }
                            tempDocumentList.clear();

                        }

                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage());
                    }
                }

                if(tempDocumentList .size() >0){
                    try {
                        Long startTime = System.currentTimeMillis();
                        toCollection.insertMany(tempDocumentList);
                        log.info("集合"+item+tempDocumentList.size()+"个文档批量传输成功,use:"+(System.currentTimeMillis() - startTime)+"ms");
                    } catch (Exception e) {
                        log.error("集合"+item+tempDocumentList.size()+"个零头批量传输失败："+e.getLocalizedMessage());
                    }
                    successful += tempDocumentList.size();
                    tempDocumentList.clear();
                }

                Long endTime = System.currentTimeMillis();
                log.info("集合"+item+",共成功了"+successful+"个文档,失败:"+failed+",use:"+(endTime-startTIme)/1000 +"s");
            }



        }

    }


}
