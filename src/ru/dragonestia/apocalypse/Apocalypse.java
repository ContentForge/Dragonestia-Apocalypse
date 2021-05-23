package ru.dragonestia.apocalypse;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import ru.dragonestia.apocalypse.player.PlayerManager;
import ru.dragonestia.apocalypse.player.PlayerData;
import ru.dragonestia.apocalypse.commands.ItemDataCommand;
import ru.dragonestia.apocalypse.commands.RadioCommand;
import ru.dragonestia.apocalypse.commands.SendRadioCommand;
import ru.dragonestia.apocalypse.commands.StormCommand;
import ru.dragonestia.apocalypse.item.ApocalypseID;
import ru.dragonestia.apocalypse.item.*;
import ru.dragonestia.apocalypse.level.ApocalypseGenerator;
import ru.dragonestia.apocalypse.level.biome.ApocalypseBiome;
import ru.dragonestia.apocalypse.level.biome.CommonBiome;
import ru.dragonestia.apocalypse.level.biome.AshBiome;
import ru.dragonestia.apocalypse.level.biome.FireBiome;
import ru.dragonestia.apocalypse.level.populator.cluster.Cluster;
import ru.dragonestia.apocalypse.listener.ChatListener;
import ru.dragonestia.apocalypse.listener.MainListener;
import ru.dragonestia.apocalypse.storms.GlobalEvents;
import ru.jl1mbo.scoreboard.manager.ScoreboardManager;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Apocalypse extends PluginBase {

    private static Apocalypse instance;
    public Cluster[] clusters;
    public final String contentPath = "plugins/Hardcore/";
    private final PlayerManager playerManager = new PlayerManager(this);
    private Level gameLevel;
    private final GlobalEvents globalEvents = new GlobalEvents(this);

    @Override
    public void onLoad() {
        instance = this;

        Item.list[ApocalypseID.GUIDE_BOOK] = GuideItem.class;
        Item.list[ApocalypseID.BATTERY] = BatteryItem.class;
        Item.list[ApocalypseID.CHIP] = ChipItem.class;
        Item.list[ApocalypseID.COPPER_WIRE] = CopperWireItem.class;
        Item.list[ApocalypseID.SCRAP] = ScrapItem.class;
        Item.list[ApocalypseID.CLOTH] = ClothItem.class;
        Item.list[ApocalypseID.PLASTIC] = PlasticItem.class;
        Item.list[ApocalypseID.SULFUR_CLUSTER] = SulfurClusterItem.class;
        Item.list[ApocalypseID.SULFUR_DUST] = SulfurDustItem.class;
        Item.list[ApocalypseID.IRON_CLUSTER] = IronClusterItem.class;
        Item.list[ApocalypseID.COPPER_CLUSTER] = CopperClusterItem.class;
        Item.list[ApocalypseID.TIN_CLUSTER] = TinClusterItem.class;
        Item.list[ApocalypseID.COPPER_INGOT] = CopperIngotItem.class;
        Item.list[ApocalypseID.TIN_INGOT] = TinIngotItem.class;
        Item.list[ApocalypseID.COPPER_NUGGET] = CopperNuggetItem.class;
        Item.list[ApocalypseID.TIN_NUGGET] = TinNuggetItem.class;

        Biome.biomes[ApocalypseGenerator.ASH_BIOME] = new AshBiome();
        Biome.biomes[ApocalypseGenerator.FIRE_BIOME] = new FireBiome();
        Biome.biomes[ApocalypseGenerator.COMMON_BIOME] = new CommonBiome();

        clusters = new Cluster[]{

        };

        Generator.addGenerator(ApocalypseGenerator.class, "apocalypse", Generator.TYPE_INFINITE);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MainListener(this, clusters), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getServer().getCommandMap().registerAll("", Arrays.asList(
                new ItemDataCommand(),
                new RadioCommand(this),
                new SendRadioCommand(this),
                new StormCommand(globalEvents)
        ));

        gameLevel = getServer().getDefaultLevel();

        for(Player player: getServer().getOnlinePlayers().values()){
            playerManager.initPlayer(player);
        }

        globalEvents.start();
    }

    @Override
    public void onDisable() {
        playerManager.unloadAllRadio();
    }

    public static Apocalypse getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GlobalEvents getGlobalEvents() {
        return globalEvents;
    }

    public Level getGameLevel() {
        return gameLevel;
    }

    public void initScoreboard(Player player) {
        ScoreboardManager.createScoreboard(player)
                .setDisplayName("§c§lDrаgonestia§f - §4Hardcore")
                .setLine(1, "§fРадио: §2§l???Мгц")
                .setLine(2, "§fРадиация: §g§l0 мРентген/с")
                .setLine(3, " ")
                .setLine(4, "")
                .setLine(5, "§9vk.com/dragonestia")
                .addUpdater(sb -> {
                    PlayerData playerData = playerManager.get(player);
                    if (playerData == null) return;
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    String eventMessage = globalEvents.currentEvent.generateTitleMessage(player, globalEvents.time);
                    Biome biome = Biome.getBiome(player.getLevel().getBiomeId(player.getFloorX(), player.getFloorZ()));
                    double rad = 0, radGround = 0;
                    if(biome instanceof ApocalypseBiome){
                        rad = radGround = ((ApocalypseBiome) biome).getRadioactiveLevel().getGroundDose();
                    }

                    sb.setLine(1, "§fR: §2§l" + (playerData.getRadioChannel() / 10.0) + "Мгц§r§f  C: §g§l" + playerData.getRadioCharge() + "EU§r§f  D: §l§3" + (playerData.getRadioDistance() / 1000.0) + "км")
                            .setLine(2, "§fРадиация: §g§l" + decimalFormat.format(rad) +" мР/с§f |§r Фон: §l§e" + decimalFormat.format(radGround) + " мР/с")
                            .setLine(3, (eventMessage == null) ? "  " : eventMessage);
                }, 1).show();
    }

}
