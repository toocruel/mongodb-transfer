package com.iqismart.mongodbTransfer;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

/**
 * Created by sty on 2017/9/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RmtUtilsApplication.class)
public class TestMongoDb {

    @Value("${rmt.from_db.host}")
    private String from_host ;

    @Value("${rmt.from_db.port}")
    private Integer from_port ;

    @Test
    public void testMongodb() {
        try {
            // 连接到 mongodb 服务
            Mongo mongo = new Mongo(from_host, from_port);
            //根据mongodb数据库的名称获取mongodb对象 ,
            DB db = mongo.getDB("9tong");
            Set<String> collectionNames = db.getCollectionNames();
            // 打印出9tong中的集合
            for (String name : collectionNames) {
                System.out.println("collectionName===" + name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
