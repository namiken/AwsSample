package aws.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

//https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/developerguide/DynamoDBMapper.Annotations.html
@DynamoDBTable(tableName = "testTable1")
public class TestTableEntity {
  private String testKey1;

  private String testKey2;

  private int column1;

  private String column2;

  @DynamoDBHashKey(attributeName = "TestKey1")
  public String getTestKey1() {
    return testKey1;
  }

  public void setTestKey1(String testKey1) {
    this.testKey1 = testKey1;
  }

  @DynamoDBRangeKey(attributeName = "TestKey2")
  public String getTestKey2() {
    return testKey2;
  }

  public void setTestKey2(String testKey2) {
    this.testKey2 = testKey2;
  }

  @DynamoDBAttribute
  public int getColumn1() {
    return column1;
  }

  public void setColumn1(int column1) {
    this.column1 = column1;
  }

  @DynamoDBAttribute
  public String getColumn2() {
    return column2;
  }

  public void setColumn2(String column2) {
    this.column2 = column2;
  }

  @Override
  public String toString() {
    return "TestTableEntity [testKey1=" + testKey1 + ", testKey2=" + testKey2 + ", column1=" + column1 + ", column2="
        + column2 + "]";
  }
}
