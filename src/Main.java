import java.util.*;

public class Main {
    public static void main(String[] args) {
        int N, M;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Input:");
            String[] rowsAndColumns = sc.nextLine().split(" ");
            M = Integer.parseInt(rowsAndColumns[0]);
            N = Integer.parseInt(rowsAndColumns[1]);
        } while (M > 100 || N > 100);
        
        int[][] matrix = new int[M][N];
        int countRows = 0, countColumns = 0;

        System.out.println("Enter first layer of bricks: ");
        try {
            for(int j = 0; j < M; j++) {
                String[] row = sc.nextLine().split(" ");
                countRows++;
                for (int i = 0; i < N; i++) {
                    countColumns++;
                    matrix[j][i] = Integer.parseInt(row[i]);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong input based on number of rows and columns");
            System.exit(-1);
        }

        if (!validateSpanningBricks(matrix, N, M)) {
            System.out.println("There are bricks spanning 3 rows/columns");
            System.exit(-1);
        }

        if(!buildBricks(N, M, matrix)) {
            System.exit(-1);
            System.out.println("No solution exists");
        }
        System.out.println();
        print(matrix, N, M);

    }

    //Method that prints the matrix
    public static void print(int[][] arr, int N, int M) {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    //Validate if there are bricks spanning 3 rows/columns
    public static boolean validateSpanningBricks(int[][] arr, int N, int M) {
        //Iterate through the whole matrix and check if on any side there are 2 consecutive numbers same as the current
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if(i - 1 > 0  && arr[i][j] == arr[i-1][j] && arr[i-1][j] == arr[i-2][j])
                    return false;
                if(i + 1 < M - 1 && arr[i][j] == arr[i+1][j] && arr[i+1][j] == arr[i+2][j])
                    return false;
                if(j - 1 > 0 && arr[i][j] == arr[i][j-1] && arr[i][j-1] == arr[i][j-2])
                    return false;
                if(j + 1 < N - 1 && arr[i][j] == arr[i][j+1] && arr[i][j+1] == arr[i][j+2])
                    return false;
            }
        }
        return true;
    }

    //matrix rotation method
    //It takes as parameter a contour and a matrix. Contour should always end with the same element it starts
    //It is rotating the matrix by one position on the left
    public static void rotate(List<AbstractMap.SimpleEntry<Integer, Integer>> coordinates, int[][] arr){
        int previous = arr[coordinates.get(0).getKey()][coordinates.get(0).getValue()];
        for(int i = 1; i < coordinates.size(); i++){
            int temp = arr[coordinates.get(i).getKey()][coordinates.get(i).getValue()];
            arr[coordinates.get(i).getKey()][coordinates.get(i).getValue()] = previous;
            previous = temp;
        }
    }

