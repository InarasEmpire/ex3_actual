package engine.singlegame;

import Logic.GameDescriptor;
import Logic.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engine.users.UserContainer;

import java.util.Set;

public class GamesManager {

    Set<SingleGame> gamesList;


    public synchronized SingleGame getGameByIndex(int index) throws Exception {
        if(index > gamesList.size())
        {
            throw new Exception("game index doesn't exist");
        }
        for (int i=0; i<index-1; ++i){ //TODO: check this
            gamesList.iterator().next();
        }
        return gamesList.iterator().next();
    }

    public synchronized void addNewGame(GameDescriptor game){
        SingleGame newSngleGame = new SingleGame(game);

    }

    public synchronized void addPlayerToGame(UserContainer userContainer, int gameIndex) throws Exception {
        SingleGame gameByIndex = getGameByIndex(gameIndex);
        gameByIndex.addUIPlayer(userContainer);
    }

    public synchronized void addPlayerToGame(Player player, int gameIndex) throws Exception {
        SingleGame gameByIndex = getGameByIndex(gameIndex);
        gameByIndex.addUIPlayer(player);
    }

    private void checkIfActivateGame(SingleGame game)
    {
        if(game.gameDescriptor.getDynamicPlayers().getPlayers().size() == game.gameDescriptor.getDynamicPlayers().getTotalPlayers())
        {
            game.gameDescriptor.startGame();
        }
    }


    public synchronized String getGameJsonStatus(int index) throws Exception {

        SingleGame singleGame = getGameByIndex(index);

        JsonObject singleGameJson = new JsonObject();


        JsonArray playersJsonArray= new JsonArray();
        for(UIPlayer player : singleGame.uiPlayersList)
        {
            JsonObject  currentPlayerJson = new JsonObject();
            currentPlayerJson.addProperty("name", player.player.getName());
            currentPlayerJson.addProperty("type", player.player.getType());
            currentPlayerJson.addProperty("color", player.color);
            playersJsonArray.add(currentPlayerJson);
        }
        singleGameJson.add("players", playersJsonArray);

    //TODO: continue. map the game table from user-id to user-color
        /*
        JsonObject  currentGameJson = new JsonObject();
        currentGameJson.addProperty("GameTitle", currGame.singleGame.gameDescriptor.getDynamicPlayers().getGameTitle());
        currentGameJson.addProperty("Creator", currGame.creator);
        currentGameJson.addProperty("BoardSize",
                currGame.singleGame.gameDescriptor.getGame().getBoard().getRows() + "x" + currGame.singleGame.gameDescriptor.getGame().getBoard().getColumns());
        currentGameJson.addProperty("Target", currGame.singleGame.gameDescriptor.getGame().getTarget());
        currentGameJson.addProperty("Variant", currGame.singleGame.gameDescriptor.getGame().getVariant());
        currentGameJson.addProperty("Status", currGame.singleGame.gameDescriptor.isActive()? "Active" : "Non-Active");
        currentGameJson.addProperty("Players",
                currGame.singleGame.gameDescriptor.getDynamicPlayers().getPlayers().size() + "/" + currGame.singleGame.gameDescriptor.getDynamicPlayers().getTotalPlayers());

            gamesJsonArray.add(currentGameJson);


        singleGameJson.add("games", gamesJsonArray);
        singleGameJson.addProperty("version",  getVersion());
        */
        return singleGameJson.toString();
    }


}
