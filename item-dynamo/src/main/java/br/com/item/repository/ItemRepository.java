package br.com.item.repository;

import br.com.item.model.Item;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Item> table;
    private final String tableName = "Items"; // ensure the table exists in AWS

    public ItemRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(Item.class));
    }

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString());
        }
        table.putItem(item);
        return item;
    }

    public Optional<Item> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Item item = table.getItem(r -> r.key(key));
        return Optional.ofNullable(item);
    }

    public void deleteById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        table.deleteItem(r -> r.key(key));
    }

    public List<Item> findAll() {
        List<Item> out = new ArrayList<>();
        table.scan().items().forEach(out::add);
        return out;
    }
}
