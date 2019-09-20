package csokicraft.bukkit.bladders_bowels;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import csokicraft.bukkit.bladders_bowels.ItemShit.EnumItemType;

public class MainPlugin extends JavaPlugin implements Listener{
	public static int MAX_SHIT, MAX_PEE, MAX_THIRST;
	public static double WARN;
	public static final String META_SHIT="bladders_bowels.shit", META_PEE="bladders_bowels.pee", META_THIRST="bladders_bowels.thirst";
	public static YamlLocale locale;
	
	public Recipe toiletRecipe;
	public List<Location> toilets;
	public List<FoodAttr> foods;
	private int taskItemTick=-1;
	
	@Override
	public void onDisable(){
		super.onDisable();
		if(taskItemTick>0)
			getServer().getScheduler().cancelTask(taskItemTick);
		reloadConfig();
		getConfig().set("toilets", toilets);
		saveConfig();
		ConfigurationSerialization.unregisterClass(FoodAttr.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		super.onEnable();
		ConfigurationSerialization.registerClass(FoodAttr.class);
		saveDefaultConfig();
		ItemShit.SHIT_ITEM_DATA=new NamespacedKey(this, "item_data");
		getServer().getPluginManager().registerEvents(this, this);
		
		try {
			locale=YamlLocale.getLocale(getConfig().getString("lang"), this);
		} catch (IOException | InvalidConfigurationException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		taskItemTick=getServer().getScheduler().scheduleSyncRepeatingTask(this, ()->{onTick();}, 0, (int)(20*getConfig().getDouble("frequency")));
		
		toiletRecipe=new ShapedRecipe(new NamespacedKey(this, "toilet"), ItemShit.getToilet())
				.shape("i i", "idi", "iii")
				.setIngredient('i', Material.IRON_INGOT)
				.setIngredient('d', Material.DIRT);
		getServer().addRecipe(toiletRecipe);
		
		var limits=getConfig().getConfigurationSection("limits");
		MAX_SHIT=limits.getInt("shit");
		MAX_PEE=limits.getInt("pee");
		MAX_THIRST=limits.getInt("thirst");
		WARN=limits.getDouble("warn");
		
		toilets=(List<Location>) getConfig().getList("toilets", new LinkedList<Location>());
		foods=(List<FoodAttr>) getConfig().getList("foods", new LinkedList<FoodAttr>());
	}
	
	public static int getPlayerMeta(Player p, String meta){
		return p.hasMetadata(meta)?p.getMetadata(meta).get(0).asInt():0;
	}
	
	protected void shit(Player p){
		int sh=getPlayerMeta(p, META_SHIT);
		sh-=MAX_SHIT;
		if(sh<0) sh=0;
		Item e=p.getWorld().dropItem(p.getLocation(), ItemShit.getShit());
		e.setPickupDelay(200);
		p.setMetadata(META_SHIT, new FixedMetadataValue(this, sh));
		p.getWorld().playSound(p.getLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 10.0f, .5f);
	}
	
	protected void pee(Player p){
		int sh=getPlayerMeta(p, META_PEE);
		sh-=MAX_PEE;
		if(sh<0) sh=0;
		Item e=p.getWorld().dropItem(p.getLocation(), ItemShit.getPee());
		e.setPickupDelay(200);
		p.setMetadata(META_PEE, new FixedMetadataValue(this, sh));
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WATER_AMBIENT, 10.0f, 1.0f);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		switch(label){
		case "shit":
			if(!(sender instanceof Player))
				return false;
			shit((Player) sender);
			return true;
		case "pee":
		case "piss":
			if(!(sender instanceof Player))
				return false;
			pee((Player) sender);
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK&&e.hasItem()&&ItemShit.getItemType(e.getItem())==EnumItemType.TOILET){
			if(!e.getPlayer().hasPermission("csokicraft.bladders_bowels.toilet")){
				e.setCancelled(true);
				return;
			}
			var pos=e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection());
			toilets.add(pos);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e){
		toilets.remove(e.getBlock().getLocation());
		if(e.isDropItems()){
			var b=e.getBlock();
			b.getWorld().dropItem(b.getLocation(), ItemShit.getToilet());
			e.setDropItems(false);
		}
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e){
		Player p=e.getPlayer();
		int thirst=getPlayerMeta(p, META_THIRST),
				shit=getPlayerMeta(p, META_SHIT),
				pee=getPlayerMeta(p, META_PEE);
		for(FoodAttr attr:foods)
			if(e.getItem().isSimilar(attr.item)||(attr.item.getType()==Material.POTION&&e.getItem().getType()==Material.POTION)){
				shit+=attr.shit;
				pee+=attr.pee;
				thirst+=attr.thirst;
				if(shit<0) shit=0;
				if(pee<0) pee=0;
				if(thirst<0) thirst=0;
				p.setMetadata(META_SHIT, new FixedMetadataValue(this, shit));
				p.setMetadata(META_PEE, new FixedMetadataValue(this, pee));
				p.setMetadata(META_THIRST, new FixedMetadataValue(this, thirst));
			}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		Player p=e.getPlayer();
		p.setMetadata(META_SHIT, new FixedMetadataValue(this, 0));
		p.setMetadata(META_PEE, new FixedMetadataValue(this, 0));
		p.setMetadata(META_THIRST, new FixedMetadataValue(this, 0));
	}
	
	int cnt=0;
	private void onTick(){
		for(var loc:toilets)
			for(var e:loc.getWorld().getNearbyEntities(loc, 1, 1, 1, (e)->{return e instanceof Item;})){
				var t=ItemShit.getItemType(((Item)e).getItemStack());
				if(t==ItemShit.EnumItemType.SHIT||t==ItemShit.EnumItemType.PEE){
					loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 10, 1);
					e.remove();
				}
			}
		
		for(Player p:getServer().getOnlinePlayers()){
			if(p.hasPermission("csokicraft.bladders_bowels.exempt"))
				continue;
			
			int thirst=getPlayerMeta(p, META_THIRST)+1,
					shit=getPlayerMeta(p, META_SHIT),
					pee=getPlayerMeta(p, META_PEE);
			p.setMetadata(META_THIRST, new FixedMetadataValue(this, thirst));
			
			if(cnt==30/getConfig().getDouble("frequency")){
				if(thirst>=WARN*MAX_THIRST)
					p.sendMessage(locale.translate("warn_thirst"));
				if(shit>=WARN*MAX_SHIT)
					p.sendMessage(locale.translate("warn_shit"));
				if(pee>=WARN*MAX_THIRST)
					p.sendMessage(locale.translate("warn_pee"));
				cnt=0;
			}
			
			//getLogger().info("INFO for "+p.getName()+"\n"+shit+"\n"+pee+"\n"+thirst);
			
			if(thirst>=MAX_THIRST){
				p.damage(1.0);
			}
			if(shit>=MAX_SHIT){
				shit(p);
				p.sendMessage(locale.translate("info_shit"));
			}
			if(pee>=MAX_PEE){
				pee(p);
				p.sendMessage(locale.translate("info_pee"));
			}
			cnt++;
		}
	}
}
