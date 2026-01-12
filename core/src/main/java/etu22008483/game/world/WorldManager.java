package etu22008483.game.world;

import etu22008483.game.map.MapManager;

public class WorldManager {
    private int X;
    private int Y;
    private MapManager mapManager;

    public WorldManager(int startX, int startY) {
        X = startX;
        Y = startY;
        loadMap();
    }
    private void loadMap() {
        String path = "maps/map_" + X + "_" + Y + ".tmx";
        mapManager = new MapManager(path);
    }
    public void changeMap(int newX, int newY) {
        X = newX;
        Y = newY;
        loadMap();
    }
    public MapManager getMapManager() {
        return mapManager;
    }
}
