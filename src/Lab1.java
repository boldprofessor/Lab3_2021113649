import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Lab1 类是一个表示图的容器类.
 * 它使用嵌套Map结构来存储图的信息，包括节点间的关系和节点值。
 */
public class Lab1 {
  private final Map<String, Map<String, Integer>> graph;
  private final Map<String, Integer> node;

  public Lab1() {
    this.graph = new HashMap<>();
    this.node = new HashMap<>();
  }

  /**
     *  buildGraphFromFile.
   */
  public void buildGraphFromFile(String filePath) throws FileNotFoundException {

    List<String> words = new ArrayList<>();
    Scanner scanner = new Scanner(new File(filePath));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().toLowerCase();
      String[] wordsArray = line.split("[^a-zA-Z]+");
      Collections.addAll(words, wordsArray);
    }

    for (String s : words) {
      if (!node.containsKey(s)) {
        node.put(s, 1);
      }
    }

    for (int i = 1; i < words.size(); i++) {
      String word1 = words.get(i - 1);
      String word2 = words.get(i);

      // Retrieve or create the map for word1
      Map<String, Integer> tmp = graph.computeIfAbsent(word1, k -> new HashMap<>());

      // Update the count for the pair (word1, word2)
      tmp.put(word2, tmp.getOrDefault(word2, 0) + 1);
    }

