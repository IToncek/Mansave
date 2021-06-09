
package cz.itoncek.mansave;

import com.pieterdebot.biomemapping.Biome;
import com.pieterdebot.biomemapping.BiomeMappingAPI;
import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class ManSave extends JavaPlugin implements Listener
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
        ManSave.hunterSlot = new ItemStack[9];
    }

    public void onEnable() {
        this.saveDefaultConfig();
        ManSave.huntersTakeDamage = this.getConfig().getBoolean("huntersTakeDamage");
        ManSave.huntersHungerLoss = this.getConfig().getBoolean("huntersHungerLoss");
        ManSave.suicidalHungerLoss = this.getConfig().getBoolean("suicidalHungerLoss");
        ManSave.countdownInSeconds = this.getConfig().getInt("countdownInSeconds");
        ManSave.hunterSlot[0] = this.getConfig().getItemStack("hunterSlot0");
        ManSave.hunterSlot[1] = this.getConfig().getItemStack("hunterSlot1");
        ManSave.hunterSlot[2] = this.getConfig().getItemStack("hunterSlot2");
        ManSave.hunterSlot[3] = this.getConfig().getItemStack("hunterSlot3");
        ManSave.hunterSlot[4] = this.getConfig().getItemStack("hunterSlot4");
        ManSave.hunterSlot[5] = this.getConfig().getItemStack("hunterSlot5");
        ManSave.hunterSlot[6] = this.getConfig().getItemStack("hunterSlot6");
        ManSave.hunterSlot[7] = this.getConfig().getItemStack("hunterSlot7");
        ManSave.hunterSlot[8] = this.getConfig().getItemStack("hunterSlot8");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("mansave").setTabCompleter((TabCompleter)new Autocomplete());
        ManSave.hunters = new ArrayList<Player>();
        ManSave.inGame = false;
        BiomeMappingAPI api = new BiomeMappingAPI();
        try {
            api.replaceBiomes(Biome.LUKEWARM_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.COLD_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.WARM_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.FROZEN_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.DEEP_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.DEEP_FROZEN_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.DEEP_COLD_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.DEEP_LUKEWARM_OCEAN, Biome.FOREST);
            api.replaceBiomes(Biome.DEEP_WARM_OCEAN, Biome.FOREST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        this.getConfig().set("huntersTakeDamage", (Object)ManSave.huntersTakeDamage);
        this.getConfig().set("huntersHungerLoss", (Object)ManSave.huntersHungerLoss);
        this.getConfig().set("suicidalHungerLoss", (Object)ManSave.suicidalHungerLoss);
        this.getConfig().set("countdownInSeconds", (Object)ManSave.countdownInSeconds);
        this.getConfig().set("hunterSlot0", (Object)ManSave.hunterSlot[0]);
        this.getConfig().set("hunterSlot1", (Object)ManSave.hunterSlot[1]);
        this.getConfig().set("hunterSlot2", (Object)ManSave.hunterSlot[2]);
        this.getConfig().set("hunterSlot3", (Object)ManSave.hunterSlot[3]);
        this.getConfig().set("hunterSlot4", (Object)ManSave.hunterSlot[4]);
        this.getConfig().set("hunterSlot5", (Object)ManSave.hunterSlot[5]);
        this.getConfig().set("hunterSlot6", (Object)ManSave.hunterSlot[6]);
        this.getConfig().set("hunterSlot7", (Object)ManSave.hunterSlot[7]);
        this.getConfig().set("hunterSlot8", (Object)ManSave.hunterSlot[8]);
        this.saveConfig();
    }
//commands
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final String cmd = command.getName().toLowerCase();
        if (cmd.equals("mansave")) {
            if (args.length == 1) {
                if (args[0].equals("start")) {
                    if (ManSave.inGame) {
                        sender.sendMessage(ChatColor.RED + "Hra již začala!");
                    }
                    else {
                        this.startGame();
                        World world = Bukkit.getWorld("world");
                        WorldBorder border = world.getWorldBorder();
                        border.setSize(400.0, 10);
                        border.setCenter(0.0,0.0);
                    }
                    return true;
                }
                if (args[0].equals("stop")) {
                    if (!ManSave.inGame) {
                        sender.sendMessage(ChatColor.RED + "Hra ještě nezačala!");
                    }
                    else {
                        this.stopGame("Hra ukončena!");
                    }
                    return true;
                }
            }
            else if (args.length == 2) {
                if (args[0].equals("addHunter")) {
                    final Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Hráč neexistuje.");
                        return false;
                    }
                    ManSave.hunters.add(player);
                    sender.sendMessage(ChatColor.GREEN + player.getName() + " je odteď lovec.");
                    return true;
                }
                else if (args[0].equals("removeHunter")) {
                    final Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Hráč neexistuje.");
                        return false;
                    }
                    ManSave.hunters.remove(player);
                    sender.sendMessage(ChatColor.GREEN + player.getName() + " již není lovec.");
                    return true;
                }
                else {
                    if (args[0].equals("huntersTakeDamage")) {
                        ManSave.huntersTakeDamage = Boolean.parseBoolean(args[1]);
                        if (ManSave.huntersTakeDamage) {
                            sender.sendMessage(ChatColor.GREEN + "Lovci si budou ubírat damage.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "Lovci si nebudou ubírat damage.");
                        }
                        return true;
                    }
                    if (args[0].equals("huntersHungerLoss")) {
                        ManSave.huntersHungerLoss = Boolean.parseBoolean(args[1]);
                        if (ManSave.huntersHungerLoss) {
                            sender.sendMessage(ChatColor.GREEN + "Lovcům bude ubývat hunger.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "Lovcům nebude ubývat hunger.");
                        }
                        return true;
                    }
                    if (args[0].equals("suicidalHungerLoss")) {
                        ManSave.suicidalHungerLoss = Boolean.parseBoolean(args[1]);
                        if (ManSave.suicidalHungerLoss) {
                            sender.sendMessage(ChatColor.GREEN + "Sebevrahovi bude ubývat hunger.");
                        }
                        else {
                            sender.sendMessage(ChatColor.GREEN + "Sebevrahovi nebude ubývat hunger.");
                        }
                        return true;
                    }
                    if (args[0].equals("countdownInSeconds")) {
                        ManSave.countdownInSeconds = Integer.parseInt(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "Odpočet nastaven na " + ManSave.countdownInSeconds + " sekund.");
                        return true;
                    }
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Nápověda:");
        sender.sendMessage(ChatColor.RED + "/mansave start - Začne hru");
        sender.sendMessage(ChatColor.RED + "/mansave stop - Ukončí hru");
        sender.sendMessage(ChatColor.RED + "/mansave addHunter <hráč> - Přidá hráče do seznamu lovců");
        sender.sendMessage(ChatColor.RED + "/mansave removeHunter <hráč> - odebere hráče ze seznamu lovců");
        sender.sendMessage(ChatColor.RED + "/mansave huntersTakeDamage <true|false> - false = lovci si nemůžou ubrat damage");
        sender.sendMessage(ChatColor.RED + "/mansave huntersHungerLoss <true|false> - false = lovci neztrácí hlad.");
        sender.sendMessage(ChatColor.RED + "/mansave suicidalHungerLoss <true|false> - false = speedrunnerovi neubývá hunger");
        sender.sendMessage(ChatColor.RED + "/mansave countdownInSeconds <sekundy> - časový limit (normálně 300s = 5min)");
        return false;
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (!ManSave.inGame) {
            return;
        }
        final Player p = e.getEntity();
        if (ManSave.hunters.contains(p)) {
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
        else {
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setDeathMessage(String.valueOf(e.getDeathMessage()) + " a vyhrál!");
            this.stopGame("Lovci prohráli!");
            for (final Player player : Bukkit.getOnlinePlayers()) {
                for (final PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                player.setFireTicks(0);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setTotalExperience(0);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
    @EventHandler
    public void onJoin (PlayerJoinEvent e)
    {
        Player pl = e.getPlayer();
        pl.setGameMode(GameMode.SPECTATOR);
        e.getPlayer().getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        e.getPlayer().getWorld().setTime(6000L);
        e.getPlayer().getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        e.getPlayer().getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        e.getPlayer().getWorld().setDifficulty(Difficulty.NORMAL);
    }

    public void startGame() {
        final ScoreboardManager m = Bukkit.getScoreboardManager();
        final Scoreboard b = m.getNewScoreboard();
        final Objective o = b.registerNewObjective("timer", "", "Odpočet");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        final Score s = o.getScore("Čas");
        s.setScore(ManSave.countdownInSeconds);
        this.countdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)getPlugin((Class)ManSave.class), (Runnable)new Runnable() {
            @Override
            public void run() {
                final int score = s.getScore() - 1;
                if (score == 0) {
                    ManSave.this.stopGame("Konec! Lovci vyhráli!");
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
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append("Hra začala! Hodně štěstí!").toString());
            if (!ManSave.hunters.contains(p) && ManSave.suicidalHungerLoss) {
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COOKED_BEEF, 64) });
            }
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3.0F, 1);
            Location loc = new Location(Bukkit.getWorld("world"), 0, 255, 0, 0, 0);
            loc.setY(255);
            World world = Bukkit.getWorld("world");
            final RayTraceResult result = world.rayTraceBlocks(loc, new Vector(0, -1, 0), 255);
            final Location finaloc = result.getHitBlock().getLocation();
            p.teleport((Location) finaloc);
        }
        for (final Player h : ManSave.hunters) {
            this.giveHunterItems(h);
        }
        ManSave.inGame = true;
    }

    public void stopGame(final String msg) {
        Bukkit.getServer().getScheduler().cancelTask(this.countdownTask);
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append(msg).toString());
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 3.0F, 1);
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
            p.setGameMode(GameMode.SPECTATOR);
        }
        ManSave.inGame = false;
    }

    public void giveHunterItems(final Player p) {
        final Inventory inv = (Inventory)p.getInventory();
        for (int i = 0; i <= 8; ++i) {
            inv.addItem(new ItemStack[] { ManSave.hunterSlot[i] });
        }
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent e) {
        if (!ManSave.inGame) {
            return;
        }
        if (ManSave.huntersTakeDamage) {
            return;
        }
        if (e.getEntityType() == EntityType.PLAYER && ManSave.hunters.contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerDeplete(final FoodLevelChangeEvent e) {
        if (!ManSave.inGame) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        final Player p = (Player)e.getEntity();
        if (!ManSave.huntersHungerLoss && ManSave.hunters.contains(p)) {
            e.setCancelled(true);
        }
        else if (!ManSave.suicidalHungerLoss && !ManSave.hunters.contains(p)) {
            e.setCancelled(true);
        }
    }
}