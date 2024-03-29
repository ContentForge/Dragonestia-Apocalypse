package ru.dragonestia.apocalypse.level.populator.section;

import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import ru.dragonestia.apocalypse.level.ApocalypseGenerator;
import ru.dragonestia.apocalypse.level.populator.section.filter.VoidFilter;

import java.util.Random;

public class BrickHouse extends HouseSection {

    public BrickHouse(Random random, ChunkManager chunkManager) {
        super(random, chunkManager);
    }

    @Override
    protected void generate(BlockPlacer placer, FullChunk chunk, boolean broken) {
        int h = chunk.getHighestBlockAt(7, 7) - 6 - random.nextInt(4);
        int levels = 5 + random.nextInt(7);
        VoidFilter voidFilter = new VoidFilter(h, h + levels*4, (broken? 1 : 0) + random.nextInt(2), random);
        for(int i = 1; i < levels; i++) buildLevel(h + i*4, placer, chunk, voidFilter, broken);

        //Крыша
        for(int x = 0; x < 16; x++)
            for(int z = 0; z < 16; z++){
                for(int y = levels * 4 + h, ty = y; ty < y + 2; ty++){
                    if(x > 0 && x < 15 && z > 0 && z < 15){
                        if(y != ty || (x < 3 && z < 4) || (broken && random.nextFloat() > 0.4f)) continue;
                        placeFloor(x, ty, z, placer, voidFilter);
                        continue;
                    }
                    placeWall(x, ty, z, placer, voidFilter, broken);
                }
            }
    }

    private void buildLevel(int dy, BlockPlacer blockPlacer, FullChunk chunk, VoidFilter voidFilter, boolean broken){
        //Стены
        for(int y = 0; y < 4; y++){
            for(int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (x > 0 && x < 15 && z > 0 && z < 15) continue;
                    if ((y == 2 || y == 3) && (x % 3 != 0 || z % 3 != 0)) continue;
                    placeWall(x, y + dy, z, blockPlacer, voidFilter, broken);
                }
            }
        }

        //Перегородки
        for(int x = 1; x < 7; x++){
            for(int y = 0; y < 4; y++){
                if(x == 4 && (y == 1 || y == 2)) continue;
                placeSymmetryWall(x, y + dy, 6, blockPlacer, voidFilter, broken);
            }
        }
        for(int z = 6; z < 15; z++){
            for(int y = 0; y < 4; y++){
                if((z == 11 || z == 13) && (y == 1 || y == 2)) continue;
                placeSymmetryWall(6, y + dy, z, blockPlacer, voidFilter, broken);
            }
        }
        for(int x = 6; x < 9; x++){
            for(int y = 0; y < 4; y++) placeSymmetryWall(x, y + dy, 9, blockPlacer, voidFilter, broken);
        }
        for(int x = 6; x < 12; x++){
            for(int y = 0; y < 4; y++) placeSymmetryWall(x, y + dy, 12, blockPlacer, voidFilter, broken);
        }
        for(int z = 10; z < 12; z++){
            for(int y = 0; y < 4; y++) placeSymmetryWall(9, y + dy, z, blockPlacer, voidFilter, broken);
        }
        for(int z = 13; z < 15; z++){
            for(int y = 0; y < 4; y++) placeSymmetryWall(12, y + dy, z, blockPlacer, voidFilter, broken);
        }

        //Лесница
        placeFloor(2, dy+1, 1, blockPlacer, voidFilter);
        placeFloor(1, dy+2, 1, blockPlacer, voidFilter);
        placeFloor(1, dy+2, 2, blockPlacer, voidFilter);
        placeFloor(1, dy+2, 3, blockPlacer, voidFilter);
        placeFloor(2, dy+3, 3, blockPlacer, voidFilter);
        for(int x = 1; x < 6; x++)
            for(int z = 1; z < 6; z++){
                if(x < 3 && z < 4) continue;
                placeFloor(x, dy, z, blockPlacer, voidFilter);
            }

        //Пол
        for(int x = 1; x < 6; x++)
            for(int z = 7; z < 15; z++)
                placeSymmetryFloor(x, dy, z, blockPlacer, voidFilter);
        for(int x = 7; x < 9; x++)
            for(int z = 10; z < 12; z++)
                placeSymmetryFloor(x, dy, z, blockPlacer, voidFilter);
        for(int x = 7; x < 12; x++)
            for(int z = 13; z < 15; z++)
                placeSymmetryFloor(x, dy, z, blockPlacer, voidFilter);

        //Коробки
        placeBox(12, dy+1, 1, blockPlacer, voidFilter);
        placeBox(13, dy+1, 11, blockPlacer, voidFilter);
        placeBox(10, dy+1, 7, blockPlacer, voidFilter);
    }

    private void placeSymmetryWall(int x, int y, int z, BlockPlacer blockPlacer, VoidFilter voidFilter, boolean broken){
        placeWall(x, y, z, blockPlacer, voidFilter, broken);
        placeWall(z, y, x, blockPlacer, voidFilter, broken);
    }

    private void placeWall(int x, int y, int z, BlockPlacer placer, VoidFilter voidFilter, boolean broken){
        if(voidFilter.check(x, y, z)) return;
        switch (random.nextInt(5)){
            case 1:
                placer.setBlock(x, y, z, 172, 0);
                break;
            case 2:
                placer.setBlock(x, y, z, 1, 1);
                break;

            default:
                placer.setBlock(x, y, z, (broken && random.nextFloat() > 0.6f)? Item.IRON_BARS : Item.BRICKS_BLOCK, 0);
        }
    }

    private void placeFloor(int x, int y, int z, BlockPlacer placer, VoidFilter voidFilter){
        if(voidFilter.check(x, y, z)) return;
        if(random.nextFloat() > 0.3f) placer.setBlock(x, y, z, 1, 0);
        else placer.setBlock(x, y, z, Item.COBBLE, 0);
    }

    private void placeSymmetryFloor(int x, int y, int z, BlockPlacer blockPlacer, VoidFilter voidFilter){
        int id = Item.COBBLE, damage = 0;
        switch (random.nextInt(7)){
            case 0:
                if(blockPlacer.getBiome(x, z) != ApocalypseGenerator.COMMON_BIOME){
                    id = 0;
                    break;
                }
                id = Item.PLANK;
                break;
            case 2:
            case 1:
                id = Item.SLAB;
                damage = 3;
                break;
            case 3:
            case 4:
                id = Item.GRAVEL;
                break;
        }

        if(!voidFilter.check(x, y, z)) blockPlacer.setBlock(x, y, z, id, damage);
        if(!voidFilter.check(z, y, x)) blockPlacer.setBlock(z, y, x, id, damage);
    }

    private void placeBox(int x, int y, int z, BlockPlacer placer, VoidFilter voidFilter){
        if(!voidFilter.check(x, y, z) && random.nextFloat() < 0.05f) placer.setBlock(x, y, z, Item.JUKEBOX);
        if(!voidFilter.check(z, y, x) && random.nextFloat() < 0.05f) placer.setBlock(z, y, x, Item.JUKEBOX);
    }

}
