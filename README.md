# mongodb-transfer
mongodb 迁移程序，无关Mongodb版本。

java写的基于springboot。

### 1.修改配置application-dev.yml 
```
#开发/测试环境配置
rmt:
  from_db:
    host: 192.168.254.18
    port: 27017
  to_db:
    host: 192.168.254.22
    port: 27017
  database: 9tong
  collections: user_contacts,user_gps,weibo_contacts,linkedin_contacts,contacts_update_state 
#  singleConnection: contacts_update_state
  oneByOne: false
  batchCount: 1000

logging:
  path: logs
  level: info


```
 
  from_db 从哪个mongo导出

  to_db 导入到哪个mongo

  database 迁移哪个数据库

  collections 迁移该数据库哪些集合

  oneByOne 是否一个一个迁移，会在原document增加字段transfered，值为1，表明该document已被迁移

  batchCount oneByOne为false时，该值有效，单词批量迁移数量，视机器性能而定，默认1000
