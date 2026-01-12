package etu22008483.game.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Vector2 pos;
    private float cooldown = 1f;
    private float timer = 0f;
    private Texture texture;
    private Array<Projectile> projectiles = new Array<>();
    private Texture projectileTex = new Texture("sprites/bullet.png");
    private int HP;
    private float speed;
    public Enemy(float x, float y, Texture tex) {
        this.pos = new Vector2(x, y);
        this.texture = tex;
    }
    public void update(float delta, Player player) {
        timer += delta;
        if (timer >= cooldown) {
            timer = 0;
            shootAt(player.getCenterX(), player.getCenterY());
        }
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            if (!p.isAlive()) projectiles.removeIndex(i);
        }
    }
    private void shootAt(float targetX, float targetY) {
        Vector2 dir = new Vector2(targetX - pos.x, targetY - pos.y);
        Projectile p = new Projectile(pos.x, pos.y, dir, projectileTex);
        projectiles.add(p);
    }
    public void render(SpriteBatch batch) {
        batch.draw(texture, pos.x, pos.y);
        for (Projectile p : projectiles) p.render(batch);
    }
    public Array<Projectile> getProjectiles() {
        return projectiles;
    }
    public Vector2 getPos() {
        return pos;
    }
    public void takeDamage(int quantity){
        this.HP = this.HP - quantity;
    }
    public boolean isDead(){
        return (this.HP < 1);
    }
    public void setHP(int hp){
        this.HP = hp;
    }
    public void setSpeed(float speed){
        this.speed = speed;
    }
    public void clear() {
        for (Projectile p : projectiles) {
            p.destroy();
        }
        projectiles.clear();
        if (texture != null) {
            texture.dispose();
        }
    }

}
