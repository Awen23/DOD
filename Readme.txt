HOW TO INSTALL AND RUN
Download the project file, then navigate to it in the command line. Compile first using "javac *.java" then run using "java GameLogic". You will then be faced with it asking you to select a file, once you have typed in the file name the game will start with the selected file.

HOW TO ADD MAPS
Create a text file with the following formatting:
name *insert name here*
win *insert number here*
*insert map here*

Then move it to the Maps file within the project folder, and on the next run it will come up in the file list such that you can type the file name to select it. 
Note: maps must be rectangular and surrounded with walls (#)

HOW TO PLAY

The Board:
P - you, the player
B - the bot
G - gold
. - empty space
# - wall
E - exit space

Commands:
HELLO - tells you how many gold you need to escape
GOLD - tells you how many gold you have
MOVE <direction> - allows you to move one space N, E, S or W on the map
PICKUP - will pickup gold if you are currently on gold
LOOK - will show you a 5x5 grid of what's surrounding you on the map
QUIT - quits the game, if you are on the exit tile with enough gold you will win, otherwise you will lose

Once you play a command, your turn is over, and it is now the bot's turn. You will see whether the bot has decided to move or look, which will be signified with "BOT: MOVED" and "BOT: LOOK" resp.

Aim of the Game:
To collect all of the gold in the dungeon and exit successfully on an exit tile without being caught by the bot

COMMENTS ON CODE

Implementation of Human Player:
Command is passed to getInputFromConsole. It then checks if the result from getNextAction is a wrong command, and if so prints "Command not recognised", otherwise the result from executing the command is printed and the bot takes a turn. getNextAction will execute the commands by calling the relevant function in GameLogic with any needed parameters (e.g. direction). 

Implementation of Bot:
Bot will use look at least every 3rd go, and from the look will construct next two moves, which will either be two movements or one movement then another look. From the look, it will rate all possible tiles to land on, and then go for one of the ones giving the highest score. It first priorities player, then spaces adjacent to the player, then spaces adjacent to gold or exit tiles, then the gold or exit tiles themselves, then blank spaces. When a gold or exit tile is found, it will usually linger going to spaces adjacent to it for a few turns, as the player is likely to be looking out for these tiles and may still go for it if the bot is next to it and they can see the gold or exit. If it has been going around a gold or exit tile for a few rounds of look, it will move away and go searching for the player again, in order to cover more area of the map, making it more likely to find the player. 

Implementation of Map Selection:
It will read in all the file names from the /Maps/ folder, and then print all of these. This is so that the player can see which maps can be read in successfully. From here, the player will input the name of the text file from the list and then it will be read in if it matches a file in the list, or an error will be outputted if it doesn't. The map is read in through a string buffer to start with each line seperated by a new line, and is then split into a char array after all lines have been read in.