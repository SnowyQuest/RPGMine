package net.snowyquest.rpgmine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import com.google.common.collect.Maps;
import de.obey.crown.core.data.plugin.CrownConfig;
import de.obey.crown.core.util.FileUtil;

public final class PluginConfig extends CrownConfig {

    private final YamlConfiguration configuration;
    
    private final List<Material> blockTypes = new ArrayList<>();
    private final Map<Block, Long> pendingBlocks = Maps.newConcurrentMap();
    
    private World mineWorld;
    private int blockRefillDelay;

    public PluginConfig(Plugin plugin) {
        super(plugin);
        this.configuration = YamlConfiguration.loadConfiguration(this.getConfigFile());
        this.mineWorld = Bukkit.getWorld(FileUtil.getString(configuration, "mine-world", "world"));
        this.blockRefillDelay = FileUtil.getInt(configuration, "block-refill-delay", 10);
        
        Iterator<String> blockKeyIterator =
        configuration.getConfigurationSection("blocks")
                     .getKeys(false)
                     .iterator();

        while(blockKeyIterator.hasNext()) {
            String blockType = blockKeyIterator.next();

            for(int i = 0; (double)i < FileUtil.getDouble(this.configuration, "blocks." + blockType + ".chance", 0.0D) * 10.0D; ++i) {
                this.blockTypes.add(Material.getMaterial(blockType));
            }
        }
        
        FileUtil.saveConfigurationIntoFile(this.configuration, this.getConfigFile());
    }

    public void setMineWorld(World world) {
        this.mineWorld = world;
        this.configuration.set("mine-world", world.getName());
        FileUtil.saveConfigurationIntoFile(this.configuration, this.getConfigFile());
    }

    public void setMineDelay(int delay) {
        this.blockRefillDelay = delay;
        this.configuration.set("block-refill-delay", delay);
        FileUtil.saveConfigurationIntoFile(this.configuration, this.getConfigFile());
    }

    public World getMineWorld() {
        return this.mineWorld;
    }

    public List<Material> getBlockTypes() {
        return this.blockTypes;
    }

    public Map<Block, Long> getPendingBlocks() {
        return this.pendingBlocks;
    }

    public int getBlockRefillDelay() {
        return this.blockRefillDelay;
    }
}
