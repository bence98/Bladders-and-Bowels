package csokicraft.bukkit.bladders_bowels;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class FoodAttr implements ConfigurationSerializable{
	public ItemStack item;
	public int shit, pee, thirst;
	
	public FoodAttr(){}
	
	public FoodAttr(Map<String, Object> m){
		item=(ItemStack) m.get("item");
		shit=(Integer) m.get("shit");
		pee=(Integer) m.get("pee");
		thirst=(Integer) m.get("thirst");
	}
	
	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> m=new HashMap<>();
		m.put("item", item);
		m.put("shit", shit);
		m.put("pee", pee);
		m.put("thirst", thirst);
		return m;
	}
}
