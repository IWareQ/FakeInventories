# FakeInventories

FakeInventories is a simple library plugin for PowerNukkit/Nukkit Minecraft Bedrock core, that will help you to create
your custom virtual inventories with ease.

##### [Download](https://github.com/IWareQ/FakeInventories/releases)

## Usage

You can extend from CustomInventory

```java
import me.iwareq.fakeinventories.CustomInventory;

public class MyInventory extends CustomInventory {

    public MyInventory() {
        super(InventoryType.CHEST, "my title");
    }
}
``` 

or create a new CustomInventory object.

```java
CustomInventory inventory = new CustomInventory(InventoryType.CHEST);
```

To open your inventory, use `Player.addWindow(inventory);` or to close, use `Player.removeWindow(inventory);`

For handling items, use `CustomInventory.setItem(int slot, Item item, TriConsumer<Item, Custom Inventory, 
InventoryTransactionEvent> listener);`

You can also set a handler for all items that don't have a handler.
`CustomInventory.setDefaultListener(TriConsumer<Item, CustomInventory, InventoryTransactionEvent> listener)`