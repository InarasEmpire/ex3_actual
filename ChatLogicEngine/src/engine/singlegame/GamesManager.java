package engine.singlegame;

import Logic.Disc;
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

        //players
        JsonArray playersJsonArray= new JsonArray();
        for(UIPlayer player : singleGame.uiPlayersList)
        {
            JsonObject  currentPlayerJson = new JsonObject();
            currentPlayerJson.addProperty("name", player.player.getName());
            currentPlayerJson.addProperty("type", player.player.getType());
            currentPlayerJson.addProperty("color", player.color);
            currentPlayerJson.addProperty("turnAmount", player.player.getTurnAmount());
            playersJsonArray.add(currentPlayerJson);
        }
        singleGameJson.add("players", playersJsonArray);
        singleGameJson.addProperty("nextTurn", singleGame.gameDescriptor.getPlayers().getActivePlayer().getName());

        // board
        singleGameJson.addProperty("cols", singleGame.gameDescriptor.getGame().getBoard().getColumns());
        singleGameJson.addProperty("rows", singleGame.gameDescriptor.getGame().getBoard().getRows());
        JsonArray boardJsonArray= new JsonArray();
        Disc[][] board = singleGame.gameDescriptor.getGame().getBoard().getDiscs();
         for(int i = 0; i<board.length; ++i) // rows
         {
             for(int j = 0; j<board[i].length; ++j) // cols
             {
                 JsonObject  currentDiscJson = new JsonObject();
                 currentDiscJson.addProperty("row", i);
                 currentDiscJson.addProperty("col", j);
                 if(board[i][j] == null)
                 {
                     currentDiscJson.addProperty("color", "Gainsboro");
                 }
                 else
                 {
                     currentDiscJson.addProperty("color", singleGame.getColorByPlayer(board[i][j].getPlayer()));
                 }

                 boardJsonArray.add(currentDiscJson);
             }
         }
        singleGameJson.add("board", boardJsonArray);

         // status
        singleGameJson.addProperty("status", singleGame.gameDescriptor.isActive() ?  "active": "non-active");

         //winners
         if(singleGame.gameDescriptor.isEnded())
         {
             JsonArray winnersJsonArray= new JsonArray();
             for(Player player: singleGame.gameDescriptor.getWinners())
             {
                 JsonObject  winnerDiscJson = new JsonObject();
                 winnerDiscJson.addProperty("name", player.getName());
                 winnersJsonArray.add(winnerDiscJson);
             }
             singleGameJson.add("winners", winnersJsonArray);
         }

        return singleGameJson.toString();
    }


}
