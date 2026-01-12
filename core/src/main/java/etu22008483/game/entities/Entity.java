package etu22008483.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 pos;
    protected float speed;
    protected Vector2 dest;
    protected Texture texture;
    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
}
