package ru.dragonestia.apocalypse.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import ru.dragonestia.apocalypse.Apocalypse;
import ru.dragonestia.apocalypse.item.ApocalypseID;
import ru.dragonestia.apocalypse.level.biome.ApocalypseBiome;
import ru.dragonestia.apocalypse.level.populator.cluster.Cluster;
import ru.nukkitx.forms.elements.ImageType;
import ru.nukkitx.forms.elements.SimpleForm;

import java.util.Random;

public class MainListener implements Listener {

    private final Cluster[] clusters;
    private final Apocalypse main;
    private final Random random;

    public MainListener(Apocalypse main, Cluster[] cluster, Random random){
        this.main = main;
        this.clusters = cluster;
        this.random = random;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        if(player.isCreative() && player.getInventory().getItemInHand().getId() == Item.STICK){
            Block block = event.getBlock();
            player.sendMessage("cX: "+(block.getFloorX()%16)+" Y: "+block.getFloorY()+" cZ: "+(block.getFloorZ()%16));
            player.sendMessage("id: "+block.getId()+" meta: "+block.getDamage());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDataPacketSend(DataPacketSendEvent event) {
        DataPacket packet = event.getPacket();
        if (packet instanceof StartGamePacket) {
            ((StartGamePacket) packet).dimension = Level.DIMENSION_THE_END;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();

        for(Cluster cluster: clusters){
            if(block.getId() == cluster.getBlockId()){
                Biome biome = Biome.getBiome(block.level.getBiomeId(block.getFloorX(), block.getFloorZ()));
                if(!(biome instanceof ApocalypseBiome)) return;

                event.setDrops(cluster.getDrop((ApocalypseBiome) biome, block.getFloorX(), block.getFloorY(), block.getFloorZ()));
                return;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        main.getPlayerManager().initPlayer(player);
        main.initScoreboard(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        main.getPlayerManager().unloadRadio(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        main.getPlayerManager().get(player).onDeath();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreakBox(BlockBreakEvent event){
        if(event.isCancelled() || event.isFastBreak() || event.getBlock().getId() != Item.JUKEBOX) return;
        Item drop;

        switch (random.nextInt(8)){
            case 0:
                drop = Item.get(287, 0, random.nextInt(3) + 1);
                break;
            case 1:
                drop = Item.get(ApocalypseID.PLASTIC, 0, random.nextInt(1) + 1);
                break;
            case 2:
                drop = Item.get(ApocalypseID.CLOTH, 0, random.nextInt(2) + 1);
                break;
            case 3:
                drop = Item.get(ApocalypseID.TIN_NUGGET, 0, random.nextInt(4) + 1);
                break;
            case 4:
                drop = Item.get(ApocalypseID.COPPER_NUGGET, 0, random.nextInt(4) + 1);
                break;
            case 5:
                drop = Item.get(ApocalypseID.BATTERY, 0, 1);
                break;
            case 6:
                drop = Item.get(ApocalypseID.YODADULIN, 0, 1);
                break;
            default:
                drop = Item.get(Item.PLANK);
        }

        event.setDrops(new Item[]{drop});
        event.getBlock().getLevel().addSound(event.getBlock(), Sound.MOB_ZOMBIE_WOODBREAK);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorkbenchTap(PlayerInteractEvent event){
        if(event.isCancelled() || !event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) || event.getBlock().getId() != Item.CRAFTING_TABLE) return;
        Player player = event.getPlayer();

        new SimpleForm("Верстак")
                .addButton("Классическое создание предметов", ImageType.PATH, "textures/blocks/crafting_table_top")
                .addButton("Материалы", ImageType.PATH, "textures/items/cloth")
                .addButton("Расходники", ImageType.PATH, "textures/items/upgrade/chip")
                .addButton("Оружейный станок", ImageType.PATH, "textures/items/weapon/ak")
                .addButton("Блоки", ImageType.PATH, "textures/ui/icon_recipe_construction")
                .send(player, (p, f, data) -> {
                    switch (data){
                        case 0: //Классическое создание предметов
                            player.craftingType = 1;
                            ContainerOpenPacket pk = new ContainerOpenPacket();
                            pk.windowId = -1;
                            pk.type = 1;
                            pk.x = (int)event.getBlock().x;
                            pk.y = (int)event.getBlock().y;
                            pk.z = (int)event.getBlock().z;
                            pk.entityId = player.getId();
                            player.dataPacket(pk);
                            break;

                        case 1: //TODO: Материалы
                            break;

                        case 2: //TODO: Расходники
                            break;

                        case 3: //TODO: Оружейный станок
                            break;

                        case 4: //TODO: Блоки
                            break;

                    }
                });

        event.setCancelled(true);
    }

}
