package aws.dynamodb;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

import aws.dynamodb.entity.TestTableEntity;

//オブジェクト永続モデル：https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/developerguide/DynamoDBMapper.ArbitraryDataMapping.html
//オブジェクト永続モデルでない場合： https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/developerguide/JavaDocumentAPIItemCRUD.html
//https://dev.classmethod.jp/server-side/java/spring-data-dynamodb/
//https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/developerguide/GettingStarted.Java.html
public class DynamoTest {

  public static void main(String[] args) {

    //認証情報
    BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id");
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
        .build();

    System.out.println(client.getClass());

    showTableList(client);

    //    createTable(client);

    //input
    TestTableEntity testTableEntity = new TestTableEntity();
    String hashkey = UUID.randomUUID().toString();
    testTableEntity.setTestKey1(hashkey);
    testTableEntity.setTestKey2("sort key");
    testTableEntity.setColumn1(new Random().nextInt(2000));
    testTableEntity.setColumn2("test column");

    DynamoDBMapper mapper = new DynamoDBMapper(client);
    mapper.save(testTableEntity);

    // get
    TestTableEntity inputedItem = mapper.load(TestTableEntity.class, hashkey, "sort key");
    System.out.println("input item: " + inputedItem);

    // update
    inputedItem.setColumn1(new Random().nextInt(2000));
    inputedItem.setColumn2("update test column");
    mapper.save(inputedItem);

    // get
    TestTableEntity updatedEntity = mapper.load(TestTableEntity.class, hashkey, "sort key");
    System.out.println("update item :" + updatedEntity);

    //delete
    mapper.delete(updatedEntity);

    //get
    TestTableEntity deletedEntity = mapper.load(TestTableEntity.class, hashkey, "sort key");
    System.out.println("delete item :" + deletedEntity);
  }

  static void createTable(AmazonDynamoDB client) throws AmazonServiceException {
    CreateTableRequest request = new CreateTableRequest()
        .withAttributeDefinitions(
            new AttributeDefinition("TestKey1", ScalarAttributeType.S),
            new AttributeDefinition("TestKey2", ScalarAttributeType.S))
        .withKeySchema(
            new KeySchemaElement("TestKey1", KeyType.HASH),
            new KeySchemaElement("TestKey2", KeyType.RANGE))
        .withProvisionedThroughput(
            new ProvisionedThroughput(new Long(10), new Long(10)))
        .withTableName("testTable");

    CreateTableResult result = client.createTable(request);
    System.out.println("Done!：" + result.getTableDescription());
  }

  static void showTableList(AmazonDynamoDB ddb) throws AmazonServiceException {

    System.out.println("show table List");

    ListTablesRequest request = new ListTablesRequest();
    ListTablesResult tableListResult = ddb.listTables(request);

    for (String tableName : tableListResult.getTableNames()) {
      TableDescription tableDescription = ddb.describeTable(tableName).getTable();
      System.out.println("table name = " + tableDescription.getTableName());
      List<KeySchemaElement> keySchema = tableDescription.getKeySchema();
      for (KeySchemaElement keySchemaElement : keySchema) {
        System.out.println("  " + keySchemaElement.getAttributeName() + " : " + keySchemaElement.getKeyType());
      }
      System.out.println();
    }
  }
}
