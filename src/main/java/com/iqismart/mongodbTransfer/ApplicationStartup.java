package com.iqismart.mongodbTransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by sty on 2017/9/25.
 */

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    protected Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

    public void onApplicationEvent(ContextRefreshedEvent event){
        log.info("系统启动完成，正在启动任务...");
        RestoreMongoDbTask restoreMongoDbTask = event.getApplicationContext().getBean(RestoreMongoDbTask.class);
        restoreMongoDbTask.start();
    }
}

