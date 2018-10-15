package engine.singlegame;

import Logic.GameDescriptor;
import Logic.Player;
import engine.users.UserContainer;


import java.util.HashSet;
import java.util.Set;

public class SingleGame {
    Set<UIPlayer> uiPlayersList;
    public GameDescriptor gameDescriptor;

    String colors[] = {"Maroon",
            "Azure",
            "Scarlet",
            "Crimson",
            "Tangerine",
            "Magenta"};

    public SingleGame(GameDescriptor gameDescriptor){
        this.gameDescriptor = gameDescriptor;
        uiPlayersList = new HashSet<>();
    }

    void addUIPlayer(Player player) throws Exception {
        for(UIPlayer uiPlayer: uiPlayersList)
        {
            if(uiPlayer.player.getName().equals(player.getName()))
            {
                throw new Exception(player.getName() + "already exists in this game");
            }
        }
        UIPlayer newPlayer = new UIPlayer(player, colors[uiPlayersList.size()]);
        uiPlayersList.add(newPlayer);

        gameDescriptor.getDynamicPlayers().addPlayer(newPlayer.player);
    }

    void addUIPlayer(String username, boolean isComputer, int idIndex) throws Exception {
        if(uiPlayersList.size() < gameDescriptor.getDynamicPlayers().getTotalPlayers()) {
            Player player = new Player(username, isComputer, idIndex);
            //++idIndex;

            addUIPlayer(player);
        }
        else
        {
            throw new Exception("The game is already sctive, please join another non-active game");
        }
    }

    void addUIPlayer(UserContainer userContainer) throws Exception {
        if(uiPlayersList.size() < gameDescriptor.getDynamicPlayers().getTotalPlayers()) {
            Player player = new Player(userContainer.Name, userContainer.IsComputer, userContainer.id);
            addUIPlayer(player);
        }
        else
        {
            throw new Exception("The game is already sctive, please join another non-active game");
        }
    }

    String getColorByPlayer(Player player)
    {
        for(UIPlayer currPlayer : uiPlayersList)
        {
            if(player.getName() == currPlayer.player.getName())
            {
                return currPlayer.color;
            }
        }

        return "Gainsboro";
    }
}
