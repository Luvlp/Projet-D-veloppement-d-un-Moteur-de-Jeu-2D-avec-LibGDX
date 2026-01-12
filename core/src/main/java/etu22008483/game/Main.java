package etu22008483.game;

import com.badlogic.gdx.Game;
import etu22008483.game.screens.GameScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
