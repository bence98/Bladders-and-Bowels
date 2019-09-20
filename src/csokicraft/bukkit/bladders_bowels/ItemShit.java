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
		meta.setDisplayName(MainPlugin.locale.translate("item_name_shit"));
		meta.setLore(Collections.singletonList(MainPlugin.locale.translate("item_desc_shit")));
		meta.getPersistentDataContainer().set(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, EnumItemType.SHIT.ordinal());
		shit.setItemMeta(meta);
		return shit;
	}
	
	public static ItemStack getPee(){
		ItemStack pee=new ItemStack(Material.YELLOW_DYE);
		var meta=pee.getItemMeta();
		meta.setDisplayName(MainPlugin.locale.translate("item_name_pee"));
		meta.setLore(Collections.singletonList(MainPlugin.locale.translate("item_desc_pee")));
		meta.getPersistentDataContainer().set(SHIT_ITEM_DATA, PrimitivePersistentDataType.INTEGER, EnumItemType.PEE.ordinal());
		pee.setItemMeta(meta);
		return pee;
	}
	
	public static ItemStack getToilet(){
		ItemStack toilet=new ItemStack(Material.CAULDRON);
		var meta=toilet.getItemMeta();
		meta.setDisplayName(MainPlugin.locale.translate("item_name_toilet"));
		meta.setLore(Collections.singletonList(MainPlugin.locale.translate("item_desc_toilet")));
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
