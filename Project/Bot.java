import java.util.ArrayList;

public class Bot {

    //to see how many goes the bot has had since last running look command
    private int movesSinceLook = 3;

    //stores bots next planned moves
    private char[] nextMoves = new char[2];
    private GameLogic gl = new GameLogic();

    //will ensure that bot doesn't stay around one piece of gold or exit tile all game
    private int lingering = 0;

    /**
     * Decides whether look or move is next depending on when last look was done or what's in nextMoves
     */
    protected void decideCommand() {
        if(movesSinceLook > 1) {
            //if 2 moves have been done since looking, print that the bot is looking and execute the look
            System.out.println("BOT: LOOK");
            executeLook();
            movesSinceLook = 0;
        } else if(nextMoves[movesSinceLook] == 'L') {
            //checking this if separately to avoid index out of bounds errors with trying to check nextMoves[2]
            System.out.println("BOT: LOOK");
            executeLook();
            movesSinceLook = 0;
        } else {
            //if not looking, move the bot with the next planned move
            System.out.println("BOT: MOVED");
            gl.moveBot(nextMoves[movesSinceLook]);
            movesSinceLook++;
        }
    }

    /**
     * Looks at the result from look and constructs the next two moves accordingly
     */
    protected void executeLook() {

        /*ratings array gives every block on the look a score giving how much to prioritise it:
         *
         * -1 - # or blank space, spaces which can't be moved onto
         * 1 - . tile
         * 2 - E or G tile (as standing on these obscures them from the player, meaning they're not tempted to come closer
         * 3 - blocks adjacent to a E or G tile (so player can see there's an E or G for them to collect, making them
         *       want to try and slip past the bot and onto it
         * 4 - blocks adjacent to the player so the bot aims for these when the player is out of reach in two goes
         * 5 - block with the player itself
         */
        int[][] ratingsArray = new int[5][5];

        //moves is all moves that will be checked to see which is the best move (L is looking again)
        String[] moves = {"NN", "EE", "SS", "WW", "NE", "NW", "EN", "ES", "SE", "SW", "WN", "WS", "NL", "EL", "SL", "WL"};

        //ratings for all 16 of the moves
        int[] moveRatings = new int[16];

        //will store the highest rating on the look, and highestAt will store all moves which reach this
        int highestRating = 0;
        ArrayList<Integer> highestAt = new ArrayList<>();

        //getting the result itself, array separated into rows, calling look with false to show the look around the bot
        String[] lookResult = gl.look(false).split(System.lineSeparator());

        //going through all of the look spaces to set priorities
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                switch(lookResult[i].charAt(j)) {
                    //setting as -1 if can't move onto the space
                    case ' ':
                    case '#':
                        ratingsArray[i][j] = -1;
                        break;
                    case '.':
                        //checking if it's already been assigned as adjacent space to E, G or P before setting as 1
                        if(ratingsArray[i][j] != 3 && ratingsArray[i][j] != 4) {
                            ratingsArray[i][j] = 1;
                        }
                        break;
                    case 'P':
                        //setting adjacent spaces to 4 if they are within bounds and contain a ., G or E
                        if(i < 4) {
                            if (lookResult[i + 1].charAt(j) == '.' ||
                                    lookResult[i + 1].charAt(j) == 'G' || lookResult[i + 1].charAt(j) == 'E') {
                                ratingsArray[i + 1][j] = 4;
                            }
                        }
                        if(i > 0) {
                            if (lookResult[i - 1].charAt(j) == '.' ||
                                    lookResult[i - 1].charAt(j) == 'G' || lookResult[i - 1].charAt(j) == 'E') {
                                ratingsArray[i - 1][j] = 4;
                            }
                        }
                        if(j > 0) {
                            if (lookResult[i].charAt(j - 1) == '.' ||
                                    lookResult[i].charAt(j - 1) == 'G' || lookResult[i].charAt(j - 1) == 'E'  ) {
                                ratingsArray[i][j - 1] = 4;
                            }
                        }
                        if(j < 4) {
                            if (lookResult[i].charAt(j + 1) == '.' ||
                                    lookResult[i].charAt(j + 1) == 'G' || lookResult[i].charAt(j + 1) == 'E') {
                                ratingsArray[i][j + 1] = 4;
                            }
                        }

                        //setting location of player themselves as 5
                        ratingsArray[i][j] = 5;
                        break;
                    case 'G':
                    case 'E':
                        //setting rating as 3 if adjacent spot is within range, is a . and hasn't already been set as
                        // 4 due to player
                        if(i < 4) {
                            if (lookResult[i + 1].charAt(j) == '.' && ratingsArray[i + 1][j] != 4) {
                                ratingsArray[i + 1][j] = 3;
                            }
                        }
                        if(i > 0) {
                            if (lookResult[i - 1].charAt(j) == '.' && ratingsArray[i - 1][j] != 4) {
                                ratingsArray[i - 1][j] = 3;
                            }
                        }
                        if(j > 0) {
                            if (lookResult[i].charAt(j - 1) == '.' && ratingsArray[i][j - 1] != 4) {
                                ratingsArray[i][j - 1] = 3;
                            }
                        }
                        if(j < 4) {
                            if (lookResult[i].charAt(j + 1) == '.' && ratingsArray[i][j + 1] != 4) {
                                ratingsArray[i][j + 1] = 3;
                            }
                        }

                        //setting location of G/E itself as 2 if not already rated 4 due to player being adjacent
                        if(ratingsArray[i][j] != 4) {
                            ratingsArray[i][j] = 2;
                        }
                        break;
                }
            }

        }

        //going through all the moves and setting the ratings
        for(int i = 0; i < 16; i++) {
            char[] moveChars = moves[i].toCharArray();

            //getting the xy for the first and second move
            int[] first = changeXYInDirn(moveChars[0], 2, 2);
            int[] second = changeXYInDirn(moveChars[1], first[0], first[1]);

            //if it's less than 12, these are the two movement ones
            if(i < 12) {
                //checks both are above 1, aka both can be moved onto, then sets rating as block landed on
                if (ratingsArray[first[0]][first[1]] >= 1 && ratingsArray[second[0]][second[1]] >= 1) {
                    moveRatings[i] = ratingsArray[second[0]][second[1]];
                } else {
                    //if movement illegal, set move rating to -1
                    moveRatings[i] = -1;
                }
            } else {
                //for moves 12-15 where there's one then a look, we only want to do these if they give something
                //higher than 1, as if there's only ones we want to move the furthest possible
                if(ratingsArray[first[0]][first[1]] > 1) {
                    moveRatings[i] = ratingsArray[first[0]][first[1]];
                } else {
                    //if illegal movement, put -1
                    moveRatings[i] = -1;
                }
            }
        }

        //checks what the highest existing rating is
        for(int i = 0; i < 16; i++) {
            if(moveRatings[i] > highestRating) {
                highestRating = moveRatings[i];
            }
        }

        //if it hasn't been lingering for too long, make the highest move. If it has and it's aiming for a 3, do another move
        for(int i = 0; i < 16; i++) {
            if(lingering > 2 && highestRating == 3) {
                if(moveRatings[i] == 1) {
                    highestAt.add(i);
                }
            } else {
                if (moveRatings[i] == highestRating) {
                    highestAt.add(i);
                }
            }
        }

        //if it moved to a 3, up lingering
        if(highestRating == 3) {
            lingering++;
        } else {
            //if it didn't and there is a lingering score, decrease it
            if(lingering > 0) {
                lingering--;
            }
        }

        //randomly select one of the highest moves
        int highestUsed = (int) (Math.random() * highestAt.size());

        //put this move in nextMoves to be executed
        nextMoves[0] = moves[highestAt.get(highestUsed)].charAt(0);
        nextMoves[1] = moves[highestAt.get(highestUsed)].charAt(1);

    }

    /**
     * Changes the x and y coordinates to be the ones which would be moved to under a certain direction
     * @param dirn: the direction to move coordinates to
     * @param x1: starting x
     * @param y1: starting y
     * @return int array of length 2 with end x and end y
     */
    private int[] changeXYInDirn(char dirn, int x1, int y1) {
        //setting them to x1 and y1 as only one of x1 y1 will change and we want the other to be the same
        int[] x2y2 = {x1, y1};
        switch(dirn) {
            case 'N':
                x2y2[0]--;
                break;
            case 'E':
                x2y2[1]++;
                break;
            case 'S':
                x2y2[0]++;
                break;
            case 'W':
                x2y2[1]--;
                break;
            default:
                //when it's an L
                break;
        }

        return x2y2;
    }
}
