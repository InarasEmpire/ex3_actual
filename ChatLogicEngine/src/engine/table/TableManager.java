package engine.table;

import java.util.ArrayList;
import java.util.List;

import Logic.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/*
This class is thread safe for the manner of adding\fetching new chat lines, but not for the manner of getting the size of the list
if the use of getVersion is to be incorporated with other methods here - it should be synchronized from the user code
 */
public class TableManager {

    private final List<SingleTableEntry> tableDataList;

    public TableManager() {
        tableDataList = new ArrayList<>();
    }

    public synchronized void addTableEntry(String fileName, String username, String fileContent) {
        tableDataList.add(new SingleTableEntry(username, fileName, fileContent));
        //todo: catch exception
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
            currentGameJson.addProperty("GameTitle", currGame.gameDescriptor.getDynamicPlayers().getGameTitle());
            currentGameJson.addProperty("Creator", currGame.creator);
            currentGameJson.addProperty("BoardSize",
                    currGame.gameDescriptor.getGame().getBoard().getRows() + "x" + currGame.gameDescriptor.getGame().getBoard().getColumns());
            currentGameJson.addProperty("Target", currGame.gameDescriptor.getGame().getTarget());
            currentGameJson.addProperty("Variant", currGame.gameDescriptor.getGame().getVariant());
            currentGameJson.addProperty("Status", currGame.gameDescriptor.isActive()? "Active" : "Non-Active");
            currentGameJson.addProperty("Players",
                    currGame.gameDescriptor.getDynamicPlayers().getPlayers().size() + "/" + currGame.gameDescriptor.getDynamicPlayers().getTotalPlayers());

            gamesJsonArray.add(currentGameJson);
        }

        gamesJson.add("games", gamesJsonArray);
        gamesJson.addProperty("version",  getVersion());
        return gamesJson.toString();
    }

}