    for (String word : node.keySet()) {
      if (!graph.containsKey(word)) {
        graph.put(word, new HashMap<>());
      }
    }
    scanner.close();
  }

  private static void printMenu() {
    System.out.println("Please input choice");
    System.out.println("1. show the directed graph");
    System.out.println("2. query bridge words");
    System.out.println("3. generate new text");
    System.out.println("4. calculate shortest path");
    System.out.println("5. random walk");
    System.out.println("0. exit");
  }

  /**
    * main.
  **/
  public static void main(String[] args) {
    Lab1 lab1 = new Lab1();
    Scanner scanner = new Scanner(System.in);
    System.out.println("Please input file path: ");
    String filePath = "src\\1.txt";
            //scanner.nextLine();
    try {
      lab1.buildGraphFromFile(filePath);
    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + filePath);
    }
    while (true) {
      printMenu();
      int choice = scanner.nextInt();
      scanner.nextLine();

      switch (choice) {
        case 1:
          lab1.showDirectedGraph(new HashMap<>());
          break;
        case 2:
          System.out.println("Please input two words");
          String word1 = scanner.next();
          String word2 = scanner.next();
          lab1.queryBridgeWords(word1, word2);
          break;
        case 3:
          System.out.println("Please input a line of text");
          String inputText = scanner.nextLine();
          String newText = lab1.generateNewText(inputText);
          System.out.println(newText);
          break;
        case 4:
          System.out.println("Please input source and destination word");
          String startWord = scanner.next();
          String endWord = scanner.next();
          lab1.calcShortestPath(startWord, endWord);
          break;
        case 5:
          lab1.randomWalk();
          break;
        case 0:
          return;
        default:
          System.out.println("Invalid!");
      }
    }
  }

  /**
     * showDirectedGraph.
   */
  public void showDirectedGraph(Map<String, Map<String, Integer>> highlight) {
    StringBuilder newText = new StringBuilder();
    newText.append("digraph G {\n");
    for (String source : graph.keySet()) {
      Map<String, Integer> edges = graph.get(source);
      for (String target : edges.keySet()) {
        int weight = edges.get(target);
        String color = "black";
        if (highlight.containsKey(source) && highlight.get(source).containsKey(target)) {
          color = "red";
        }
        newText.append(String.format(
                "  \"%s\" -> \"%s\" [label=\"%d\", color=\"%s\"]\n",
                source,
                target,
                weight,
                color));
      }
    }

    newText.append("}\n");

    try {
      String filePath = "output";
      BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
      writer.write(newText.toString());
      writer.close();
      System.out.println("Write to " + filePath + " success.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      Process process = Runtime.getRuntime().exec("cmd /c dot ./output -Tsvg > a.svg");
      int exitCode = process.waitFor();
      System.out.println("Generate exit code: " + exitCode);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void queryBridgeWords(String word1, String word2) {
    internal_queryBridgeWords(word1, word2, false);
  }

  private String internal_queryBridgeWords(String word1, String word2, Boolean quiet) {
    if (!node.containsKey(word1) || !node.containsKey(word2)) {
      if (!quiet) {
        System.out.println("No word1 or word2 in the graph!");
      }
      return null;
    }
    List<String> ans = new ArrayList<>();
    for (String word : node.keySet()) {
      if (graph.get(word1).containsKey(word)
              && graph.containsKey(word)
              && graph.get(word).containsKey(word2)) {
        ans.add(word);
      }
    }
    if (ans.size() == 0) {
      if (!quiet) {
        System.out.println("No bridge words from word1 to word2!");
      }
      return null;
    } else {
      StringBuilder newText = new StringBuilder();
      newText.append("The bridge words from word1 to word2 are: ");

      newText.append(String.join(", ", ans.subList(0, ans.size() - 1)))
              .append(ans.size() > 1 ? " and " : "").append(ans.get(ans.size() - 1)).append(".");
      if (!quiet) {
        System.out.println(newText.toString());
      }
      return ans.get(0);
    }
  }

  /**
     * generateNewText.
     */
  public String generateNewText(String inputText) {
    String[] words = inputText.split(" ");
    StringBuilder newText = new StringBuilder();

    for (int i = 0; i < words.length - 1; i++) {

      String currentWord = words[i];
      String nextWord = words[i + 1];
      newText.append(currentWord).append(" ");
      String bridgeWord = internal_queryBridgeWords(currentWord, nextWord, true);
      if (bridgeWord != null && !bridgeWord.isEmpty()) {
        newText.append(bridgeWord).append(" ");
      }
    }
    if (words.length == 0) {
      return newText.toString();
    }
    newText.append(words[words.length - 1]);
    return newText.toString();
  }

  /**
     * calcShortestPath.
     */
  public void calcShortestPath(String word1, String word2) {
    Map<String, Integer> distances = new HashMap<>();
    Map<String, String> predecessors = new HashMap<>();
    Set<String> visited = new HashSet<>();
    PriorityQueue<String> priorityQueue;
    priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

    for (String word : node.keySet()) {
      distances.put(word, Integer.MAX_VALUE);
    }
    distances.put(word1, 0);
    priorityQueue.add(word1);

    while (!priorityQueue.isEmpty()) {
      String current = priorityQueue.poll();
      if (!visited.add(current)) {
        continue;
      }

      if (current.equals(word2)) {
        break;
      }

      for (Map.Entry<String, Integer> neighborEntry : graph.get(current).entrySet()) {
        String neighbor = neighborEntry.getKey();
        int weight = neighborEntry.getValue();
        int newDist = distances.get(current) + weight;

        if (newDist < distances.get(neighbor)) {
          distances.put(neighbor, newDist);
          predecessors.put(neighbor, current);
          priorityQueue.add(neighbor);
        }
      }
    }

    if (distances.get(word2) == Integer.MAX_VALUE) {
      System.out.println("Word2 unreachable from word1!");
    } else {
      System.out.println("Shortest Path: " + distances.get(word2));
      Map<String, Map<String, Integer>> highlight = new HashMap<>();
      String current = word2;
      while (predecessors.containsKey(current)) {
        String predecessor = predecessors.get(current);
        highlight.computeIfAbsent(predecessor, k -> new HashMap<>()).put(current, 1);
        current = predecessor;
      }
      showDirectedGraph(highlight);
    }
  }


  private static Map<String, Map<String, Integer>> deepCopyGraph(
          Map<String, Map<String, Integer>> original) {
    return original.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new HashMap<>(entry.getValue())
            ));
  }

  /**
     * 随机游走.
     */
  public void randomWalk() {
    Map<String, Map<String, Integer>> g = deepCopyGraph(graph);
    Random random = new Random();
    List<String> tmp = new ArrayList<>(node.keySet());
    String now = tmp.get(random.nextInt(tmp.size()));
    AtomicBoolean running = new AtomicBoolean(true);
    Scanner scanner = new Scanner(System.in);


    Thread inputThread = new Thread(() -> {
      System.out.println("Enter 's' to stop:");
      while (true) {
        String command = scanner.nextLine().trim().toLowerCase();
        if (command.isEmpty()) {
          break;
        }
        if ("s".equals(command)) {
          running.set(false);
          break;
        }
      }
    });
    inputThread.start();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    while (running.get()) {
      System.out.print(now + " ");
      if (g.get(now).isEmpty()) {
        System.out.println();
        System.out.println("Please input ENTER to break");
        break;
      }
      List<String> cad = new ArrayList<>(g.get(now).keySet());
      String next = cad.get(random.nextInt(cad.size()));
      g.get(now).remove(next);
      now = next;

      for (int i = 0; i < 10; i++) {
        if (!running.get()) {
          break;
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
    try {
      inputThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    System.out.println();
  }
}
