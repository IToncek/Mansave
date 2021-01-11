// 
// Decompiled by Procyon v0.5.36
// 

package cz.itoncek.mansave;

import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Autocomplete implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final List<String> arguments = new ArrayList<String>();
        if (args.length == 1) {
            arguments.add("addHunter");
            arguments.add("removeHunter");
            arguments.add("setHunterItems");
            arguments.add("start");
            arguments.add("stop");
            arguments.add("huntersTakeDamage");
            arguments.add("huntersHungerLoss");
            arguments.add("suicidalHungerLoss");
            arguments.add("countdownInSeconds");
        }
        else if (args.length == 2) {
            if (args[0].equals("addHunter")) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (ManSave.hunters.contains(p)) {
                        continue;
                    }
                    arguments.add(p.getName());
                }
            }
            else if (args[0].equals("removeHunter")) {
                for (final Player p : ManSave.hunters) {
                    arguments.add(p.getName());
                }
            }
            else if (args[0].equals("huntersTakeDamage") || args[0].equals("huntersHungerLoss") || args[0].equals("suicidalHungerLoss")) {
                arguments.add("true");
                arguments.add("false");
            }
            else if (args[0].equals("countdownInSeconds")) {
                arguments.add("[number]");
            }
        }
        return arguments;
    }
}