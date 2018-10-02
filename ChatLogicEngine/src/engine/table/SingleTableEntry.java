package engine.table;

import Logic.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SingleTableEntry {
    public final String fileName;
    public  final String creator;
    public GameDescriptor gameDescriptor;
  //  public int totalPlayer;

    public SingleTableEntry(String username, String  fileName, String fileContent) {
        this.fileName = fileName;
        this.creator = username;
        addNewGame(fileContent);
    }

   /* public String getFileName() {
        return fileName;
    }

    public String getCreator() {
        return creator;
    }

    public String getTotalPlayers() {
        return creator;
    }
*/
    @Override
    public String toString() {
        return (creator != null ? creator : "");
    }

    private synchronized String addNewGame(String fileContent) {
        String msg="";
        try{
            GameDescriptor newGame;
            try {
                InputStream stream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
                JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                newGame = (GameDescriptor)(jaxbUnmarshaller.unmarshal(new InputStreamReader(stream)));
                newGame.init();
            }
            catch(JAXBException e){
                throw new Exception("please check your xml format");
            }

            validateGame(newGame);

            gameDescriptor = newGame;
            //totalPlayer = newGame.getDynamicPlayers().getTotalPlayers();

        } catch (Exception e) {
            //todo: throw exception
            msg = e.getMessage();
        }
        return msg;
    }

    private void validateGame(GameDescriptor game)throws Exception{
     //check minimum and maximum players
        final int MAX_PLAYERS = 6;
        final int MIN_PLAYERS = 2;
        if(game.getDynamicPlayers().getTotalPlayers() < MIN_PLAYERS || game.getDynamicPlayers().getTotalPlayers() > MAX_PLAYERS)
        {
            throw new Exception("Total players must be between 2 and 6");
        }
    }
}
