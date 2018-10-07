package engine.singlegame;

public class UIPlayer {
    Logic.Player player;
    String color;

    public UIPlayer(Logic.Player player, String color) {
        this.player = player;
        this.color = color;
    }

    public int getId()
    {
        return player.getId();
    }
}
