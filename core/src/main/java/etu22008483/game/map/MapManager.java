package etu22008483.game.map;

import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MapManager {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public MapManager(String path) {
        map = new TmxMapLoader().load(path);
        renderer = new OrthogonalTiledMapRenderer(map);
    }
    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }
    public MapLayer getCollisionLayer() {
        return map.getLayers().get("collisions");
    }
    public MapLayer getTeleportLayer() {
        return map.getLayers().get("teleporters");
    }
    public MapLayer getEnemieLayer() {
        return map.getLayers().get("enemies");
    }
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
    public TiledMap getMap(){
        return map;
    }
}
