import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import za.ac.wits.snake.DevelopmentAgent;

//test
public class MyAgent extends DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    final String SNAKE = "snakeDescription";
    final String APPLE = "apple";
    final String OBSTACLE = "obstacleDescription";
    final String SNAKENUM = "snakeNum";

    // declare variables
    String[] snakeSplit = null;
    String[] obsLineSplit = null;

    String snakeHeadString = null;

    String[] snakeHeadSplit = null;

    int[] snakeHead = { 0, 0 };

    ArrayList<int[]> mySnake = new ArrayList<>();

    ArrayList<ArrayList<int[]>> obstacles = new ArrayList<>();
    ArrayList<ArrayList<int[]>> snakes = new ArrayList<>();

    int[] apple = { 0, 0 };

    int move = 9999;

    int timeStep = -1;

    int N = 50;

    int[][] grid = new int[N][N]; // [row][column]

    int[] mainTarget = { 0, 0 };

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();

            String[] temp = initString.split(" ");
            int nSnakes = Integer.parseInt(temp[0]);
            int numObstacles = 3;

            // --------------

            while (true) {

                timeStep += 1;

                String line = br.readLine(); // READ IN APPLE

                String appleString = line;
                // do stuff with apples

                String[] appleSplit = appleString.split(" ");

                apple[0] = Integer.parseInt(appleSplit[0]);
                apple[1] = Integer.parseInt(appleSplit[1]);

                if (line.contains("Game Over")) {
                    break;
                }

                // READ IN OBSTACLES

                for (int j = 0; j < numObstacles; j++) {

                    String obsLine = br.readLine();

                    // make each obstacle an array list of arrays, where each array is a coordinate
                    obsLineSplit = obsLine.split(" ");
                    ArrayList<int[]> obs = new ArrayList<>();

                    for (String c : obsLineSplit) {
                        String[] cSplit = c.split(",");
                        int[] cInt = { Integer.parseInt(cSplit[0]), Integer.parseInt(cSplit[1]) };

                        obs.add(cInt);
                    }

                    obstacles.add(obs);

                }

                // READ IN OBSTACLES ============================= END

                int mySnakeNum = Integer.parseInt(br.readLine()); // READ IN SNAKE NUMBER

                // READ IN AND CREATE SNAKES
                snakes.clear();
                for (int i = 0; i < nSnakes; i++) // READ IN SNAKES
                {

                    String snakeLine = br.readLine();

                    // make each snake an array list of arrays, with each array being a coordinate

                    snakeSplit = snakeLine.split(" ");
                    ArrayList<int[]> snake = new ArrayList<>();

                    int[] previousC = { 0, 0 };
                    int cCount = 0;
                    for (String c : snakeSplit) {

                        if (c.contains(",") == false) {
                            continue;
                        }

                        String[] cSplit = c.split(",");
                        int[] cInt = { Integer.parseInt(cSplit[0]), Integer.parseInt(cSplit[1]) };

                        snake.add(cInt);

                        // fill rest of snake

                        if (cCount > 0) {
                            // fill rest of snake
                            int minX = Math.min(previousC[0], cInt[0]); // Math.min(startPoint[0], endPoint[0]);
                            int maxX = Math.max(previousC[0], cInt[0]);

                            int minY = Math.min(previousC[1], cInt[1]);
                            int maxY = Math.max(previousC[1], cInt[1]);

                            for (int x = 0; x < N; x++) {
                                for (int y = 0; y < N; y++) {

                                    if ((x >= minX && x <= maxX) && (y >= minY && y <= maxY)) {
                                        // playArea[x][y] = snakeNumber;
                                        int[] tempC = { x, y };
                                        snake.add(tempC);
                                    }

                                }
                            }

                        }
                        previousC = cInt;

                        cCount += 1;
                    }

                    // make sure start of array is head and end is tail

                    try {
                        String otherSnakeHeadString = snakeSplit[3];
                        String otherSnakeTailString = snakeSplit[snakeSplit.length - 1];

                        String[] otherSnakeHeadSplit = otherSnakeHeadString.split(",");
                        String[] otherSnakeTailSplit = otherSnakeTailString.split(",");

                        int[] otherSnakeHead = { 0, 0 };
                        int[] otherSnakeTail = { 0, 0 };

                        otherSnakeHead[0] = Integer.parseInt(otherSnakeHeadSplit[0]);
                        otherSnakeHead[1] = Integer.parseInt(otherSnakeHeadSplit[1]);

                        otherSnakeTail[0] = Integer.parseInt(otherSnakeTailSplit[0]);
                        otherSnakeTail[1] = Integer.parseInt(otherSnakeTailSplit[1]);

                        if (snake.contains(otherSnakeHead)) {
                            snake.remove(otherSnakeHead);
                            snake.add(0, otherSnakeHead);
                        }
                        if (snake.contains(otherSnakeTail)) {
                            snake.remove(otherSnakeTail);
                            snake.add(snake.size() - 1, otherSnakeTail);
                        }
                    } catch (Exception e) {

                    }

                    // --------------------------------------------

                    if (i == mySnakeNum) {
                        // hey! That's me :)

                        snakeSplit = snakeLine.split(" ");

                        snakeHeadString = snakeSplit[3];

                        snakeHeadSplit = snakeHeadString.split(",");

                        snakeHead[0] = Integer.parseInt(snakeHeadSplit[0]);
                        snakeHead[1] = Integer.parseInt(snakeHeadSplit[1]);

                        // System.out.println("log" + snakeHeadString);

                        mySnake = snake;

                    } else {
                        if (snake.size() != 0) {
                            snakes.add(snake);
                        }

                    }

                    // do stuff with other snakes
                }
                // DONE READING AND CREATING SNAKES ================================

                // clear grid

                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        grid[i][j] = 0;
                    }
                }

                // Mark filled points

                // for (int i = 0; i < 50; i++) // borders
                // {
                // grid[i][0] = 9;
                // grid[0][i] = 9;
                // grid[49][i] = 9;
                // grid[i][49] = 9;
                // }

                for (ArrayList<int[]> obs : obstacles) // obstacles
                {
                    for (int[] c : obs) {
                        grid[c[0]][c[1]] = 5;
                    }
                }

                int snakeNum = 1;

                for (ArrayList<int[]> snake : snakes) // snakes
                {

                    for (int[] c : snake) {
                        grid[c[0]][c[1]] = snakeNum;
                    }

                    snakeNum += 1;

                }

                for (int[] c : mySnake) {
                    grid[c[0]][c[1]] = snakeNum;
                }

                // Mark filled points =========== end

                // finished reading, calculate move:

                // get to apple

                if (willReachAppleFirst()) {
                    // mainTarget = apple;
                    goToTarget(apple);
                } else {
                    // mainTarget = emptiestPoint();

                    if (snakeHead[0] > snakeHead[1]) {
                        moveVertical();
                    } else {
                        moveHorizontal();
                    }
                    //
                    // if (snakeDirection(mySnake) == "r" || snakeDirection(mySnake) == "l")
                    // {
                    // moveVertical();
                    // } else if (snakeDirection(mySnake) == "u" || snakeDirection(mySnake) == "d")
                    // {
                    // moveHorizontal();
                    // }
                }

                // mainTarget[0] = 49;
                // mainTarget[1] = 0;

                // goToTarget(mainTarget);

                addSnakeFuture(2);

                // MARK HEADS
                for (ArrayList<int[]> snake : snakes) // snakes
                {

                    grid[snake.get(0)[0]][snake.get(0)[1]] = 7; // head
                    grid[snake.get(snake.size() - 1)[0]][snake.get(snake.size() - 1)[1]] = 6; // tail

                }

                avoidAllObstacles();

                // when about to hit edge
                // avoidEdges();

                // System.out.println("log direction:" + snakeDirection(mySnake));

                // if (targetToHead(apple, snakeHead).contains("d"))
                // {
                // System.out.println("log" + "head: " + snakeHeadString + " apple: " +
                // appleString + " location: " + targetToHead(apple, snakeHead));
                // }

                // System.out.println("log snakeHead: [" + snakeHead[0] + "," + snakeHead[1] +
                // "]");

                // System.out.println("log emptiestPoint: [" + point[0] + "," + point[1] + "]"
                // );

                // int tempporary = distBelow();

                int[] point = emptiestPoint();
                grid[point[0]][point[1]] = 7;

                print2DArray(grid);

                System.out.println(move);

            } // end while

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean willReachAppleFirst() {

        int myDis = distance(snakeHead, apple);

        for (ArrayList<int[]> snake : snakes) {
            if (snake.size() == 0) {
                continue;
            }

            int[] head = { snake.get(0)[0], snake.get(0)[1] };
            int dis = distance(head, apple);

            if (dis <= myDis) {
                return false;
            }
        }

        return true;
    }

    public String typeOfInput(String inputText) {
        if (inputText.length() <= 5 && inputText.length() > 2) {
            return APPLE;
        } else if (inputText.length() == 1) {
            return SNAKENUM;
        } else if (inputText.contains("alive") || inputText.contains("dead")) {
            return SNAKE;
        } else {
            return OBSTACLE;
        }
    }

    public String safestDirection() {
        return "";
    }

    public void moveVertical() {

        if (snakeHead[0] == apple[0]) {
            if (willReachAppleFirst()) {
                if (apple[1] < snakeHead[1]) // apple above
                {
                    moveUp();
                } else // apple below
                {
                    moveDown();
                }
                return;
            }
        }

        if (distAbove() > distBelow()) {
            moveUp();
            // System.out.println("log moveUp");
        } else {
            moveDown();
            // System.out.println("log moveDown");
        }

    }

    public void moveHorizontal() {

        if (snakeHead[1] == apple[1]) {
            if (willReachAppleFirst()) {
                if (apple[0] < snakeHead[0]) // apple to the left
                {
                    moveLeft();
                } else // apple to the right
                {
                    moveRight();
                }
                return;
            }
        }

        if (distRight() > distLeft()) {
            moveRight();
            // System.out.println("log moveRight");
        } else {
            moveLeft();
            // System.out.println("log moveLeft");
        }

    }

    public int distRight() {
        // check to the right of head
        int[] closestPointRight = { 999, 999 };
        for (int x = snakeHead[0] + 1; x < 50; x++) {
            if (grid[x][snakeHead[1]] > 0) {
                closestPointRight[0] = x;
                closestPointRight[1] = snakeHead[1];
                break;
            }
        }
        int distRight = Math.abs(snakeHead[0] - closestPointRight[0]);
        if (closestPointRight[0] == 999 && closestPointRight[1] == 999) {
            distRight = 50 - snakeHead[0];
        }

        System.out.println("log dist Right: " + distRight + " head: " + "[" + snakeHead[0] + "," + snakeHead[1] + "]"
                + " obs: " + "[" + closestPointRight[0] + "," + closestPointRight[1] + "]");

        return distRight;
    }

    public int distLeft() {
        // check to the left of head
        int[] closestPointLeft = { 999, 999 };
        for (int x = snakeHead[0] - 1; x >= 0; x--) {
            if (grid[x][snakeHead[1]] > 0) {
                closestPointLeft[0] = x;
                closestPointLeft[1] = snakeHead[1];
                break;
            }
        }

        int distLeft = Math.abs(snakeHead[0] - closestPointLeft[0]);
        if (closestPointLeft[0] == 999 && closestPointLeft[1] == 999) {
            distLeft = snakeHead[0] + 1;
        }

        // System.out.println("log dist Left: " + distLeft + " head: " + "[" +
        // snakeHead[0] + "," + snakeHead[1] + "]" + " obs: " + "[" +
        // closestPointLeft[0] + "," + closestPointLeft[1] + "]");

        return distLeft;
    }

    public int distAbove() {
        // check above head
        int[] closestPointAbove = { 999, 999 };
        for (int y = snakeHead[1] - 1; y >= 0; y--) {
            if (grid[snakeHead[0]][y] > 0) {
                closestPointAbove[0] = snakeHead[0];
                closestPointAbove[1] = y;
                break;
            }
        }

        int distAbove = Math.abs(snakeHead[1] - closestPointAbove[1]);
        if (closestPointAbove[0] == 999 && closestPointAbove[1] == 999) {
            distAbove = snakeHead[1] + 1;
        }

        // System.out.println("log dist Above: " + distAbove + " head: " + "[" +
        // snakeHead[0] + "," + snakeHead[1] + "]" + " obs: " + "[" +
        // closestPointAbove[0] + "," + closestPointAbove[1] + "]");

        return distAbove;
    }

    public int distBelow() {
        // check below head
        int[] closestPointBelow = { 999, 999 };
        for (int y = snakeHead[1] + 1; y < 50; y++) {
            if (grid[snakeHead[0]][y] > 0) {
                closestPointBelow[0] = snakeHead[0];
                closestPointBelow[1] = y;
                break;
            }
        }
        int distBelow = Math.abs(snakeHead[1] - closestPointBelow[1]);
        if (closestPointBelow[0] == 999 && closestPointBelow[1] == 999) {
            distBelow = 50 - snakeHead[1];
        }

        // System.out.println("log dist Below: " + distBelow + " head: " + "[" +
        // snakeHead[0] + "," + snakeHead[1] + "]" + " obs: " + "[" +
        // closestPointBelow[0] + "," + closestPointBelow[1] + "]");

        return distBelow;

    }

    public int distance(int[] a, int[] b) {
        int distance = Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
        return distance;
    }

    public void goToTarget(int[] target) {
        if (targetToHead(target, snakeHead) == "da") {
            moveUp();
        } else if (targetToHead(target, snakeHead) == "db") {
            moveDown();
        } else if (targetToHead(target, snakeHead) == "dl") {
            moveLeft();
        } else if (targetToHead(target, snakeHead) == "dr") {
            moveRight();
        }

        if (snakeDirection(mySnake) == "r") {
            if (targetToHead(target, snakeHead) == "bl") {
                moveDown();
            } else if (targetToHead(target, snakeHead) == "al") {
                moveUp();
            }
        } else if (snakeDirection(mySnake) == "l") {
            if (targetToHead(target, snakeHead) == "br") {
                moveDown();
            } else if (targetToHead(target, snakeHead) == "ar") {
                moveUp();
            }
        } else if (snakeDirection(mySnake) == "u") {
            if (targetToHead(target, snakeHead) == "br") {
                moveRight();
            } else if (targetToHead(target, snakeHead) == "bl") {
                moveLeft();
            }
        } else if (snakeDirection(mySnake) == "d") {
            if (targetToHead(target, snakeHead) == "ar") {
                moveRight();
            } else if (targetToHead(target, snakeHead) == "al") {
                moveLeft();
            }
        }

    }

    public void avoidAllObstacles() {
        if (snakeDirection(mySnake) == "r") {
            if (distRight() < 2 || snakeHead[0] == 49) {
                moveVertical();
            }

        } else if (snakeDirection(mySnake) == "l") {
            if (distLeft() < 2 || snakeHead[0] == 0) {
                moveVertical();
            }
        } else if (snakeDirection(mySnake) == "u") {
            if (distAbove() < 2 || snakeHead[1] == 0) {
                moveHorizontal();
            }
        } else if (snakeDirection(mySnake) == "d") {
            if (distBelow() < 2 || snakeHead[1] == 49) {
                moveHorizontal();
            }
        }

    }

    public void moveRight() {
        if (distRight() < 2) {
            return;
        }
        // System.out.println("log move right");
        move = 3;
    }

    public void moveLeft() {
        if (distLeft() < 2) {
            return;
        }
        // System.out.println("log move left");
        move = 2;
    }

    public void moveUp() {
        if (distAbove() < 2) {
            return;
        }
        // System.out.println("log move up");
        move = 0;
    }

    public void moveDown() {
        if (distBelow() < 2) {
            return;
        }
        // System.out.println("log move down");
        move = 1;
    }

    public String snakeDirection(ArrayList<int[]> snake) {

        int[] head = snake.get(0);
        int[] next = snake.get(1);

        if (head[0] == next[0]) // vertical
        {
            if (head[1] < next[1]) {
                return "u";
            } else if (head[1] > next[1]) {
                return "d";
            }
        } else if (head[1] == next[1]) // horizontal
        {
            if (head[0] < next[0]) {
                return "l";
            } else if (head[0] > next[0]) {
                return "r";
            }
        }

        return "nothing returned";
    }

    public String targetToHead(int[] target, int[] head) {

        if (target[0] == head[0]) // apple in same column as head
        {
            if (target[1] < head[1]) {
                return "da"; // directly above
            } else if (target[1] > head[1]) {
                return "db"; // directly below
            }
        } else if (target[1] == head[1]) // apple in same row as head
        {
            if (target[0] < head[0]) {
                return "dl"; // directly left
            } else if (target[0] > head[0]) {
                return "dr"; // directly right
            }
        } else if (target[0] > head[0]) // apple to the right of head
        {
            if (target[1] < head[1]) {
                return "ar"; // above right
            } else if (target[1] > head[1]) {
                return "br"; // below right
            }
        } else if (target[0] < head[0]) // apple to the left of head
        {
            if (target[1] < head[1]) {
                return "al"; // above left
            } else if (target[1] > head[1]) {
                return "bl"; // below left
            }
        }

        return "nothing matched";

    }

    public int[] emptiestPoint() {
        // Grid size
        int N = 50;

        // Boolean array to store empty/filled
        boolean[][] grid = new boolean[N][N];

        // Array to store MIN distance for each point
        int[][] minDist = new int[N][N];

        // Mark filled points

        for (int i = 0; i < 50; i++) // borders
        {
            grid[i][0] = true;
            grid[0][i] = true;
            grid[49][i] = true;
            grid[i][49] = true;
        }

        for (ArrayList<int[]> obs : obstacles) // obstacles
        {
            for (int[] c : obs) {
                grid[c[0]][c[1]] = true;
            }
        }

        for (ArrayList<int[]> snake : snakes) // snakes
        {
            for (int[] c : snake) {
                grid[c[0]][c[1]] = true;
            }
        }

        // Mark filled points =========== end

        // Calculate min distances
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == false) {

                    int min = 5000;

                    // Calculate Manhattan distance to each filled point
                    for (int x = 0; x < N; x++) {
                        for (int y = 0; y < N; y++) {
                            if (grid[x][y]) {
                                int dist = Math.abs(i - x) + Math.abs(j - y);
                                min = Math.min(min, dist);
                            }
                        }
                    }

                    // Store min distance for this empty point
                    minDist[i][j] = min;
                }
            }
        }

        // Find index of max value in distance array
        int maxIndexX = 0;
        int maxIndexY = 0;
        int maxMinDist = 0;

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (minDist[x][y] > maxMinDist) {
                    maxMinDist = minDist[x][y];
                    maxIndexX = x;
                    maxIndexY = y;
                }
            }
        }

        // Return empty point at max min distance
        int[] point = { maxIndexX, maxIndexY };
        return point;
    }

    public void addSnakeFuture(int blocks) {

        for (ArrayList<int[]> snake : snakes) {
            int[] head = snake.get(0);

            int snakeNum = grid[head[0]][head[1]];

            if (snakeDirection(snake) == "r") {
                for (int i = 1; i <= blocks; i++) {
                    if (head[0] + i <= 49) {
                        grid[head[0] + i][head[1]] = snakeNum;
                    }

                }
            } else if (snakeDirection(snake) == "l") {
                for (int i = 1; i <= blocks; i++) {
                    if (head[0] - i >= 0) {
                        grid[head[0] - i][head[1]] = snakeNum;
                    }

                }
            } else if (snakeDirection(snake) == "u") {
                for (int i = 1; i <= blocks; i++) {
                    if (head[1] - i >= 0) {
                        grid[head[0]][head[1] - i] = snakeNum;
                    }

                }
            } else if (snakeDirection(snake) == "d") {
                for (int i = 1; i <= blocks; i++) {
                    if (head[1] + i <= 49) {
                        grid[head[0]][head[1] + i] = snakeNum;
                    }

                }
            }
        }

    }

    public static void print2DArray(int[][] array) {

        // for (int i = 0; i < array.length; i++)
        // {
        // String outMessage = "";
        // for (int j = 0; j < array[i].length; j++)
        // {
        // outMessage += array[i][j];
        // }
        // // Move to the next row
        //
        // System.out.println("log " + outMessage);
        // }

        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    writer.print(array[j][i]);
                }
                writer.println(); // Move to the next line for the next row
            }
            System.out.println("Array written to output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

// end
