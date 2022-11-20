# FakeInventories

FakeInventories is a simple library plugin for PowerNukkit/Nukkit Minecraft Bedrock core, that will help you to create
your custom virtual inventories with ease.

##### [Download](https://github.com/IWareQ/FakeInventories/releases)

## Usage

```java
CustomInventory inventory=new CustomInventory(InventoryType.CHEST,"custom title");

		inventory.setDefaultItemHandler((item,event)->{
		event.setCancelled(true);

		Player target=event.getTransaction().getSource();

		target.sendMessage("is default item handler");
		});

		inventory.setItem(5,Item.get(Item.DIAMOND),((item,event)->{
		event.setCancelled(true);

		Player target=event.getTransaction().getSource();

		target.sendMessage("is custom item handler");

		target.removeWindow(inventory);
		}));

		player.addWindow(inventory);
```