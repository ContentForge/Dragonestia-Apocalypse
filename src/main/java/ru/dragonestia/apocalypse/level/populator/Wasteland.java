package ru.dragonestia.apocalypse.level.populator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.noise.nukkit.d.SimplexD;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import ru.dragonestia.apocalypse.level.ApocalypseGenerator;
import ru.dragonestia.apocalypse.level.populator.wastelands.RadioTower;
import ru.dragonestia.apocalypse.level.populator.wastelands.StoneHouse;
import ru.dragonestia.apocalypse.level.populator.wastelands.WastelandPopulator;

import java.util.Random;

public class Wasteland extends Populator {

    private final SimplexD cityNoise;
    private final WastelandPopulator[] populators;

    public Wasteland(Random random, SimplexD cityNoise){
        this.cityNoise = cityNoise;

        populators = new WastelandPopulator[]{
                new RadioTower(random),
                new StoneHouse(random),
        };
    }

    @Override
    public void populate(ChunkManager chunkManager, int chunkX, int chunkZ, NukkitRandom nukkitRandom, FullChunk chunk) {
        if(ApocalypseGenerator.isCity(cityNoise, chunkX, chunkZ)) return;

        for(WastelandPopulator populator: populators){
            if(!populator.checkPlace(chunkX, chunkZ, chunk)) continue;

            populator.populate(chunkManager, chunkX, chunkZ, nukkitRandom, chunk);
            break;
        }
    }

}
