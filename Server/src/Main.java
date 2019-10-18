import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static int[][] grid = new int[10][10];
    private static int[] ships = new int[2];

    public static void main(String[] args) {
        try {
            System.out.println("Server is being initiliazed...");
            ServerSocket listener = new ServerSocket(51000);
            delay(1);
            System.out.println("Socket created! Server is listening for incoming connection...");
            Socket socket = listener.accept();
            System.out.println("\nConnection Created!");
            delay(2);
            System.out.println("\n\nPreparing for the new Battle! The fate of the nation depends on you!");
            ships[0]=3; //Number of Server ships
            ships[1]=3; //Number of Client ships
            delay(2);

            //Displaying the beggining field
            intro(socket);
            delay(3);

            //Your turn to deploy ships
            grid = deployShips(socket);
            delay(1);

            //Show the updated sea with the new ships
            showNewSea(grid);

            //Enemy's turn to deploy ships
            grid = takingEnemyShipCoordinates(socket);


            System.out.println("\n\nLET THE BATTLE BEGIN");
            delay(1);
            System.out.println("HONOUR YOUR NATION");

            while(ships[0]!=0&&ships[1]!=0){
                delay(2);
                //User fires its missile
                grid = yourShot(socket, grid);
                showNewSea(grid);

                //Client fires its missile
                grid = enemyShot(socket, grid);
                showNewSea(grid);

                //Show how many ships are left
                ships = showShips(grid);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void delay(int seconds){
        try{
            Thread.sleep(seconds*1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void intro(Socket socket) {
            System.out.println("\nRight now the Sea is empty\n");
            System.out.println("   0123456789  ");
            for (int i=0;i<10;i++){
                System.out.println(i+ " !         ! " +i);
            }
            System.out.println("   0123456789  ");
    }
    public static int[][] deployShips(Socket socket) {
        for (int i = 0; i < 3; i++) {
            System.out.println("\nPlease insert the coordinates where you want to deploy your number " + i + 1 + " ship!\n");
            int x, y;
            System.out.print("X coordinate: ");
            x = takeCoordinate();
            System.out.print("Y coordinate: ");
            y = takeCoordinate();
            while (grid[x][y] == 1) {
                System.out.println("Please make sure to use other coordinates. You already have placed one ship here");
                System.out.print("X coordinate: ");
                x = takeCoordinate();
                System.out.print("X coordinate: ");
                y = takeCoordinate();
            }
            grid[x][y] = 1;
            sendCoordinates(socket, x, y);
        }
        delay(1);
        System.out.println("\nShips are being deployed...");
        delay(2);
        System.out.println("Ships have been deployed in the below positions...\n");
        return grid;
    }
    public static int takeCoordinate(){
        String check = "0 1 2 3 4 5 6 7 8 9";
        Scanner in = new Scanner(System.in);
        String temp = in.next();

        while(!check.contains(temp)){
            System.out.println("Please insert a NUMBER 0-9");
            temp = in.next();
        }

        return  Integer.parseInt(temp);
    }

    public static void showNewSea(int[][] grid) {
        System.out.println("   0123456789  ");
        for(int i=0;i<=9;i++){
            System.out.print(i+ " !");
            for(int j=0;j<=9;j++){
                if(grid[i][j]==0||grid[i][j]==2){
                    System.out.print(" ");
                } else if (grid[i][j]==1) {
                    System.out.print("u");
                } else if (grid[i][j]==3){
                    System.out.print("-");
                } else if (grid[i][j]==4){
                    System.out.print("x");
                } else if (grid[i][j]==5){
                    System.out.print("*");
                }
            }
            System.out.println("! " +i);
        }
        System.out.println("   0123456789  ");

    }

    public static void sendCoordinates(Socket socket, int x, int y){
        try {
            String message = x + " " + y;
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
            output.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] takingEnemyShipCoordinates(Socket socket){

        try{
            System.out.println("\nPlease wait while enemy is deplyoing its ships...");
            delay(2);
            boolean eleminated = false;
            for (int i=1; i<=3; i++){
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Waiting for the "+ i +" ship");
                String mess = input.readLine();
                String temp = mess;
                int ocurrence = mess.indexOf(' ');
                int x=Integer.parseInt(mess.substring(0, ocurrence));
                int y=Integer.parseInt(temp.substring(ocurrence+1));
                System.out.println("Ship nr " + i + " has been deployed");
                if (grid[x][y]==1){
                    delay(1);
                    System.out.println("\nUPPS! Enemy placed its ship in the same coordinates with yours." +
                            " Both of your ships have been destroyed\n");
                    eleminated =true;
                    ships[0]--;
                    ships[1]--;grid[x][y]=3;
                    delay(1);
                }else {
                    grid[x][y] = 2;
                }

            }
            if (eleminated){
                delay(2);
                System.out.println("\n\nAfter your ship has been drown your army looks like this");
                showNewSea(grid);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return grid;
    }

    public static int[][] yourShot(Socket socket, int[][] grid) {
        Scanner input = new Scanner(System.in);
        System.out.println("It is your turn!\n");
        System.out.print("Enter your X coordinate: ");
        int x=takeCoordinate();
        System.out.print("\nEnter your Y coordinate: ");
        int y=takeCoordinate();
        delay(1);
        System.out.println("\nRocket is flyyiinnggg...");
        sendCoordinates(socket, x, y);
        grid = checkUserResult(grid, x, y);
        delay(1);
        return grid;
    }

    public static int[][] checkUserResult(int[][] grid, int x, int y) {
        delay(2);
        if (grid[x][y]==0){
            System.out.println("You missed!\n");
            grid[x][y]=5;
        } else if (grid[x][y]==1){
            System.out.println("UPPS! You destroyed your own ship!\n");
            grid[x][y]=3;
        } else if (grid[x][y]==2){
            System.out.println("BRAVO! You destroyed an enemy ship!\n");
            grid[x][y]=4;
        }
        return grid;
    }
    public static int[][] enemyShot(Socket socket, int[][] grid){
        try{
            System.out.println("\nPlease wait while enemy is choosing its shooting coordiantes...");
            delay(2);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mess = input.readLine();
            System.out.println("\nIncomingggg....\n");
            String temp = mess;
            int ocurrence = mess.indexOf(' ');
            int x=Integer.parseInt(mess.substring(0, ocurrence));
            int y=Integer.parseInt(temp.substring(ocurrence+1));
            delay(1);
            grid = checkEnemyShotResult(grid, x, y);
        }catch (IOException e){
            e.printStackTrace();
        }
        return grid;
    }
    public static int[][] checkEnemyShotResult(int[][] grid, int x, int y) {
        if (grid[x][y]==0){
            System.out.println("Huh! Enemy missed! We are safe!");
            grid[x][y]=5;
        } else if (grid[x][y]==1){
            System.out.println("UPPS! Enemy destroyed your ship!");
            grid[x][y]=3;
        } else if (grid[x][y]==2){
            System.out.println("YUHUU! Enemy destroyed its own ship!");
            grid[x][y]=4;
        }
        return grid;
    }
    public static int[] showShips(int[][] grid) {
        int[] ships = new int[2];
        int myShips=0, compShips=0;
        for(int i=0; i<=9; i++){
            for(int j=0; j<=9; j++){
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
}
