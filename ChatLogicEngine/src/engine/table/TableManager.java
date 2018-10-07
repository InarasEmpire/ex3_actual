package engine.table;

import java.util.ArrayList;
import java.util.List;

import Logic.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import engine.singlegame.SingleGame;

/*
This class is thread safe for the manner of adding\fetching new chat lines, but not for the manner of getting the size of the list
if the use of getVersion is to be incorporated with other methods here - it should be synchronized from the user code
 */
public class TableManager {

    private final List<SingleTableEntry> tableDataList;

    public TableManager() {
        tableDataList = new ArrayList<>();
    }

    public synchronized SingleGame addTableEntry(String fileName, String username, String fileContent) throws Exception {

        SingleTableEntry newEntry =new SingleTableEntry(username, fileName, fileContent);
        for(SingleTableEntry currentry: tableDataList)
        {
            if(currentry.singleGame.gameDescriptor.getDynamicPlayers().getGameTitle().equals(newEntry.singleGame.gameDescriptor.getDynamicPlayers().getGameTitle()))
            {
                throw new Exception("Your game title already exists, please change it.");
            }
        }
        tableDataList.add(newEntry);
        return newEntry.singleGame;
    }

    public synchronized List<SingleTableEntry> getTableEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex >= tableDataList.size()) {
            fromIndex = 0;
        }
        return tableDataList.subList(fromIndex, tableDataList.size());
    }
    public int getVersion() {
        return tableDataList.size();
    }

    public synchronized String toString(){
        JsonObject  gamesJson = new JsonObject();
        JsonArray gamesJsonArray= new JsonArray();

        for (SingleTableEntry currGame: tableDataList) {
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
        }

        gamesJson.add("games", gamesJsonArray);
        gamesJson.addProperty("version",  getVersion());
        return gamesJson.toString();
    }

    public synchronized SingleTableEntry getTableEntryByIndex(int index){
        if (index < 0 || index >= tableDataList.size()) {
            return null;
        }
        return tableDataList.get(index);
    }

}