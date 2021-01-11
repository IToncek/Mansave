// 
// Decompiled by Procyon v0.5.36
// 

package com.rozza.mansave;

import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.Iterator;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{
    public static ArrayList<Player> hunters;
    public static boolean inGame;
    private int countdownTask;
    public static boolean huntersTakeDamage;
    public static boolean huntersHungerLoss;
    public static boolean suicidalHungerLoss;
    public static int countdownInSeconds;
    public static ItemStack[] hunterSlot;
    
    static {
        Main.hunterSlot = new ItemStack[9];
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        Main.huntersTakeDamage = this.getConfig().getBoolean("huntersTakeDamage");
        Main.huntersHungerLoss = this.getConfig().getBoolean("huntersHungerLoss");
        Main.suicidalHungerLoss = this.getConfig().getBoolean("suicidalHungerLoss");
        Main.countdownInSeconds = this.getConfig().getInt("countdownInSeconds");
        Main.hunterSlot[0] = this.getConfig().getItemStack("hunterSlot0");
        Main.hunterSlot[1] = this.getConfig().getItemStack("hunterSlot1");
        Main.hunterSlot[2] = this.getConfig().getItemStack("hunterSlot2");
        Main.hunterSlot[3] = this.getConfig().getItemStack("hunterSlot3");
        Main.hunterSlot[4] = this.getConfig().getItemStack("hunterSlot4");
        Main.hunterSlot[5] = this.getConfig().getItemStack("hunterSlot5");
        Main.hunterSlot[6] = this.getConfig().getItemStack("hunterSlot6");
        Main.hunterSlot[7] = this.getConfig().getItemStack("hunterSlot7");
        Main.hunterSlot[8] = this.getConfig().getItemStack("hunterSlot8");
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getCommand("mansave").setTabCompleter((TabCompleter)new Autocomplete());
        Main.hunters = new ArrayList<Player>();
        Main.inGame = false;
    }
    
    public void onDisable() {
        this.getConfig().set("huntersTakeDamage", (Object)Main.huntersTakeDamage);
        this.getConfig().set("huntersHungerLoss", (Object)Main.huntersHungerLoss);
        this.getConfig().set("suicidalHungerLoss", (Object)Main.suicidalHungerLoss);
        this.getConfig().set("countdownInSeconds", (Object)Main.countdownInSeconds);
        this.getConfig().set("hunterSlot0", (Object)Main.hunterSlot[0]);
        this.getConfig().set("hunterSlot1", (Object)Main.hunterSlot[1]);
        this.getConfig().set("hunterSlot2", (Object)Main.hunterSlot[2]);
        this.getConfig().set("hunterSlot3", (Object)Main.hunterSlot[3]);
        this.getConfig().set("hunterSlot4", (Object)Main.hunterSlot[4]);
        this.getConfig().set("hunterSlot5", (Object)Main.hunterSlot[5]);
        this.getConfig().set("hunterSlot6", (Object)Main.hunterSlot[6]);
        this.getConfig().set("hunterSlot7", (Object)Main.hunterSlot[7]);
        this.getConfig().set("hunterSlot8", (Object)Main.hunterSlot[8]);
        this.saveConfig();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final String cmd = command.getName().toLowerCase();
        if (cmd.equals("mansave")) {
            if (args.length == 1) {
                if (args[0].equals("start")) {
                    if (Main.inGame) {
                        sender.sendMessage(ChatColor.RED + "Game already started!");
                    }
                    else {
                        this.startGame();
                    }
                    return true;
                }
                if (args[0].equals("stop")) {
                    if (!Main.inGame) {
                        sender.sendMessage(ChatColor.RED + "Game hasn't started!");
                    }
                    else {
                        this.stopGame("Game force stopped!");
                    }
                    return true;
                }
                if (args[0].contentEquals("setHunterItems")) {
                    final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, "Choose hunter items:");
                    for (int i = 0; i <= 8; ++i) {
                        if (Main.hunterSlot[i] == null) {
                            Main.hunterSlot[i] = new ItemStack(Material.AIR, 1);
                        }
                        inv.addItem(new ItemStack[] { Main.hunterSlot[i] });
                    }
                    final Player p = (Player)sender;
                    p.openInventory(inv);
                    p.sendMessage(ChatColor.BOLD + "Close inventory to save items!");
                    p.sendMessage(ChatColor.GRAY + "Use the creative inventory to get items, then place them in here.");
                    p.sendMessage(ChatColor.GRAY + "Use middle-mouse to stack any item (e.g. potions).");
                    return true;
                }
            }
            else if (args.length == 2) {
                if (args[0].equals("addHunter")) {
                    final Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                        return false;
                    }
                    Main.hunters.add(player);
                    sender.sendMessage(ChatColor.GREEN + player.getName() + " is now a hunter.");
                    return true;
                }
                else if (args[0].equals("removeHunter")) {
                    final Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                        return false;
                    }
                    Main.hunters.remove(player);
                    sender.sendMessage(ChatColor.GREEN + player.getName() + " is no longer a hunter.");
                    return true;
                }
                else {
                    if (args[0].equals("huntersTakeDamage")) {
                        Main.huntersTakeDamage = Boolean.parseBoolean(args[1]);
                        if (Main.huntersTakeDamage) {
                            sender.sendMessage(ChatColor.GREEN + "Hunters will now take damage.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "Hunters will no longer take damage.");
                        }
                        return true;
                    }
                    if (args[0].equals("huntersHungerLoss")) {
                        Main.huntersHungerLoss = Boolean.parseBoolean(args[1]);
                        if (Main.huntersHungerLoss) {
                            sender.sendMessage(ChatColor.GREEN + "Hunters will now lose hunger.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "Hunters will no longer lose hunger.");
                        }
                        return true;
                    }
                    if (args[0].equals("suicidalHungerLoss")) {
                        Main.suicidalHungerLoss = Boolean.parseBoolean(args[1]);
                        if (Main.suicidalHungerLoss) {
                            sender.sendMessage(ChatColor.GREEN + "The suicidal will now lose hunger, and be granted a stack of steak.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "The suicidal will no longer lose hunger.");
                        }
                        return true;
                    }
                    if (args[0].equals("countdownInSeconds")) {
                        Main.countdownInSeconds = Integer.parseInt(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "Countdown has been set to " + Main.countdownInSeconds + " seconds.");
                        return true;
                    }
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Invalid command.");
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Choose hunter items:")) {
            return;
        }
        final Player p = (Player)e.getWhoClicked();
        if (e.getClick() == ClickType.MIDDLE) {
            if (p.getItemOnCursor().getType() != Material.AIR) {
                return;
            }
            e.setCancelled(true);
            final ItemStack i = e.getCurrentItem().clone();
            i.setAmount(64);
            p.setItemOnCursor(i);
        }
        else if (e.getClick() == ClickType.RIGHT) {
            final ItemStack cur = p.getItemOnCursor();
            final ItemStack cli = e.getCurrentItem();
            if (cur == null || cli == null) {
                return;
            }
            if (cur.getType() == Material.AIR || cli.getType() == Material.AIR) {
                return;
            }
            if (cur.getType() == cli.getType()) {
                if (cli.getAmount() == 64) {
                    return;
                }
                cli.setAmount(cli.getAmount() + 1);
                if (cur.getAmount() == 0) {
                    p.setItemOnCursor((ItemStack)null);
                }
                else {
                    cur.setAmount(cur.getAmount() - 1);
                }
            }
            e.setCancelled(true);
        }
        else if (e.getClick() == ClickType.LEFT) {
            final ItemStack cur = p.getItemOnCursor();
            final ItemStack cli = e.getCurrentItem();
            if (cli == null || cli.getType() == Material.AIR) {
                if (cur == null || cur.getType() == Material.AIR) {
                    return;
                }
                e.setCancelled(true);
                e.getClickedInventory().setItem(e.getSlot(), cur);
                p.setItemOnCursor((ItemStack)null);
            }
            else {
                if (cur == null || cur.getType() == Material.AIR) {
                    return;
                }
                if (cur.getType() == cli.getType()) {
                    e.setCancelled(true);
                    final int total = cur.getAmount() + cli.getAmount();
                    if (total <= 64) {
                        cli.setAmount(total);
                        p.setItemOnCursor((ItemStack)null);
                    }
                    else {
                        cli.setAmount(64);
                        cur.setAmount(total - 64);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        final InventoryView iv = e.getView();
        if (!iv.getTitle().equals("Choose hunter items:")) {
            return;
        }
        final Inventory inv = iv.getTopInventory();
        for (int i = 0; i <= 8; ++i) {
            Main.hunterSlot[i] = inv.getItem(i);
        }
        e.getPlayer().sendMessage(ChatColor.GREEN + "Hunter items saved!");
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (!Main.inGame) {
            return;
        }
        final Player p = e.getEntity();
        if (Main.hunters.contains(p)) {
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
        else {
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setDeathMessage(String.valueOf(e.getDeathMessage()) + " and won the game!");
            this.stopGame("Hunters lost the game!");
        }
    }
    
    public void startGame() {
        final ScoreboardManager m = Bukkit.getScoreboardManager();
        final Scoreboard b = m.getNewScoreboard();
        final Objective o = b.registerNewObjective("timer", "", "Countdown");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        final Score s = o.getScore("Time left");
        s.setScore(Main.countdownInSeconds);
        this.countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)getPlugin((Class)Main.class), (Runnable)new Runnable() {
            @Override
            public void run() {
                final int score = s.getScore() - 1;
                if (score == 0) {
                    Main.this.stopGame("Time up! Hunters win!");
                }
                else {
                    s.setScore(s.getScore() - 1);
                }
            }
        }, 20L, 20L);
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final PotionEffect e : p.getActivePotionEffects()) {
                p.removePotionEffect(e.getType());
            }
            p.setFireTicks(0);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.setTotalExperience(0);
            p.getInventory().clear();
            p.setScoreboard(b);
            p.sendMessage(new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append("Game started! Good luck!").toString());
            if (!Main.hunters.contains(p) && Main.suicidalHungerLoss) {
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COOKED_BEEF, 64) });
            }
        }
        for (final Player h : Main.hunters) {
            this.giveHunterItems(h);
        }
        Main.inGame = true;
    }
    
    public void stopGame(final String msg) {
        Bukkit.getServer().getScheduler().cancelTask(this.countdownTask);
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append(msg).toString());
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (p.isDead()) {
                continue;
            }
            for (final PotionEffect e : p.getActivePotionEffects()) {
                p.removePotionEffect(e.getType());
            }
            p.setFireTicks(0);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.setTotalExperience(0);
            p.getInventory().clear();
        }
        Main.inGame = false;
    }
    
    public void giveHunterItems(final Player p) {
        final Inventory inv = (Inventory)p.getInventory();
        for (int i = 0; i <= 8; ++i) {
            inv.addItem(new ItemStack[] { Main.hunterSlot[i] });
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent e) {
        if (!Main.inGame) {
            return;
        }
        if (Main.huntersTakeDamage) {
            return;
        }
        if (e.getEntityType() == EntityType.PLAYER && Main.hunters.contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onHungerDeplete(final FoodLevelChangeEvent e) {
        if (!Main.inGame) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        final Player p = (Player)e.getEntity();
        if (!Main.huntersHungerLoss && Main.hunters.contains(p)) {
            e.setCancelled(true);
        }
        else if (!Main.suicidalHungerLoss && !Main.hunters.contains(p)) {
            e.setCancelled(true);
        }
    }
}