import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;
    public static boolean[] checkOreientation(char[][] board, int x , int y, int rows, int columns){ // could merge to
        // checkDamage but if we go by "every function should do only 1 thing" it makes (some) sense to do it this way
        boolean[] oreintation_xy = {false, false};
        if (x+1 < rows){
            if (board[x+1][y] != '–'){
                oreintation_xy[0] = true;
                return oreintation_xy;
            }
        }
        if (x-1 >0){// could replace to x>1 but more readable this way
            if (board[x-1][y] != '–'){
                oreintation_xy[0] = true;
                return oreintation_xy;
            }
        }
        if (y+1 < columns){
            if (board[x][y+1] != '–'){
                oreintation_xy[1] = true;
                return oreintation_xy;
            }
        }
        if (y-1 > 0){
            if(board[x][y-1] != '–'){
                oreintation_xy[1] = true;
                return oreintation_xy;
            }
        }
        return oreintation_xy;
    }
    public static boolean checkDmg(char[][] board,int x, int y, int rows , int columns){
        boolean[] oreintation_xy = checkOreientation(board,x,y,rows,columns);
        if (oreintation_xy[0] == true){

        }
        else if (oreintation_xy[1]==true) {

        }
        return true;
    }

    public static boolean checkHit (char[][] board , char [][] guessing_board,int x, int y , int rows , int columns ,
                                boolean player){
        boolean flag = false;
        if((x<0) | (x>= columns) | (y<0) | (y>=rows)) {
            if (player)
                System.out.println("Illegal tile, try again!");
        }
        else if (guessing_board[x][y] == 'X' | guessing_board[x][y] == 'V') {
            if (player)
                System.out.println("Tile already attacked, try again!");
        }
        else{
            if(board[x][y] == '#') {
                flag = true;
                board[x][y] = 'X';
                if (player) {
                    guessing_board[x][y] = 'X';
                    System.out.println("That is a hit!");
                } else {
                    System.out.println("the computer attack " + x + "," + y);
                }
            }
        }
        return flag;
    }
    public static boolean gameStart(char[][] player_board, char[][] guessing_board, char[][] pc_board, int ship_count){
        int x,y;
        String[] location;
        int i =0;
        int player_ship_count = ship_count;
        int pc_ship_count = ship_count;
        int rows = player_board.length;
        int columns = player_board.length;
        while(player_ship_count>0 | pc_ship_count>0){
            if (i%2 ==0){
                System.out.println("Your current guessing board:");
                printBoard(guessing_board);
                System.out.println("Enter a tile to attack");
                location = scanner.nextLine().replaceAll(" ","").split(",");
                x = Integer.parseInt(location[0]); // could skip these but added to make it abit more readable
                y = Integer.parseInt(location[1]);
                if(checkHit(pc_board,guessing_board,x,y,rows,columns,true))
                    i++;
            }
            else{ // computers turn
                x = rnd.nextInt(rows);
                y = rnd.nextInt(columns);
                if(checkHit(player_board, player_board,x,y,rows,columns,false))
                    i++;
            }
        }
        return player_ship_count != 0;
    }
    public static void printBoard(char[][] board){
        int rows = board.length;
        int columns = board[0].length;
        int m; // used for the offset in the first line
        for (int i=0; i< rows ; i++){
            for (int j=0; j<=columns; j++){
                if (i==0){
                    if(j==0)
                        System.out.print(" " +"");
                    else {
                        m = j-1;
                        System.out.print(" " + m);
                    }
                }
                else {
                    if (j == 0)
                        System.out.print("" + i);
                    else
                        System.out.print(" " + board[i][j - 1]);
                }
            }
            System.out.println();
        }
    }
    public static int checkIfLegalPlacement(char[][] board,int columns , int rows ,int i, int j , int[] arr_ships ,
                                            int[] location ,boolean player){
        int k;
        int p;
        int flag = 1;
        if ((location[0] < 0) | (location[0] >= columns) | (location[1] < 0) | (location[1] >= rows)){
            if (player)
                System.out.println("Illegal tile, try again!");
        }

        else if (location.length!=3 | location[2] != 0 & location[2] !=1 ) {
            if (player)
                System.out.println("Illegal orientation, try again!");
        }
        else if (((location[2]==1)&(location[1])+arr_ships[(i*2) +1] >= rows) | // winged it could be wrong index
                ((location[2]==0)&(location[0] + arr_ships[i*2] >= columns))){
            if (player)
                System.out.println("Battleship exceeds the boundaries of the board, try again!");
        }
        //else if () // need to think of a good way to check if any ship is too close
        else
            flag =0;
        if (location[2] == 1){
            for(k=0; k < arr_ships[(i*2)+1];k++) {
                if (k >=0 & k <arr_ships[(i*2)+1])
                    if (board[location[0]][location[1] + k] == '#') {
                        if (player)
                            System.out.println("Battleship overlaps with another battleship, try again!");
                        return 1;
                }
                //for (p)

            }
        }
        else
            for (k=0; k< arr_ships[i*2];k++){
                if (board[location[0]+k][location[1]] == '#') {
                    if (player)
                        System.out.println("Battleship overlaps with another battleship, try again!");
                    return 1;
                }
        }

        if(flag == 0)
            return 0;
        else return 1;
    }
    public static int[] generatePCLocation(int rows, int columns){
        int[] location = new int[3];
        location[0] = rnd.nextInt(rows); // x cord
        location[1] = rnd.nextInt(columns); // y cord
        location[2] = rnd.nextInt(2); // orientation
        return location;
    }
    public static char[][] placeBattleships(char[][] board , int[] arr_ships, boolean player){
        int amount_of_ships = arr_ships.length/2;
        int rows = board.length;
        int columns = board[0].length;
        int[] location;
        for (int i=0 ; i < amount_of_ships;i++){
            for (int j=0 ; j<arr_ships[i*2];){
                if(player) {
                    System.out.println("Enter location and orientation");
                    location = stringToIntArray(scanner.nextLine());
                }
                else {
                    location = generatePCLocation(rows,columns); // generates a location for PC ship
                }
                if (checkIfLegalPlacement(board,columns,rows,i,j,arr_ships,location,true) == 1)
                    continue;
                else{
                    for (int p=0;p < arr_ships[i*2+1]; p++){
                        if (location[2]==1){
                            board[location[0]][location[1]+p] = '#';
                        }
                        else
                            board[location[0]+p][location[1]] = '#';
                    }
                    j++;
                    if(player){
                        System.out.println("Your current game board:");
                        printBoard(board);
                    }

                }

            }


        }
        return board;
    }
    public static int[] stringToIntArray(String mystring){

        mystring = mystring.replaceAll(" ","X");
        mystring = mystring.replaceAll(",","");
        String[] split_string = mystring.split("X");
        int size_of_string = split_string.length;

        int[] arr = new int[size_of_string];
        for (int i = 0 ; i < size_of_string; i++){
            arr[i] = Integer.parseInt(split_string[i]);
        }
        return arr;
    }
    public static char[][] createBoard(String board_size){
        int[] int_arr = stringToIntArray(board_size);
        int row = int_arr[0];
        int column = int_arr[1];
        char[][] board = new char[row][column];

        for (int i =0 ; i < row ; i++) {
            for (int j = 0; j < column; j++) {
                board[i][j] = '–';
            }
        }
        return board;
    }
    public static void battleshipGame() {
        System.out.println("Enter the board size");
        String board_size = scanner.nextLine();
        char[][] player_board = createBoard(board_size); // create boards
        char[][] pc_board = createBoard(board_size);
        char[][] player_guessing_board = createBoard(board_size);

        String string_of_ships = scanner.nextLine();
        int[] array_of_ships = stringToIntArray(string_of_ships); // create the array of ships to play on player board
        int ship_count = string_of_ships.length()/2;

        placeBattleships(player_board,array_of_ships,true); // place ships on player generated board
        placeBattleships(pc_board, array_of_ships,false); // place ships on pc generated board

        if(gameStart(player_board,player_guessing_board,pc_board,ship_count)) // starts the game and checks result
            System.out.println("You won the game!");
        else
            System.out.println("You lost ):");
    }

    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }
}



