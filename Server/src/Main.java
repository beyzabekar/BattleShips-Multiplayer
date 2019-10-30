import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;


/*
    This program creates a SERVER APPLICATION. In order for this application to work, it needs that some other applicaton (CLIENT) to establish a connection through the socket
    opened at port 51000. After the connection is established both, SERVER and CLIENT play in turns. After each one finishes its turn, they send messages to each other to inform.
    The messages contain the coordinates where the ship will be deployed, and where each player shoots the missile after each turn.
*/



public class Main {


    private static int[][] grid = new int[6][6];                                                    //Declaration of the grid area where the ships will be placed
    private static int[] fleets = new int[2];                                                       //Declaration of the fleets. One Fleet of ships is for SERVER and the other for CLIENT

    public static void main(String[] args) {

        try {
            System.out.println("Server is being initiliazed...");
            ServerSocket listener = new ServerSocket(51000);                                        //A new TCP Socket is opened at port 51000. This application will run on this port
            delay(1);
            System.out.println("Socket created! Server is listening for incoming connection...");
            Socket socket = listener.accept();                                                      //Socket on port 51000 is listening for new Connection.
                                                                                                    //When an incoming connection arrives, it means that a CLIENT joined the game
            System.out.println("\nConnection Created!");
            delay(2);
            System.out.println("\n\nPreparing for the new Battle! The fate of the nation depends on you!");
            fleets[0]=3;                                                                            //The SERVER's fleet is initialized with 3 ships
            fleets[1]=3;                                                                            //The CLIENT's fleet is initialized with 3 ships
            delay(2);

            intro(socket);                                                                          //Shows the Sea-Grid where the battle will happen
            delay(3);

            grid = deployShips(socket);                                                             //SERVER is asked to insert the coordinates where he want to deploy its ships
            delay(1);

            showNewSea(grid);                                                                       //Show the Sea but this time with the deployed SERVER's ships on it

            grid = takingEnemyShipCoordinates(socket);                                              //Waits for the CLIENT to send us the coordinates where he wants to deploy its ships
                                                                                                    //The USER does not know these coordinates. They are used in the background


            System.out.println("\n\nLET THE BATTLE BEGIN");
            delay(1);
            System.out.println("HONOUR YOUR NATION");                                               //Battle begins

            while(fleets[0]!=0&&fleets[1]!=0){                                                      //The battle will last until one of the user's has no ship left on it fleet
                delay(2);

                grid = serverShot(socket, grid);                                                    //It is the SERVER's turn to shoot
                showNewSea(grid);                                                                   //The result of the shot is shown into the sea grid

                grid = clientShot(socket, grid);                                                    //It the CLIENT turn to shoot. Application waits until CLIENT chooses where he wants to shoot and sends the coordinates
                showNewSea(grid);                                                                   //The result of the shot is shown into the sea grid

                fleets = showShips(grid);                                                           //A message with the number of ships left undestroyed is shown
            }

            showEndResult();                                                                        //A message showing the winner is shown

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Function used to delay the execution of code, to make the showing messages more user friendly
    public static void delay(int seconds){
        try{
            Thread.sleep(seconds*1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //The function which displayes the area where the battle will be fought, with no ships on it, but just the area it covers
    public static void intro(Socket socket) {
            System.out.println("\nRight now the Sea is empty\n");
            System.out.println("   012345  ");
            for (int i=0;i<6;i++){
                System.out.println(i+ " !         ! " +i);
            }
            System.out.println("   012345  ");
    }

    //The functoin which allows user to insert coordinates for each of his ships in order to deploy them into the sea grid.
    public static int[][] deployShips(Socket socket) {
        for (int i = 0; i < 3; i++) {
            System.out.println("\nPlease insert the coordinates where" +
                    " you want to deploy your number " + i + 1 + " ship!\n");
            int x, y;
            System.out.print("X coordinate: ");
            x = takeCoordinate();                                                                   //SERVER inserts X coordinate
            System.out.print("Y coordinate: ");
            y = takeCoordinate();                                                                   //SERVER inserts Y coordinate
            while (grid[x][y] == 1) {
                System.out.println("Please make sure to use other coordinates. " +
                        "You already have placed one ship here");
                System.out.print("X coordinate: ");
                x = takeCoordinate();
                System.out.print("X coordinate: ");
                y = takeCoordinate();
            }
            grid[x][y] = 1;                                                                         //The cell is set at 1 to indicate that there is a SERVER's ship
            sendCoordinates(socket, x, y);                                                          //Coordinates are send via the socket to Client Application so its system knows where the SERVER ships are deployed
        }
        delay(1);
        System.out.println("\nShips are being deployed...");
        delay(2);
        System.out.println("Ships have been deployed in the below positions...\n");
        return grid;                                                                                //Returns the SeaGrid with the updated value of cells after the deployment of the ships
    }

    //The function responsible for taking coordinates
    public static int takeCoordinate(){
        String check = "0 1 2 3 4 5 ";
        Scanner in = new Scanner(System.in);
        String temp = in.next();                                                                    //Waits for the user to insert the coordinates via the keyboard

        while(!check.contains(temp)){                                                               //Makes sure the value inserted is  0-5 (not a bigger number or text which crashes the App)
            System.out.println("Please insert a NUMBER 0-5");
            temp = in.next();
        }

        return  Integer.parseInt(temp);                                                             //Returns the isnerted number which is the coordinate
    }

    //The function responsible to send the coordinates of the ships to the Client Application with whom the connection is establishe through port 51000
    public static void sendCoordinates(Socket socket, int x, int y){
        try {
            String message = x + " " + y;                                                           //The coordinates are concatinated as part of the same string which looks like -> "x y"
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);                    //A new PrintWriter is created who will write to socket.getOutputStream, which is the
                                                                                                    //stream the socket sends into the network. The destination is already set by the socket
                                                                                                    //while the message is defined by the PrintWriter
            output.println(message);                                                                //The PrintWriter writes the message in the output stream of the socket, which send the message into the network.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //The function responsible to show the SeaGrid to the user
    public static void showNewSea(int[][] grid) {
        System.out.println("   012345  ");
        for(int i=0;i<5;i++){
            System.out.print(i+ " !");
            for(int j=0;j<=5;j++){
                if(grid[i][j]==0||grid[i][j]==2){                                                   //The cell equals 0 when there is nothing in the cell and 2 when there is a CLIENT ship
                    System.out.print(" ");                                                          //In both cases we show nothing in the SeaGrid, so SERVER's has to try and find where ships are hidden
                } else if (grid[i][j]==1){                                                          //Cell equals 1 when there is SERVER's ship hidden in that spot.
                    System.out.print("u");                                                          //We indicate that by showing "u" (like user) in the cells where SERVER's ship are hidden
                } else if (grid[i][j]==3){                                                          //Cell equals 3 when there is a SERVER's destroyed ship
                    System.out.print("-");                                                          //"-" indicate that there is a SERVER's destroyed ship
                } else if (grid[i][j]==4){                                                          //Cell equals 4 when SERVER shot into a cell which happened to hold and enemy ship.
                    System.out.print("x");                                                          //"x" indicates a successful shot which destriyed an enemy ship
                } else if (grid[i][j]==5){                                                          //Cell equals 5 when SERVER shot into a cell which happened to be empty
                    System.out.print("*");                                                          //"*" indicates that SERVER MISSED the shot by shooting an empty cell
                }
            }
            System.out.println("! " +i);
        }
        System.out.println("   012345  ");

    }

    //The function which takes the coordinates of the Client ships through the socket
    public static int[][] takingEnemyShipCoordinates(Socket socket){

        try{
            System.out.println("\nPlease wait while enemy is deplyoing its ships...");
            delay(2);
            boolean eleminated = false;
            for (int i=1; i<=3; i++){
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //BufferedReader is the place where the input stream, coming from the CLIENT application through the socket, will be held
                System.out.println("Waiting for the "+ i +" ship");
                String mess = input.readLine();                                                             //The stream is stored as a String in a variable
                String temp = mess;
                int ocurrence = mess.indexOf(' ');                                                          //From the message which has the form "x y" we are able to fetch both coordinates
                int x=Integer.parseInt(mess.substring(0, ocurrence));                                       //X Coordinate
                int y=Integer.parseInt(temp.substring(ocurrence+1));                                        //Y Coordinate
                System.out.println("Ship nr " + i + " has been deployed");
                if (grid[x][y]==1){                                                                         //In case CLIENT places the ship in the same Grid Cell as SERVER, both ships are destroyed
                    delay(1);
                    System.out.println("\nUPPS! Enemy placed its ship in the same coordinates with yours." +
                            " Both of your ships have been destroyed\n");
                    eleminated =true;
                    fleets[0]--;                                                                            //The number of ships for both of fleets is decremented
                    fleets[1]--;
                    grid[x][y]=3;                                                                           //The GridCell is set at 3 to indicate later in the User Interface that the Sherver ship is destroyed
                    delay(1);
                }else {
                    grid[x][y] = 2;                                                                         //In Case the cell is empty, the GridCell is set at 2, to indicate that there is CLIENT's ship
                }

            }
            if (eleminated){
                delay(2);
                System.out.println("\n\nAfter your ship has been destroyed your fleet looks like this");
                showNewSea(grid);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return grid;
    }

    //The function responsible for the SERVER shot
    public static int[][] serverShot(Socket socket, int[][] grid) {
        Scanner input = new Scanner(System.in);
        System.out.println("It is your turn!\n");
        System.out.print("Enter your X coordinate: ");
        int x=takeCoordinate();                                                                 //The X coordinate is inserted
        System.out.print("\nEnter your Y coordinate: ");
        int y=takeCoordinate();                                                                 //The Y coordinate is inserted
        delay(1);
        System.out.println("\nRocket is flyyiinnggg...");
        sendCoordinates(socket, x, y);                                                          //The inserted coordinates are sent to the CLIENT application
        grid = checkServerResult(grid, x, y);                                                   //The shooting is checked for its result
        delay(1);
        return grid;                                                                            //The grid is returned with its updated values after the shooting
    }

    //The function responsible to check the result of the SERVER's shot
    public static int[][] checkServerResult(int[][] grid, int x, int y) {
        delay(2);
        if (grid[x][y]==0){                                                                         //In case there is no ship in the coordinates SERVER missed its shot
            System.out.println("You missed!\n");
            grid[x][y]=5;                                                                           //GridCell is set to 5 to indicate that there has been a missed shot in these coordinates
        } else if (grid[x][y]==1){                                                                  //In case there is SERVER ship in the coordinates, SERVER destroys its own ship
            System.out.println("UPPS! You destroyed your own ship!\n");
            grid[x][y]=3;                                                                           //GridCell is set to 3 to indicate that there is a DESTROYED SERVER ship
        } else if (grid[x][y]==2){                                                                  //In case there is a CLIENT ship in these coordinates, SERVER destroyes enemy ship
            System.out.println("BRAVO! You destroyed an enemy ship!\n");
            grid[x][y]=4;                                                                           //GridCell is set to 4 to indicate there is a DESTROYED CLIENT ship
        }
        return grid;
    }

    //The function responsible for taking the coordinates for the shot of the CLIENT
    public static int[][] clientShot(Socket socket, int[][] grid){
        try{
            System.out.println("\nPlease wait while enemy is choosing its shooting coordiantes...");
            delay(2);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));      //The coordinates are fetched in the same way as before with the ship coordinates case
            String mess = input.readLine();
            System.out.println("\nIncomingggg....\n");
            String temp = mess;
            int ocurrence = mess.indexOf(' ');
            int x=Integer.parseInt(mess.substring(0, ocurrence));
            int y=Integer.parseInt(temp.substring(ocurrence+1));
            delay(1);
            grid = checkClientShotResult(grid, x, y);                                                        //The result of the shot is checked
        }catch (IOException e){
            e.printStackTrace();
        }
        return grid;                                                                                         //The grid with the new content after the shot is returned
    }

    //The function responsible to check the result of the CLIENT shot
    public static int[][] checkClientShotResult(int[][] grid, int x, int y) {
        if (grid[x][y]==0){                                                                         //In case there is no ship in the coordinates CLIENT missed its shot
            System.out.println("Huh! Enemy missed! We are safe!");
            grid[x][y]=5;                                                                           //GridCell is set to 5 to indicate that there has been a missed shot in these coordinates
        } else if (grid[x][y]==1){                                                                  //In case there is SERVER ship in the coordinates, CLIENT destroys this ship
            System.out.println("UPPS! Enemy destroyed your ship!");
            grid[x][y]=3;                                                                           //GridCell is set to 3 to indicate that there is a DESTROYED SERVER ship
        } else if (grid[x][y]==2){                                                                  //In case there is a CLIENT ship in these coordinates, CLIENT destroyes its own ship
            System.out.println("YUHUU! Enemy destroyed its own ship!");
            grid[x][y]=4;                                                                           //GridCell is set to 4 to indicate there is a DESTROYED CLIENT ship
        }
        return grid;
    }

    //The function responsible to show information to the SERVER USER on the survived ships
    public static int[] showShips(int[][] grid) {
        int[] ships = new int[2];
        int myShips=0, compShips=0;
        for(int i=0; i<=5; i++){
            for(int j=0; j<=5; j++){
                if(grid[i][j]==1){
                    myShips++;
                } else if (grid[i][j]== 2){
                    compShips++;
                }
            }
        }
        ships[0]= myShips;
        ships[1]= compShips;
        System.out.println("Your ships: " +myShips+ " | Enemy Ships: " +compShips);
        System.out.println("----------------------------------------------------");
        return ships;
    }

    //The function responsible to show the result of the game
    public static void() showEndResult {

        if (fleets[0] == 0) {
            System.out.println("\n\nYOU LOST THE BATTLE!");
        }

        if (fleets[1] == 0) {
            System.out.println("\n\nYOU WON THE BATTLE!");
        }
    }
}
