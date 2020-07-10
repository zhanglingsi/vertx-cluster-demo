            vertx-cluster-demo
	
Remote Procedure Call(RPC)

Backend is zookeeper.

Deployed two microservices:

	1.EventBusService(Must be started in cluster mode)
	
	2.HttpEndpoint 
	
	
Start:

1.Modify the configuration file(src/conf/local.json) according to your situation;

2.Start your zookeeper;

3.cd data-service;./redeploy.sh

4.cd http-service;./redeploy.sh

5.http://127.0.0.1:8080/redis/get/:key  （GET请求，返回redis中key的值）


  http://127.0.0.1:8080/:key  （GET请求，返回key字符的长度）
  
  
  http://127.0.0.1:8080/api/set  （POST请求，设置redis中key的val值）  bodyData : {"zhangsan":"zhangsan@email.com","lisi":"lisi@email.com"}


  http://127.0.0.1:8080/api/qryData   （POST请求，查询MySQL库中的表数据）  
  {
  	"sql":"SELECT USER_ID,REAL_NAME,EMAIL,PASS_WORD,PHONE_NUM,UPDATE_TIME FROM TB_USER WHERE STATUS=? AND USER_ID=?",
  	"arg0":"1",
  	"arg1":"1"
  }
  