import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;
    public static boolean[] checkOrientation(char[][] board, int x , int y, int rows, int columns){
        // could be merged to checkDamage but if we go by "every function should do only 1 thing" it makes (some)
        // sense to do it this way

        boolean[] orientation_xy = {false, false};
        if ((x+1<rows)&&board[x+1][y] != '–'){
            orientation_xy[1] = true;
        }
        else if ((x-1 >= 0)&&(board[x-1][y] != '–')){// could replace to x>1 but more readable this way
            orientation_xy[1] = true;
        }
        else if ((y+1 < columns)&&(board[x][y+1] != '–')){
            orientation_xy[0] = true;
        }
        else if ((y-1 >= 0)&&(board[x][y-1] != '–')){
            orientation_xy[0] = true;

        }
        return orientation_xy;
    }
    public static boolean checkDmg(char[][] board,int x, int y, int rows , int columns){ // searches for '#" - undamaged
        boolean[] orientation_xy = checkOrientation(board,x,y,rows,columns);
        int i;
        boolean flag_1 = true;
        boolean flag_2 = true;
        if (orientation_xy[1]){     //searching row for both direction
            for(i=1;(flag_1)&&(x+i<rows) && (board[x+i][y]!='–');i++) {
                if (board[x + i][y] == '#') {
                    flag_1 = false;
                }
            }
            for(i=1;(flag_1)&& (x-i>=0) && (board[x-i][y]!='–');i++) {
                if (board[x - i][y] == '#') {
                    flag_2 = false;
                }
            }
        }
        else if(orientation_xy[0]) { //searching column for both directions
            for(i=1;(flag_2)&&(y+i<columns) && (board[x][y+i]!='–');i++) {
                if (board[x][y + i] == '#') {
                    flag_2 = false;
                }
            }
            for(i=1;(flag_2)&&(y-i>=0) && (board[x][y-i]!='–');i++) {
                if (board[x][y-i] == '#') {
                    flag_2 = false;
                }
            }
        }

        return (flag_1&flag_2); // no undamaged parts were found therefore the ship must have gotten destroyed
    }

    public static boolean checkHit (char[][] board , char [][] guessing_board,int x, int y , int rows , int columns ,
                                boolean player){
        boolean flag = false;
        if((x<0) || (x>= rows) || (y<0) || (y>=columns)) {
            if (player)
                System.out.println("Illegal tile, try again!");
        }
        else if ((guessing_board[x][y] == 'X') || (guessing_board[x][y] == 'V')) {
            if (player)
                System.out.println("Tile already attacked, try again!");
        }
        else{
            flag=true;
            if(board[x][y] == '#') {
                if (player) {
                    board[x][y] = 'V';
                    guessing_board[x][y] = 'V';
                    System.out.println("That is a hit!");
                } else {
                    board[x][y] = 'X';
                    guessing_board[x][y] = 'X';
                    System.out.println("the computer attacked (" + x + ',' + y + ')');
                }
            }
            else{
                if(player){
                    guessing_board[x][y] = 'X';
                    System.out.println("That is a miss!");

                }
                else{
                    guessing_board[x][y] = 'X';
                }


            }
        }
        return flag;
    }
    public static boolean gameStart(char[][] player_board, char[][] guessing_board, char[][] pc_board,
                                    char[][] pc_guessing_board, int ship_count){
        int x,y;
        String[] location;
        int i =0;
        int player_ship_count = ship_count;
        int pc_ship_count = ship_count;
        int rows = player_board.length;
        int columns = player_board[0].length;

        while((player_ship_count>0) && (pc_ship_count>0)){
            if (i%2 ==0){
                System.out.println("Your current guessing board:");
                printBoard(guessing_board);
                System.out.println("Enter a tile to attack");
                location = scanner.nextLine().replaceAll(" ","").split(",");
                x = Integer.parseInt(location[0]); // could skip these but added to make it a bit more readable
                y = Integer.parseInt(location[1]);

                while(!(checkHit(pc_board,guessing_board,x,y,rows,columns,true))) {
                    location = scanner.nextLine().replaceAll(" ", "").split(",");
                    x = Integer.parseInt(location[0]); //dupe from the line above need to think on how to merge
                    y = Integer.parseInt(location[1]);
                }
                if (((pc_board[x][y] =='V'))&&(checkDmg(pc_board, x, y, rows, columns))) {
                        pc_ship_count--;
                        System.out.println("The computer's battleship has been drowned "+ pc_ship_count +
                                " more battleships to go!");
                }
                i++;
            }
            else{ // computers turn
                x = rnd.nextInt(rows);
                y = rnd.nextInt(columns);
                if(checkHit(player_board, pc_guessing_board,x,y,rows,columns,false)){
                    if ((player_board[x][y]=='X')&&(checkDmg(player_board, x, y, rows, columns))) {
                        player_ship_count--;
                        System.out.println("Your battleship has been drowned, you have left " + player_ship_count + "" +
                                " more battleships!");
                    }
                    System.out.println("Your current game board:");
                    printBoard(player_board);
                    i++;
                }
            }
        }
        return player_ship_count != 0;
    }
    public static void printBoard(char[][] board){
        int rows = board.length;
        int columns = board[0].length;
        int offset_rows;
        int k=rows-1;
        int counter_row=0;
        int counter_row_2;
        while(k>0){
            k /= 10;
            counter_row ++;
            System.out.print(" ");
        }
        for (int i=0; i<= rows ; i++){
            for (int j=0; j<=columns; j++){
                if (i==0){
                    if(j!=0) {
                        System.out.print(" " + (j-1));
                    }
                }
                else {
                    if (j == 0) {
                        offset_rows = i-1;
                        counter_row_2=0;
                        while (offset_rows > 0) {
                            offset_rows /= 10;
                            counter_row_2++;
                        }
                        if ((i==1)&&(rows-1!=0)){
                            counter_row_2 ++;
                        }
                        while (counter_row_2 < counter_row) {
                            counter_row_2++;
                            System.out.print(" ");
                        }
                        System.out.print(i - 1);
                    }
                    else {
                        System.out.print(" " + board[i - 1][j - 1]);
                    }
                }
            }
            System.out.println();
        }
    }
    public static boolean checkIfLegalPlacement(char[][] board,int rows , int columns ,int ship_size,
                                            int[] location ,boolean player){
        boolean flag = false;
        int x = location[0];
        int y = location[1];
        int orientation = location[2];
        int ship_width = 1;
        if (((orientation==1)&&( x + ship_size > rows)) |
            ((orientation==0)&&( y + ship_size > columns))) {
            if (player) {
                System.out.println("Battleship exceeds the boundaries of the board, try again!");
            }
        }
        else if ((location.length!=3) || ((orientation != 0) && (orientation !=1 ))) {
            if (player) {
                System.out.println("Illegal orientation, try again!");
            }
        }
        else if ((x < 0) || (x >= rows) || (y < 0) || (y >= columns)){
            if (player) {
                System.out.println("Illegal tile, try again!");
            }
        }
        else {
            flag = true;
            if (orientation == 1) { // seems a bit too complicated checks the grid around the ship
                for (int i = -1; i <= ship_size; i++) {
                    for (int j = -1; j <= ship_width ; j++){
                        if ((x+ i >=0)&&(x+ i < rows)&&(y+ j >=0)&&(y+ j < columns)&&(board[x+ i][y+ j] == '#')) {
                            if (player) {
                                if (j == 0) {
                                    System.out.println("Battleship overlaps with another battleship, try again!");
                                } else {
                                    System.out.println("Adjacent battleship detected, try again!");
                                }
                            }
                            return false;
                        }
                    }
                }
            }
            else {
                for (int i = -1; i <= ship_size; i++) {
                    for(int j = -1; j <=ship_width ; j++)
                        if ((x+ j <rows)&&(x+ j >=0)&&(y+ i <columns)&&( y+ i >=0)&&(board[x+ j][y+ i] == '#')) {
                            if (player) {
                                if (j == 0) {
                                    System.out.println("Battleship overlaps with another battleship, try again!");
                                } else {
                                    System.out.println("Adjacent battleship detected, try again!");
                                }
                            }
                            return false;
                    }
                }
            }
        }
        return flag;

    }
    public static int[] getPCLocation(int rows, int columns){
        int[] location = new int[3];
        location[0] = rnd.nextInt(rows); // x cord
        location[1] = rnd.nextInt(columns); // y cord
        location[2] = rnd.nextInt(2); // orientation
        return location;
    }
    public static void placeBattleships(char[][] board ,int ship_variance, int[] arr_ships, boolean player){
        int rows = board.length;
        int columns = board[0].length;
        int[] location;
        int ship_size;
        int ship_count;
        for (int i=0 ; i < ship_variance;i++){
            ship_size = arr_ships[(i*2) + 1];
            if (player){
                System.out.println("Enter location and orientation for battleship of size " + ship_size);
            }
            ship_count = arr_ships[i*2];
            for (int j=0 ; j<ship_count;){
                if(player) {
                    location = stringToIntArray(scanner.nextLine());
                }
                else {
                    location = getPCLocation(rows,columns); // generates a location for PC ship
                }
                if (checkIfLegalPlacement(board,rows,columns,ship_size,location,player)){
                    int x = location[0];
                    int y = location[1];
                    for (int p=0;p < ship_size; p++){
                        if (location[2]==0){
                            board[x][y+p] = '#';
                        }
                        else
                            board[x+p][y] = '#';
                    }
                    j++;
                    if(player){
                        System.out.println("Your current game board:");
                        printBoard(board);
                    }
                }
            }
        }
    }
    public static int[] stringToIntArray(String mystring){

        mystring = mystring.replaceAll(" ","X").replaceAll(",","");
        String[] split_string = mystring.split("X");
        int size_of_string = split_string.length;

        int[] int_arr = new int[size_of_string];
        for (int i = 0 ; i < size_of_string; i++){
            int_arr[i] = Integer.parseInt(split_string[i]);
        }
        return int_arr;
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
    public static int countShips(int[] array_of_ships){
        int different_sizes = array_of_ships.length/2;
        int sum =0;
        for(int i =0 ; i < different_sizes;i++){
            sum += array_of_ships[i*2];
        }
        return sum;
    }
    public static void battleshipGame() {
        System.out.println("Enter the board size");
        String board_size = scanner.nextLine();
        char[][] player_board = createBoard(board_size); // create boards
        char[][] pc_board = createBoard(board_size);
        char[][] player_guessing_board = createBoard(board_size);
        char[][] pc_guessing_board = createBoard(board_size);


        String string_of_ships = scanner.nextLine();
        int[] array_of_ships = stringToIntArray(string_of_ships); // create the array of ships to play on player board
        int ship_count = countShips(array_of_ships);
        int ship_variance = array_of_ships.length/2;
        System.out.println("Your current game board: "); // temp here
        printBoard(player_guessing_board);              // temp here


        placeBattleships(player_board,ship_variance,array_of_ships ,true); // place ships on player generated board
        placeBattleships(pc_board, ship_variance,array_of_ships,false); // place ships on pc generated board

        if(gameStart(player_board,player_guessing_board,pc_board,pc_guessing_board,ship_count)) // starts the game and checks result
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



