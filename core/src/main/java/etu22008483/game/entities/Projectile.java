package etu22008483.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {

    private Vector2 pos;
    private Vector2 velocity;
    private float speed = 350f; // pixels/s
    private Texture texture;
    private Rectangle bounds;
    private boolean isAlive = true;

    public Projectile(float x, float y, Vector2 direction, Texture tex) {
        this.pos = new Vector2(x, y);
        this.velocity = direction.nor().scl(speed);
        this.texture = tex;
        this.bounds = new Rectangle(x, y, 16, 16); // taille projectile
    }
    public void update(float delta) {
        pos.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(pos.x, pos.y);
    }
    public void render(SpriteBatch batch) {
        batch.draw(texture, pos.x, pos.y);
    }
    public Rectangle getBounds() { return bounds; }
    public boolean isAlive() { return isAlive; }
    public void destroy() { isAlive = false; }
}
