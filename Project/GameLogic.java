/**
 * Contains the main logic part of the game, as it processes.
 *
 */
public class GameLogic {

    //creates a new map which will ask for the user to select a new map
	private static Map map = new Map();

	//fetching map layout so it can be used to check which block the player is on
    private static char[][] mapLayout = map.getMap();

    //getting the lengths of the map for future confirming that an index isn't out of bounds
    private static int xLength = mapLayout[0].length;
    private static int yLength = mapLayout.length;

    //x and y location of player
	private static int xLocation;
	private static int yLocation;

	//x and y location of bot
	private static int botXLocation;
	private static int botYLocation;

	//setting up game running as false so the main method will place the bot and human on map
	private static Boolean gameRunning = false;

	private static int currentGold = 0;

    /**
	 * Returns the gold required to win.
	 *
     * @return : Gold required to win.
     */
    protected String hello() {
        int gold = map.getGoldRequired();
        return "Gold to win: " + gold;
    }
	
	/**
	 * Returns the gold currently owned by the player.
	 *
     * @return : Gold currently owned.
     */
    protected String gold() {
        return "Gold owned: " + currentGold;
    }

    /**
     * Checks if movement is legal and updates player's location on the map.
     *
     * @param direction : The direction of the movement.
     * @return : Protocol if success or not.
     */
    protected String move(char direction) {
        //saving the original location in case the move isn't allowed and the player has to be moved back
        int originalLocation[] = {xLocation, yLocation};

        //moving player in specified direction
        switch (direction) {
            case 'N':
                yLocation--;
                break;
            case 'E':
                xLocation++;
                break;
            case 'S':
                yLocation++;
                break;
            case 'W':
                xLocation--;
                break;
            default:
                return null;
        }

        //checking the location isn't a wall, returning fail if it is but success if it isn't
        if (mapLayout[yLocation][xLocation] == '#') {
            xLocation = originalLocation[0];
            yLocation = originalLocation[1];
            return "FAIL";
        } else {
            return "SUCCESS";
        }
    }

    /**
     * Moves the bot one place in the specified direction and checks whether action is allowed
     *
     * @param direction N, E, S, W the direction to move the bot
     */
    protected void moveBot(char direction) {
        //saving the location in case the bot does a move not allowed and has to be moved back
        int originalLocation[] = {botXLocation, botYLocation};
        //changing bot's x y coordinates depending on direction given
        switch(direction) {
            case 'N':
                botYLocation--;
                break;
            case 'E':
                botXLocation++;
                break;
            case 'S':
                botYLocation++;
                break;
            case 'W':
                botXLocation--;
                break;
            default:
        }

        //moving bot back if action wasn't allowed aka walking into a wall
        if(mapLayout[botYLocation][botXLocation] == '#') {
            botXLocation = originalLocation[0];
            botYLocation = originalLocation[1];
        }

    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    protected String look(boolean isHuman) {
        int xLoc, yLoc;

        if(isHuman) {
            xLoc = xLocation;
            yLoc = yLocation;
        } else {
            xLoc = botXLocation;
            yLoc = botYLocation;
        }
        //setting up a string builder to store the map view in
        StringBuilder output = new StringBuilder();

        //looping through the y and x coordinates
        for(int i = yLoc - 2; i <= yLoc + 2; i++) {
            for(int j = xLoc - 2; j <= xLoc + 2; j++) {
                //placing a P for player at player's x and y coords, same for bot
                if(j == xLocation && i == yLocation) {
                    output.append('P');
                } else if (j == botXLocation && i == botYLocation) {
                    output.append('B');
                } else {
                    //if it's out of bounds, output a space, otherwise output the symbol from the map
                    if(j <  0 || i < 0 || j > xLength - 1 || i > yLength - 1) {
                        output.append(" ");
                    } else {
                        output.append(mapLayout[i][j]);
                    }
                }
            }
            if(i < yLoc + 2) {
                //adding a new line at the end of each y-line
                output.append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     *
     * @return If the player successfully picked-up gold or not.
     */
    protected String pickup() {
        //if there's a G at the player's location, pick up and change space to a ., if not then fail
        if(mapLayout[yLocation][xLocation] == 'G') {
            currentGold++;
            mapLayout[yLocation][xLocation] = '.';
            return "SUCCESS. " + gold();
        } else {
            return "FAIL. " + gold();
        }
    }

    /**
     * Quits the game, shutting down the application.
     */
    protected static void quitGame() {
        //checking if the player has enough gold and is on exit tile before winning if they do or losing if not
        if(mapLayout[yLocation][xLocation] == 'E' && currentGold >= map.getGoldRequired()) {
            System.out.println("WIN");
        } else {
            System.out.println("LOSE");
        }
        System.exit(0);
    }

    /**
     * Checks if the player and the bot are at the same location, ends game if so
     */
    protected static void checkIfCaught() {
        if(xLocation == botXLocation && yLocation == botYLocation) {
            System.out.println("You've been caught by the bot! Game Over!");
            System.exit(1);
        }
    }
	
	public static void main(String[] args) {
        HumanPlayer player = new HumanPlayer();

        //if game hasn't already been started, placing player and bot on map
        if(!gameRunning) {

            char generatedSpace;

            //generating random locations for player until the player is on a . or E space
            do {
                xLocation = (int) (Math.random() * xLength);
                yLocation = (int) (Math.random() * yLength);
                 generatedSpace = mapLayout[yLocation][xLocation];
            } while (!(generatedSpace == '.' || generatedSpace == 'E'));

            //generating random location for bot until bot is on a . or E space and is not on player space
            do {
                botXLocation = (int) (Math.random() * xLength);
                botYLocation = (int) (Math.random() * yLength);
                generatedSpace = mapLayout[botYLocation][botXLocation];
            } while (!(generatedSpace == '.' || generatedSpace == 'E'
                    && (botXLocation == xLocation && botYLocation == yLocation)));

            gameRunning = true;
        }

        //reads the commands and processing them
        player.getInputFromConsole();

    }

}