    //method of building a second layer of bricks
    public static boolean buildBricks(int N, int M, int[][] fLayer) {
        //Map containing coordinates for a key and a value - whether it is visited or not
        Map<AbstractMap.SimpleEntry<Integer, Integer>, Boolean> visited = new HashMap<>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                visited.put(new AbstractMap.SimpleEntry<>(i, j), false);
            }
        }

        //Indexes to work with
        int i = 0, j = 0;
        //a map that represents traversed coordinates that contain multiple choices.
        //There are coordinates for a key and a list of selected routes for a value.
        //Implementing Multimap with Lists as values
        //LinkedHashMap because last element is necessary for further purposes
        Map<AbstractMap.SimpleEntry<Integer, Integer>, List<AbstractMap.SimpleEntry<Integer, Integer>>> multipleRouteTaken = new LinkedHashMap<>();
        //While loop which ends when there are no unvisited left
        while(visited.containsValue(false)) {
            //Counter for how many unvisited elements are left
            int countVisited = 0;
            //Loop through visited map and get unvisited elements
            for (Map.Entry<AbstractMap.SimpleEntry<Integer, Integer>, Boolean> e : visited.entrySet()) {
                if (!e.getValue())
                    countVisited++;
            }
            //If there are less than 4 unvisited elements, it means there is no solution
            if (countVisited < 4)
                return false;
            //List of coordinates which represents a contour that fills iteratively.
            List<AbstractMap.SimpleEntry<Integer, Integer>> contour = new ArrayList<>();
            //A starting point for a contour
            AbstractMap.SimpleEntry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> firstElem = null;
            //Last direction that we went is kept
            String direction = "";
            //Label
            OuterLoop:
            for (int k = 0; k < M; k++) {
                for (int p = 0; p < N; p++) {
                    //The first element from the matrix that is not visited
                    if (!visited.get(new AbstractMap.SimpleEntry<>(k, p))) {
                        firstElem = new AbstractMap.SimpleEntry<>(fLayer[k][p], new AbstractMap.SimpleEntry<>(k, p));
                        break OuterLoop;
                    }
                }
            }
            //Assign initial coordinates
            i = firstElem.getValue().getKey();
            j = firstElem.getValue().getValue();
            //An endless cycle that fills the contour
            while(true) {
                //Add the current element to the contour
                contour.add(new AbstractMap.SimpleEntry<>(i, j));
                //Make the current element visited
                visited.replace(new AbstractMap.SimpleEntry<>(i, j), true);
                //If the current element is different than the first element
                if (fLayer[i][j] != firstElem.getKey()) {
                    //4 conditions that check for the four sides that can be visited, whether any of them leads to the first element
                    //If one of these conditions is met, then we have reached the end of the contour and we break from the loop
                    if (i - 1 >= 0 && i - 1 == firstElem.getValue().getKey() && j == firstElem.getValue().getValue()) {
                        contour.add(new AbstractMap.SimpleEntry<>(i-1, j));
                        break;
                    }
                    if (i + 1 < M - 1 && i + 1 == firstElem.getValue().getKey() && j == firstElem.getValue().getValue()) {
                        contour.add(new AbstractMap.SimpleEntry<>(i+1, j));
                        break;
                    }
                    if (j - 1 >= 0 && i == firstElem.getValue().getKey() && j - 1 == firstElem.getValue().getValue()) {
                        contour.add(new AbstractMap.SimpleEntry<>(i, j-1));
                        break;
                    }
                    if (j + 1 < N - 1 && i == firstElem.getValue().getKey() && j + 1 == firstElem.getValue().getValue()) {
                        contour.add(new AbstractMap.SimpleEntry<>(i, j+1));
                        break;
                    }
                }

                //We check if on any of the 4 sides there is an element with the same value as the current one
                //We are looking for the second half of the brick
                if (i - 1 >= 0 && fLayer[i-1][j] == fLayer[i][j] && !visited.get(new AbstractMap.SimpleEntry<>(i-1, j))) {
                    i -= 1;
                    direction = "up";
                    continue;
                }
                if (i + 1 < M && fLayer[i+1][j] == fLayer[i][j] && !visited.get(new AbstractMap.SimpleEntry<>(i+1, j))) {
                    i += 1;
                    direction = "down";
                    continue;
                }
                if (j - 1 >= 0 && fLayer[i][j-1] == fLayer[i][j] && !visited.get(new AbstractMap.SimpleEntry<>(i, j-1))) {
                    j -= 1;
                    direction = "left";
                    continue;
                }
                if (j + 1 < N && fLayer[i][j+1] == fLayer[i][j] && !visited.get(new AbstractMap.SimpleEntry<>(i, j+1))) {
                    j += 1;
                    direction = "right";
                    continue;
                }

                //A map that contains the possible paths
                Map<Integer, AbstractMap.SimpleEntry<Integer, Integer>> routes = new HashMap<>();
                //First, we find the sides of which there are 2 consecutive identical numbers (one whole brick)
                if(i - 1 > 0 && !visited.get(new AbstractMap.SimpleEntry<>(i-1, j)) && fLayer[i-1][j] == fLayer[i-2][j])
                    routes.put(fLayer[i-1][j], new AbstractMap.SimpleEntry<>(i-1, j));
                if(i + 1 < M - 1 && !visited.get(new AbstractMap.SimpleEntry<>(i+1, j)) && fLayer[i+1][j] == fLayer[i+2][j])
                    routes.put(fLayer[i+1][j], new AbstractMap.SimpleEntry<>(i+1,j));
                if(j - 1 > 0 && !visited.get(new AbstractMap.SimpleEntry<>(i, j-1)) && fLayer[i][j-1] == fLayer[i][j-2])
                    routes.put(fLayer[i][j-1], new AbstractMap.SimpleEntry<>(i, j-1));
                if(j + 1 < N - 1 && !visited.get(new AbstractMap.SimpleEntry<>(i, j+1)) && fLayer[i][j+1] == fLayer[i][j+2])
                    routes.put(fLayer[i][j+1], new AbstractMap.SimpleEntry<>(i, j+1));

                //If there are no roads with 2 consecutive identical numbers, we check all possible paths
                if (routes.isEmpty()) {
                    if(i - 1 >=  0 && !visited.get(new AbstractMap.SimpleEntry<>(i-1, j)))
                        routes.put(fLayer[i-1][j], new AbstractMap.SimpleEntry<>(i-1, j));
                    if(i + 1 < M && !visited.get(new AbstractMap.SimpleEntry<>(i+1, j)))
                        routes.put(fLayer[i+1][j], new AbstractMap.SimpleEntry<>(i+1,j));
                    if(j - 1 >= 0 && !visited.get(new AbstractMap.SimpleEntry<>(i, j-1)))
                        routes.put(fLayer[i][j-1], new AbstractMap.SimpleEntry<>(i, j-1));
                    if(j + 1 < N && !visited.get(new AbstractMap.SimpleEntry<>(i, j+1)))
                        routes.put(fLayer[i][j+1], new AbstractMap.SimpleEntry<>(i, j+1));
                }

                //If there is no way to go, it means that we have reached a dead end
                //and we have to go back to the last coordinates, where we have multiple choice
                if (routes.isEmpty()) {
                    Iterator<Map.Entry<AbstractMap.SimpleEntry<Integer, Integer>, List<AbstractMap.SimpleEntry<Integer, Integer>>>> it = multipleRouteTaken.entrySet().iterator();
                    //We find the last element
                    AbstractMap.SimpleEntry<AbstractMap.SimpleEntry<Integer, Integer>, List<AbstractMap.SimpleEntry<Integer, Integer>>> lastElement = null;
                    while (it.hasNext()) {
                        lastElement = new AbstractMap.SimpleEntry<>(it.next());
                    }

                    //We go back step by step until we reach the element from above.
                    //We remove the wrong path from the contour and change the coordinates to unvisited
                    for (int x = contour.size() - 1; x >= 0; x--) {
                        if (!(contour.get(x).getKey().equals(lastElement.getKey().getKey()) && contour.get(x).getValue().equals(lastElement.getKey().getValue()))) {
                            visited.replace(contour.get(x), false);
                            contour.remove(x);
                        }
                        else break;
                    }
                    //Also, we remove one more element from the contour, because at the start of the loop, we will add it again
                    contour.remove(lastElement.getKey());

                    //Current element is equal to the last with multiple choice
                    i = lastElement.getKey().getKey();
                    j = lastElement.getKey().getValue();
                    continue;
                }

                //We check if we have gone through the current element with multiple choice
                if (multipleRouteTaken.containsKey(new AbstractMap.SimpleEntry<>(i, j))) {
                    //List that represents all the paths we have gone to from these coordinates
                    List<AbstractMap.SimpleEntry<Integer, Integer>> takenRoutesList;
                    takenRoutesList = multipleRouteTaken.get(new AbstractMap.SimpleEntry<>(i, j));
                    //We remove the paths we took before from the list of possible paths for the current item
                    for (AbstractMap.SimpleEntry<Integer, Integer> e : takenRoutesList) {
                        Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>>> it;
                        for (it = routes.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> key = it.next();
                            if (key.getValue().getKey().equals(e.getKey()) && key.getValue().getValue().equals(e.getValue()))
                                it.remove();
                        }
                    }
                }

                //If there is only one possible way, we go there
                if (routes.size() == 1) {
                    Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> entry = routes.entrySet().iterator().next();
                    i = entry.getValue().getKey();
                    j = entry.getValue().getValue();
                    continue;
                }

                //If there is more than one possible path, we check in which direction we moved last and
                // whether there is a path in the same direction. If not - we take the first possible path
                if (routes.size() > 1) {
                    //First possible path
                    Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> entry = routes.entrySet().iterator().next();
                    if (direction.equals("up")) {
                        for (Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>>> it = routes.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> temp = it.next();
                            if (temp.getValue().getKey().equals(i - 1) && temp.getValue().getValue().equals(j))
                                entry = temp;
                        }
                    }
                    if (direction.equals("down")) {
                        for (Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>>> it = routes.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> temp = it.next();
                            if (temp.getValue().getKey().equals(i + 1) && temp.getValue().getValue().equals(j))
                                entry = temp;
                        }
                    }
                    if (direction.equals("left")) {
                        for (Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>>> it = routes.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> temp = it.next();
                            if (temp.getValue().getKey().equals(i) && temp.getValue().getValue().equals(j - 1))
                                entry = temp;
                        }
                    }
                    if (direction.equals("right")) {
                        for (Iterator<Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>>> it = routes.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Integer>> temp = it.next();
                            if (temp.getValue().getKey().equals(i) && temp.getValue().getValue().equals(j + 1))
                                entry = temp;
                        }
                    }

                    //If the current element is not in the list of already visited paths, we save it there along with the path we will take
                    if (!multipleRouteTaken.containsKey(new AbstractMap.SimpleEntry<>(i, j))) {
                        multipleRouteTaken.put(new AbstractMap.SimpleEntry<>(i, j), new ArrayList<>(Arrays.asList(new AbstractMap.SimpleEntry<>(entry.getValue().getKey(), entry.getValue().getValue()))));
                    }//Else, we only update the list of previously taken paths from this element. We add the path we will take
                    else {
                        List<AbstractMap.SimpleEntry<Integer, Integer>> oldTakenRoutesList = multipleRouteTaken.get(new AbstractMap.SimpleEntry<>(i, j));
                        oldTakenRoutesList.add(new AbstractMap.SimpleEntry<>(entry.getValue().getKey(), entry.getValue().getValue()));
                        multipleRouteTaken.replace(new AbstractMap.SimpleEntry<>(i, j), oldTakenRoutesList);
                    }
                    i = entry.getValue().getKey();
                    j = entry.getValue().getValue();
                }
            }
            //When we complete a contour, we rotate this contour in the matrix
            rotate(contour, fLayer);
        }
        return true;
    }
}
