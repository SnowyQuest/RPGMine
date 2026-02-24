package net.snowyquest.rpgmine;

import java.util.Iterator;
import java.util.Objects;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import de.obey.crown.core.util.InventoryUtil;

public final class MineBlockListener implements Listener {

    private final PluginConfig pluginConfig;
    private final BlockRefillManager blockRefillManager;

    public MineBlockListener(PluginConfig pluginConfig,
                             BlockRefillManager blockRefillManager) {
        this.pluginConfig = Objects.requireNonNull(pluginConfig);
        this.blockRefillManager = Objects.requireNonNull(blockRefillManager);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player.getWorld() == this.pluginConfig.getMineWorld()) {

            if (player.getGameMode() == GameMode.SURVIVAL) {

                if (!this.blockRefillManager.isRefillBlock(event.getBlock())) {
                    event.setCancelled(true);
                } else {
                    player.playEffect(
                            event.getBlock().getLocation(),
                            Effect.MOBSPAWNER_FLAMES,
                            10
                    );
                }
            }
        }
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {

        Player player = event.getPlayer();

        if (player.getWorld() == this.pluginConfig.getMineWorld()) {

            if (player.getGameMode() == GameMode.SURVIVAL) {

                event.getBlock().setType(Material.BEDROCK);
                this.pluginConfig.getPendingBlocks()
                        .put(event.getBlock(), System.currentTimeMillis());

                if (!event.getItems().isEmpty()) {

                    player.playSound(
                            player.getLocation(),
                            Sound.ENTITY_ITEM_PICKUP,
                            0.2F,
                            2.0F
                    );

                    Iterator<Item> iterator = event.getItems().iterator();

                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        InventoryUtil.addItemToPlayer(player, item.getItemStack());
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
