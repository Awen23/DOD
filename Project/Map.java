import java.io.*;

/**
 * Reads and contains in memory the map of the game.
 *
 */
public class Map {

	// Representation of the map
	private char[][] map;
	
	// Map name
	private String mapName;
	
	// Gold required for the human player to win
	private int goldRequired;
	
	/**
     *Constructor, asks for map file name to be inputted from files available in folder
	 */
	public Map() {
        System.out.println("Please input a map file from the list below:");

        //will hold all the files in the folder Maps
        File file = new File("./Maps");

        //printing all file names
        if (file.list().length != 0) {
            for (String fileNames : file.list()) {
                System.out.println(fileNames);
            }
        } else {
            System.out.println("No files found");
        }


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean fileSelected = false;

        try{
            //looping through trying to open the file until a correct file name has been inputted and the file is selected
            do {
                String input = reader.readLine();
                for (String fileNames : file.list()) {
                    if (input.matches(fileNames)) {
                        String fileName = "./Maps/" + input;
                        readMap(fileName);
                        fileSelected = true;

                        //outputs name of map to indicate successful opening
                        System.out.println("Playing map: " + getMapName());
                        break;
                    }
                }
                if(!fileSelected) {
                    System.out.println("Wrong file name, please try again");
                }
            } while (!fileSelected);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
	}

    /**
     * @return : Gold required to exit the current map.
     */
    protected int getGoldRequired() {
    	return goldRequired;
    }

    /**
     * @return : The map as stored in memory.
     */
    protected char[][] getMap() {
    	return map;
    }


    /**
     * @return : The name of the current map.
     */
    private String getMapName() {
        return mapName;
    }


    /**
     * Reads the map from file.
     *
     * @param fileName : Name of the map's file.
     */
    protected void readMap(String fileName) {
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            //reading in name and number gold
            String name = reader.readLine();
            String numGold = reader.readLine();
            //getting rid of name prefix and setting the name, same thing for gold required with win prefix
            mapName = name.replaceAll("name ", "");
            goldRequired = Integer.valueOf(numGold.replaceAll("win ", ""));

            //setting up a stringBuffer to read in the map
            StringBuffer stringMap = new StringBuffer();
            String line = reader.readLine();

            while (line != null) {
                //reads in lines until reached end of fine, putting a new line between each
                stringMap.append(line);
                stringMap.append(System.lineSeparator());
                line = reader.readLine();
            }

            //putting all the contents of the read map in a string
            String everything = stringMap.toString();

            //creating an array with only the lines by splitting where a separator has been put in
            String lines[] = everything.split(System.lineSeparator());

            //creating a char array of correct length as y length is number of lines, x length is length of a single line
            map = new char[lines.length][lines[0].length()];

            //setting each line to include the char array of the line read
            for(int i = 0; i < lines.length; i++) {
                map[i] = lines[i].toCharArray();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


}
