package etu22008483.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity{
    private Rectangle bounds;
    private Array<Projectile> projectiles = new Array<>();
    private Texture projectileTexture = new Texture("sprites/bullet.png");
    private int HP = 100;
    private int maxHP = 100;
    private float cooldown;
    private float HPBarTimer;
    private static final float HPBarDuration = 1f;
    private int lvl;
    public Player(float x, float y, float speed){
            this.pos = new Vector2(x,y);
            this.speed = speed;
            this.dest = new Vector2(x,y);
            this.texture = new Texture("sprites/player.png");
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
            this.cooldown = 2f;
            this.lvl = 1;
    }
    public float getCenterX() {
        return bounds.x + bounds.width / 2f;
    }
    public float getCenterY() {
        return bounds.y + bounds.height / 2f;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public void shoot(float targetX, float targetY) {
        Vector2 dir = new Vector2(targetX - getCenterX(), targetY - getCenterY());
        Projectile p = new Projectile(getCenterX(), getCenterY(), dir, projectileTexture);
        projectiles.add(p);
    }
    public void updateProjectiles(float delta) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
            if (!p.isAlive()) projectiles.removeIndex(i);
        }
    }
    public void renderProjectiles(SpriteBatch batch) {
        for (Projectile p : projectiles) {
            p.render(batch);
        }
    }
    public Array<Projectile> getProjectiles() { return projectiles; }
    @Override
    public void update(float delta){
        if (!pos.epsilonEquals(dest,1f)){
            Vector2 direction = dest.cpy().sub(pos).nor();
            pos.mulAdd(direction,speed*delta);
            bounds.setPosition(pos);
        }
        if (HPBarTimer > 0){
            HPBarTimer = HPBarTimer - delta;
        }
    }
    private static Texture pixel;
    static {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
    }
    public void renderHealthBar(SpriteBatch batch) {
        if (HPBarTimer <= 0) return;
        float barWidth = 32;
        float barHeight = 5;
        float x = getX() - 8;
        float y = getY() + 20;
        float HPRatio = (float) HP / maxHP;
        batch.setColor(1, 0, 0, 1);
        batch.draw(pixel, x, y, barWidth, barHeight);
        batch.setColor(0, 1, 0, 1);
        batch.draw(pixel, x, y, barWidth * HPRatio, barHeight);
        batch.setColor(1, 1, 1, 1);
    }
    @Override
    public void render(SpriteBatch batch){
        batch.draw(texture, pos.x, pos.y);
    }
    public void moveTo(float x ,float y){
        dest.set(x,y);
    }
    public void dispose(){
        texture.dispose();
    }
    public void setPos(float x, float y){
        pos.set(x, y);
        bounds.setPosition(x, y);
    }
    public float getX(){
        return this.pos.x;
    }
    public float getY(){
        return this.pos.y;
    }
    public void resetDest(Vector2 pos){
        this.dest = pos;
    }
    public void takeDamage(int quantity){
        this.HP = this.HP - quantity;
        if (this.HP > 0){
            HPBarTimer = HPBarDuration;
        }
    }
    public boolean isDead(){
        return (this.HP <= 0);
    }
    public float getCooldown(){
        return this.cooldown;
    }
    public int getLvl(){
        return this.lvl;
    }
    public void levelUp(){
        this.lvl = this.lvl + 1;
        if (this.lvl % 2 == 0) {
            this.cooldown = this.cooldown - 0.1f;
        }
    }
    public void deathReset(){
        this.lvl = 1;
        this.HP = this.maxHP;
    }
}
