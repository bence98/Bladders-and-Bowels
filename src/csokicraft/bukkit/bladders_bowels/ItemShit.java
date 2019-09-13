package csokicraft.bukkit.bladders_bowels;

import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType.PrimitivePersistentDataType;

public class ItemShit{
	public static NamespacedKey SHIT_ITEM_DATA;

	public static ItemStack getShit(){
		ItemStack shit=new ItemStack(Material.BROWN_DYE);
		var meta=shit.getItemMeta();
		meta.setDisplayName("§6§lShit");
		meta.setLore(Collections.singletonList("§rYep, this is what it looks like. A piece of human feces."));
		meta.getPersistentDataContainer().set(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, EnumItemType.SHIT.ordinal());
		shit.setItemMeta(meta);
		return shit;
	}
	
	public static ItemStack getPee(){
		ItemStack pee=new ItemStack(Material.YELLOW_DYE);
		var meta=pee.getItemMeta();
		meta.setDisplayName("§e§lPee");
		meta.setLore(Collections.singletonList("§rSmells a bit foul. Then again, it §ois§r a puddle of piss..."));
		meta.getPersistentDataContainer().set(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, EnumItemType.PEE.ordinal());
		pee.setItemMeta(meta);
		return pee;
	}
	
	public static ItemStack getToilet(){
		ItemStack toilet=new ItemStack(Material.CAULDRON);
		var meta=toilet.getItemMeta();
		meta.setDisplayName("§b§lToilet");
		meta.setLore(Collections.singletonList("§rIt's a toilet. Useful when nature calls."));
		meta.getPersistentDataContainer().set(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, EnumItemType.TOILET.ordinal());
		toilet.setItemMeta(meta);
		return toilet;
	}
	
	public static EnumItemType getItemType(ItemStack is){
		return EnumItemType.values()[is.getItemMeta().getPersistentDataContainer().getOrDefault(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, 0)];
	}
	
	public static enum EnumItemType{
		NONE, SHIT, PEE, TOILET;
	}
}
