# Battleship
Basic version of Battleship Game (Console Application) implemented in JAVA, for two players in different COMPUTERS

# Game Discription and Rules

This game is played between 2 different USERS in 2 different COMPUTERS. One Player has to be the SERVER of the game (the one who
initiates the game and waits for connection), and from now on we will refer him as SERVER. The second player is the CLIENT (the one who
will ask and create connection with the server), and we will refer to him as the CLIENT. 

The game consists of both Players having a fleet of 3 ships and it is seperated in two phases. 

1.Deployment Phase

  These ships are deployed in a 6x6 grid, which imitates the sea. The game isplayed in turns. Firstly SERVER has to choose three different 
  coordinates in order to deploy its ships on the grid. Coordinates must bebetween 0 and 5, other values are not allowed. Then is the 
  CLIENT's turn to do the same in his machine, in order to deploy its ships. Incase CLIENT's randomly chooses to deploy one of its ships 
  in the exact same coordinates as SERVER, both of their ships will be destroyed before starting the battle.

2.Battle Phase

  After all the ships have been deployed, the second phase of the game initiates. It again goes in turns, and both players try to find
  where the enemy has placed its ships. The first goes SERVER and than CLIENT and they contiue to go in turns until one of them has no
  ships left to fight.
  Let's suppose that one Player chooses X and Y coordinates to shoot its missile. There are three possible outcomes: 1. In those 
  coordinates it is an empty sea, so the player misses its shot; 2. In those coordinates happens to be the player's own ship, so the 
  player wrongly destroyed its own ship; 3. In those coordinates happens to be the enemy's ship, so the player has a successful shot,
  destroying enemy's ship.
 
The game finishes when all the 3 ships of one of the fleets have been destroyed. The one who destroyes the enemy's ship faster, is the
winner of the game.

# Prerequites
1. You need to have at least JDK8 installed in your machine, in order for the application to run. You can [click here](https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)
for reading a tutorial on how to install JDK in Windows and/or Ubuntu.
2. For you to run the project easier, it is reccomended to use [IntelliJ IDE](https://www.jetbrains.com/idea/download/#section=windows)

# Installation and Running

For installing this game, you just need to download the source code locally and nothing more is required.

In order to run the game, a bit of attention should be payed. The project itself contains two miniprojects, The Server and The Client.
In order for the game to function properly, two steps must be followe.

1.The Server has the source code needed to run The SERVER. It should be runned first, in the Machine you want to be the First Player.
In order to run it, you just go at ~\Battleships in Network\Server\out\production\Server directory, open your Command Prompt and
simply run the command
    
    java Main
 
2.The Machine which be the second player has to run the CLIENT code. It must be in the same LAN with the SERVER Machine. 
In order to run this code, the Client User must know the Server's IP address (also the port number of the application is important,
but it is hard coded at the value of 51000).

The SERVER USER must open its Command Prompt as Admin (Press 'Win+x' and then Press 'a') and run the following command. 

    ipconfig
    
If the Server Machine is connected with the Ethernet Interface, the user must look for Ethernet adapter Ethernet. In case 
the Server Machine is connected via the Wi-Fi interface, the user must look for Wi-Fi adapter. For example in mymachine it looks
like this.

    Ethernet adapter Ethernet:

       Connection-specific DNS Suffix  . :
       Link-local IPv6 Address . . . . . : fe80::2d3b:e747:d02c:76b5%22
       IPv4 Address. . . . . . . . . . . : 192.168.2.49
       Subnet Mask . . . . . . . . . . . : 255.255.255.0
       Default Gateway . . . . . . . . . : 192.168.2.240


So the CLIENT must know the IPv4 Address of the Server. Client must be inside the ~\Battleships in Network\Client\out\production\Client
directory, open the cmd in that directory and run the following command:

    java client 192.168.2.49

ATTENTION! The server must be int the same LAN with the client, and also when the Client Applicaton asks for connection the Server 
Application must be running, otherwise the Client App will not work.

# Built With

* [JAVA 8](https://www.java.com/en/download/) - The programming language used

