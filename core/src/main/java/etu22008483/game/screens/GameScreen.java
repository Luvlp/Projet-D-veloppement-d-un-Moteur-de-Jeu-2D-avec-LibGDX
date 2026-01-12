package etu22008483.game.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import etu22008483.game.entities.Enemy;
import etu22008483.game.entities.Player;
import etu22008483.game.entities.Projectile;
import etu22008483.game.map.MapManager;
import etu22008483.game.world.WorldManager;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private MapManager mapManager;
    private Array<Rectangle> collisions = new Array<>();
    private Array<RectangleMapObject> teleporteurs = new Array<>();
    private WorldManager worldManager;
    private Array<Enemy> enemies = new Array<>();
    private float playerTimer;
    private static final int baseDmg = 1;
    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 640);
        camera.update();
        batch = new SpriteBatch();
        player = new Player(480, 320, 200);
        camera.position.set(player.getX(), player.getY(), 0);
        worldManager = new WorldManager(0, 0);
        mapManager = worldManager.getMapManager();
        loadEnemiesFromMap();
        loadTeleporteurs();
        loadCollisions();
    }
    public void loadTeleporteurs() {
        teleporteurs.clear();
        MapLayer teleportersLayer = worldManager.getMapManager().getTeleportLayer();
        if (teleportersLayer != null) {
            for (MapObject obj : teleportersLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    teleporteurs.add((RectangleMapObject) obj);
                }
            }
        }
    }
    public void loadCollisions() {
        collisions.clear();
        MapLayer CollisionsLayer = worldManager.getMapManager().getCollisionLayer();
        if (CollisionsLayer != null) {
            for (MapObject object : CollisionsLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    collisions.add(rect);
                }
            }
        }
    }
    private void loadEnemiesFromMap() {
        enemies.clear();
        MapLayer enemyLayer = worldManager.getMapManager().getEnemieLayer();
        if (enemyLayer != null) {
            for (MapObject obj : enemyLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    int health = obj.getProperties().get("HP", Integer.class);
                    float speed = obj.getProperties().get("speed", Float.class);
                    Enemy e = new Enemy(rect.x, rect.y, new Texture("sprites/enemy.png"));
                    e.setHP(health);
                    e.setSpeed(speed);
                    enemies.add(e);
                }
            }
        }
    }
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapManager.render(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        player.renderProjectiles(batch);
        player.renderHealthBar(batch);
        for (Enemy e : enemies) {
            e.render(batch);
        }
        batch.end();

    }
    private void update(float delta) {
        playerTimer += delta;
        Vector2 oldPos = new Vector2(player.getBounds().x, player.getBounds().y);
        player.update(delta);
        player.updateProjectiles(delta);
        killEnemy();
        for (Rectangle rect : collisions) {
            if (player.getBounds().overlaps(rect)) {
                player.setPos(oldPos.x, oldPos.y);
                break;
            }
        }
        if (Gdx.input.justTouched()) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            player.moveTo(mousePos.x, mousePos.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            if (playerTimer >= player.getCooldown()){
                playerTimer = 0;
                player.shoot(mousePos.x, mousePos.y);}

        }
        for (RectangleMapObject teleporteur : teleporteurs) {
            Rectangle rect = teleporteur.getRectangle();

            if (player.getBounds().overlaps(rect)) {
                int targetX = teleporteur.getProperties().get("targetX", Integer.class);
                int targetY = teleporteur.getProperties().get("targetY", Integer.class);
                worldManager.changeMap(targetX, targetY);
                player.setPos(480, 320);
                player.resetDest(new Vector2(480, 320));
                mapManager = worldManager.getMapManager();
                teleporteurs.clear();
                collisions.clear();
                loadCollisions();
                loadTeleporteurs();
                loadEnemiesFromMap();
                camera.position.set(480, 320, 0);
                camera.update();
                break;
            }
        }
        for (Enemy e : enemies) {
            for (Projectile projectile : e.getProjectiles()) {
                if (projectile.getBounds().overlaps(player.getBounds())) {
                    projectile.destroy();
                    player.takeDamage(5);
                }
            }
        }
        for (Projectile projectile : player.getProjectiles()) {
            for (Enemy enemy : enemies) {
                if (projectile.getBounds().overlaps(new Rectangle(enemy.getPos().x, enemy.getPos().y, 32, 32))) {
                    projectile.destroy();
                    enemy.takeDamage((player.getLvl() * baseDmg));
                }
            }
        }
        for (Enemy e : enemies) {
            e.update(delta, player);
        }
        if (player.isDead()) {
            camera.position.set(480, 320, 0);
            camera.update();
            worldManager.changeMap(0, 0);
            mapManager = worldManager.getMapManager();
            player.setPos(480, 320);
            player.resetDest(new Vector2(480, 320));
            player.deathReset();
            teleporteurs.clear();
            collisions.clear();
            enemies.clear();
            loadTeleporteurs();
            loadCollisions();
            loadEnemiesFromMap();
        }
    }
    public void killEnemy() {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.isDead()) {
                enemy.clear();
                enemies.removeIndex(i);
                player.levelUp();
            }
        }
    }
        @Override
        public void show () {
        }
        @Override
        public void resize ( int width, int height){
        }
        @Override
        public void pause () {
        }
        @Override
        public void resume () {
        }
        @Override
        public void hide () {
        }
        @Override
        public void dispose () {
        }
}
