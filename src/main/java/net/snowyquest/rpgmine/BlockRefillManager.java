package net.snowyquest.rpgmine;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

public final class BlockRefillManager {

    private final PluginConfig pluginConfig;

    public BlockRefillManager(PluginConfig pluginConfig) {
        this.pluginConfig = Objects.requireNonNull(pluginConfig);
    }

    public void startTask() {
        Bukkit.getScheduler().runTaskTimer(RPGMinePlugin.getInstance(), () -> {

            if (!this.pluginConfig.getPendingBlocks().isEmpty()) {

                Iterator<Block> iterator = this.pluginConfig.getPendingBlocks().keySet().iterator();

                while (iterator.hasNext()) {
                    Block block = iterator.next();

                    long placedTime = this.pluginConfig.getPendingBlocks().get(block);
                    long delayMillis = 1000L * this.pluginConfig.getBlockRefillDelay();

                    if (block.getType() == Material.BEDROCK &&
                            System.currentTimeMillis() - placedTime >= delayMillis) {

                        block.setType(getRandomBlockType());
                        iterator.remove();
                    }
                }
            }

        }, 100L, 20L);
    }

    public void refillAllImmediately() {
        if (!this.pluginConfig.getPendingBlocks().isEmpty()) {

            Iterator<Block> iterator = this.pluginConfig.getPendingBlocks().keySet().iterator();

            while (iterator.hasNext()) {
                Block block = iterator.next();

                if (block.getType() == Material.BEDROCK) {
                    block.setType(getRandomBlockType());
                }

                iterator.remove();
            }
        }
    }

    public Material getRandomBlockType() {
        if (this.pluginConfig.getBlockTypes().isEmpty()) {
            return Material.AIR;
        }

        return this.pluginConfig.getBlockTypes()
                .get(new Random().nextInt(this.pluginConfig.getBlockTypes().size()));
    }

    public boolean isRefillBlock(Block block) {
        return this.pluginConfig.getBlockTypes().contains(block.getType());
    }
}
