package com.jadventure.game.entities;

import com.jadventure.game.GameBeans;
import com.jadventure.game.items.Item;
import com.jadventure.game.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sfvl.doctesting.junitextension.ApprovalsExtension;
import org.sfvl.doctesting.utils.CodeExtractor;
import org.sfvl.doctesting.utils.DocWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityDocTest {
    private Entity entity = new Player();

    static DocWriter doc = new DocWriter();
    @RegisterExtension
    static ApprovalsExtension extension = new ApprovalsExtension(doc);

     @Test
    public void testEquipItem_OneHanded() {
        ItemRepository itemRepo = GameBeans.getItemRepository();
        double oldDamage = entity.getDamage();
        final String itemName = "wshi1";
        Item item = itemRepo.getItem(itemName);
        Map<String, String> result = entity.equipItem(item.getPosition(), item);
        assertNotNull(result.get("damage"));
        double newDamage = entity.getDamage();
        double diffDamage = Double.parseDouble(result.get("damage"));

        assertEquals(itemName, entity.getWeapon());
        assertEquals(diffDamage, newDamage - oldDamage, 0.2);

        Map<EquipmentLocation, Item> equipment = entity.getEquipment();
        final EquipmentLocation locationToSearch = EquipmentLocation.RIGHT_HAND;
        final Item itemAtLocation = equipment.get(locationToSearch);
        assertEquals(item, itemAtLocation);

        doc.write(

                String.format("Player damage without item is %d", (int) oldDamage),
                "",
                String.format("When equip player with item *%s*", itemName),
                "",
                String.format("It returns:\n\n%s", result.entrySet().stream()
                        .map(e -> "* " + e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("\n"))),
                "",
                String.format("Player damage is now %d", (int) newDamage),
                "",
                String.format("His equipement on %s is %s", locationToSearch, itemAtLocation.getId()));


    }

    @Test
    public void equip_with_an_item_one_handed() {
        ItemRepository itemRepo = GameBeans.getItemRepository();
        final String itemName = "wshi1";
        Item item = itemRepo.getItem(itemName);
        doc.write("[%header, cols=\"a,a,a\"]", "|====", "");
        doc.write("| Initial State | Action | New State", "");
        doc.write("| " + display(entity), "");
        doc.write("| " + String.format("Equip with %s", itemName),
                "",
                "[source, java, indent=0]",
                "----",
                CodeExtractor.extractPartOfMethod(FindLambdaMethod.getMethod(EntityDocTest::equip_with_an_item_one_handed), "1"),
                "----",
                "",
                itemToString(item),
                ""
        );

        Map<String, String> result = null;
        // >>>1
        result = entity.equipItem(
                item.getPosition(),
                item
        );
        // <<<1
        doc.write("| " + display(entity), "");
        doc.write("|====");

    }

    public String itemToString(Item item) {
        final List<FindLambdaMethod.SerializableFunction<Item, Object>> methods = Arrays.asList(
                Item::getId,
                Item::getName,
                Item::getDescription,
                Item::getPosition
        );

        return methods.stream()
                .map(m -> String.format("* %s: %s",
                        FindLambdaMethod.getName(m).replaceFirst("get", ""),
                        m.apply(item)))
                .collect(Collectors.joining("\n")) +
                "\n" +
                "* Properties\n" +
                item.getProperties().entrySet().stream()
                .map(e -> String.format("** %s: %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
    }

    public String display(Entity player) {
        final String equipements = player.getEquipment().entrySet().stream()
                .map(e -> "* " + e.getKey() + ": " + e.getValue().getId())
                .collect(Collectors.joining("\n"));

        return String.join("\n",
                String.format("Damage: %d", (int) player.getDamage()),
                "",
                String.format("Weight: %d", (int) player.getStorage().calculateWeight()),
                "",
                String.format("Equipements:\n\n%s", equipements),
                ""
        );
    }
}
