package lucemans.ninventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Created by Lucemans at 06/05/2018
 * See https://lucemans.nl
 */
public class NInventory implements Listener {

    private static ArrayList<NInventory> ninvs = new ArrayList<NInventory>();

    private JavaPlugin plugin;
    private Inventory inv;
    private int runnable;
    public Runnable updateTick;
    public HashMap<Integer, Boolean> locked = new HashMap<Integer, Boolean>();
    public HashMap<Integer, Runnable> lclick = new HashMap<Integer, Runnable>();
    public HashMap<Integer, Runnable> rclick = new HashMap<Integer, Runnable>();
    public HashMap<Integer, Runnable> slclick = new HashMap<Integer, Runnable>();
    public HashMap<Integer, Runnable> srclick = new HashMap<Integer, Runnable>();
    public HashMap<Integer, Runnable> dclick = new HashMap<Integer, Runnable>();
    public HashMap<Integer, Runnable> mclick = new HashMap<Integer, Runnable>();

    public NInventory(String name, int size, JavaPlugin plugin) {
        //Bukkit.getLogger().info("Created on an inventory 2.0");
        this.plugin = plugin;
        this.inv = Bukkit.createInventory(null, size, name);
        for (int i = 0; i < size; i++) {
            locked.put(i, true);
        }
        ninvs.add(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                if (updateTick != null)
                    if (inv.getViewers().size() > 0)
                        updateTick.run();
            }
        }, 1, 1);
    }

    public void destroy() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != null)
                if (p.getOpenInventory() != null)
                    if (p.getOpenInventory().getTopInventory() != null)
                        if (compare(p.getOpenInventory().getTopInventory()))
                            p.closeInventory();
        }
        Bukkit.getScheduler().cancelTask(runnable);
        ninvs.remove(this);
    }

    public static void destroyAll() {
        for (NInventory ninv : (ArrayList<NInventory>) ninvs.clone())
            ninv.destroy();
    }

    public Inventory getInv() {
        return inv;
    }

    public NInventory setItem(ItemStack item, int slot) {
        inv.setItem(slot, item);
        return this;
    }

    public NInventory lockAll() {
        for (int i = 0; i < inv.getSize(); i++) {
            locked.put(i, true);
        }
        return this;
    }

    public NInventory unlockAll() {
        for (int i = 0; i < inv.getSize(); i++) {
            locked.put(i, false);
        }
        return this;
    }

    public NInventory setLClick(Integer i, Runnable run) {
        lclick.put(i, run);
        return this;
    }

    public NInventory setShiftLClick(Integer i, Runnable run) {
        slclick.put(i, run);
        return this;
    }

    public NInventory setRClick(Integer i, Runnable run) {
        rclick.put(i, run);
        return this;
    }

    public NInventory setShiftRClick(Integer i, Runnable run) {
        srclick.put(i, run);
        return this;
    }

    public NInventory setMiddleClick(Integer i, Runnable run) {
        mclick.put(i, run);
        return this;
    }

    public NInventory setDropClick(Integer i, Runnable run) {
        dclick.put(i, run);
        return this;
    }

    public NInventory setUpdate(Runnable run) {
        this.updateTick = run;
        return this;
    }

    public void close() {
        List<HumanEntity> r = new ArrayList<>(inv.getViewers());
        for (HumanEntity p : r) {
            p.closeInventory();
        }
    }

    public static void close(Player p) {
        if (p.getOpenInventory() != null) {
            p.getOpenInventory().close();
        }
    }

    // Handlers

    /*@EventHandler
    public void onInventory(InventoryCloseEvent event) {
        if (event.getInventory() == inv)
        {
            destroy();
        }
    }*/
    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if (compare(event.getClickedInventory())) {
            switch (event.getClick()) {
                case LEFT:
                    if (lclick.get(event.getSlot()) != null)
                        lclick.get(event.getSlot()).run();
                    break;
                case SHIFT_LEFT:
                    if (slclick.get(event.getSlot()) != null)
                        slclick.get(event.getSlot()).run();
                    break;
                case RIGHT:
                    if (rclick.get(event.getSlot()) != null)
                        rclick.get(event.getSlot()).run();
                    break;
                case SHIFT_RIGHT:
                    if (srclick.get(event.getSlot()) != null)
                        srclick.get(event.getSlot()).run();
                    break;
                case DROP:
                    if (dclick.get(event.getSlot()) != null)
                        dclick.get(event.getSlot()).run();
                    break;
                case MIDDLE:
                    if (mclick.get(event.getSlot()) != null)
                        mclick.get(event.getSlot()).run();
                    break;
            }
            event.setCancelled(true);
        } else {
            /*if (event.isShiftClick())
            {
                if (compare(event.getInventory()))
                {
                    // User clicks item in own inv with our inv is open.
                    event.setCancelled(true);
                }
            }
            if (event.getClick() == ClickType.MIDDLE) {
                Bukkit.getLogger().info("MIDDLE");
                event.setCancelled(true);
            }
            if (event.getAction() == InventoryAction.CLONE_STACK) {
                Bukkit.getLogger().info("CLONE");
                event.setCancelled(true);
            }*/
        }
    }

    /*@EventHandler
    public void onInventory(InventoryDragEvent event) {
        if (event.getInventory() == inv)
        {
            event.setCancelled(true);
        }
    }*/
    /*@EventHandler
    public void onInventory(InventoryInteractEvent event) {
        if (compare(event.getInventory()))
        {
            event.setCancelled(true);
        }
    }*/
    @EventHandler
    public void onInventory(InventoryMoveItemEvent event) {
        if (compare(event.getDestination())) {
            //Bukkit.getLogger().info("DESTINATION");
            event.setCancelled(true);
        }
        if (compare(event.getSource())) {
            //Bukkit.getLogger().info("SOURCE");
            event.setCancelled(true);
        }
        if (compare(event.getInitiator())) {
            //Bukkit.getLogger().info("INITIATOR");
            event.setCancelled(true);
        }
    }

    public boolean compare(Inventory inv1) {
        if (inv1 == null)
            return false;
//        if (!inv1.equals(inv))
//////            return false;
//        if (!inv1.getName().equalsIgnoreCase(inv.getName()))
//            return false;
        if (inv1.getType() != inv.getType())
            return false;
        if (inv.getViewers().size() == 0)
            return false;
        for (HumanEntity e : inv1.getViewers()) {
            if (!inv.getViewers().contains(e))
                return false;
        }
        return true;
    }
}