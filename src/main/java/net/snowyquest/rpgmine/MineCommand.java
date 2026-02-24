package net.snowyquest.rpgmine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import de.obey.crown.core.data.plugin.Messanger;

public final class MineCommand implements CommandExecutor, TabCompleter {

    private final PluginConfig pluginConfig;
    private final Messanger messanger;

    public MineCommand(PluginConfig pluginConfig, Messanger messanger) {
        this.pluginConfig = Objects.requireNonNull(pluginConfig);
        this.messanger = Objects.requireNonNull(messanger);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!this.messanger.hasPermission(sender, "command.mine")) {
                return false;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("setworld")) {
                World world = player.getWorld();
                this.pluginConfig.setMineWorld(world);
                this.messanger.sendMessage(sender, "mine-world-set",
                        new String[]{"world"},
                        new String[]{world.getName()});
                return false;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("setdelay")) {
                int delay = this.messanger.isValidInt(args[1]);

                if (delay <= 0) {
                    return false;
                }

                this.pluginConfig.setMineDelay(delay);
                this.messanger.sendMessage(sender, "mine-delay-set",
                        new String[]{"delay"},
                        new String[]{String.valueOf(delay)});
                return false;
            }

            return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        ArrayList<String> suggestions = new ArrayList<>();

        if (!sender.hasPermission("command.mine")) {
            return suggestions;
        }

        if (args.length == 1) {
            suggestions.add("setworld");
            suggestions.add("setdelay");
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("setdelay")) {
                suggestions.add("amount");
            }

            if (args[0].equalsIgnoreCase("setworld")) {
                Iterator<World> iterator = Bukkit.getWorlds().iterator();

                while (iterator.hasNext()) {
                    World world = iterator.next();
                    suggestions.add(world.getName());
                }
            }
        }

        String currentArgument = args[args.length - 1];

        if (!currentArgument.isEmpty()) {
            suggestions.removeIf(value ->
                    !value.toLowerCase().startsWith(currentArgument.toLowerCase()));
        }

        return suggestions;
    }
}
