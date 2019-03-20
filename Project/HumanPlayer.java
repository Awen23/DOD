import java.io.*;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 */
public class HumanPlayer {
    private GameLogic gl = new GameLogic();
    private Bot bot = new Bot();


    /**
     * Reads player's input from the console.
     * <p>
     * return : A string containing the input the player entered.
     */
     protected void getInputFromConsole() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            //Keep on accepting input from the command-line
            while(true) {
                String command = reader.readLine();

                //Close on an End-of-file (EOF) (Ctrl-D on the terminal)
                if(command == null)
                {
                    //Exit code 0 for a graceful exit
                    System.exit(0);
                }

                //runs the command and stores result
                String result = getNextAction(command);

                //if not recognised, output error message and don't take up a turn
                if(result.contains("Wrong command")) {
                    System.out.println("Command not recognised");
                } else {
                    //checks if caught both before bots go and after bots go
                    GameLogic.checkIfCaught();
                    //prints result from player's turn
                    System.out.println(result);
                    //gives the bot a turn
                    bot.decideCommand();
                    GameLogic.checkIfCaught();
                }
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     * @return : Processed output or Invalid if the @param command is wrong.
     */
    protected String getNextAction(String command) {
        //calls relevant function from GameLogic depending on command
        switch(command.toUpperCase()) {
            case "HELLO":
                return gl.hello();
            case "GOLD":
                return gl.gold();
            case "MOVE N":
                return gl.move('N');
            case "MOVE E":
                return gl.move('E');
            case "MOVE S":
                return gl.move('S');
            case "MOVE W":
                return gl.move('W');
            case "PICKUP":
                return gl.pickup();
            case "LOOK":
                //calling with true so it shows the human look map
                return gl.look(true);
            case "QUIT":
                GameLogic.quitGame();
                //returning nothing just so it doesn't continue to the default
                return "";
            default:
                //if anything but one of the set commands are entered
                return "Wrong command";

        }
    }




}