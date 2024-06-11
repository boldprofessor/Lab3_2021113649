import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.util.HashMap;



public class Lab1Test {
  public Lab1 lab1;

  @BeforeEach
  void setup() throws FileNotFoundException {
    lab1 = new Lab1(); // 初始化lab1对象
    String filePath = "src\\1.txt";
    lab1.buildGraphFromFile(filePath);
  }

  @Test
  void testgenerateNewText(){
    String inputText = "in heart";
    String expectedOutput = "in the heart";
    assert lab1 != null;
    String actualOutput = lab1.generateNewText(inputText);
    assertEquals(expectedOutput, actualOutput, "The generated text should include the bridge word");
  }

  @Test
  void testgenerateNewText2(){
    String inputText = "in   heart";
    String expectedOutput = "in   heart";
    assert lab1 != null;
    String actualOutput = lab1.generateNewText(inputText);
    assertEquals(expectedOutput, actualOutput, "单词间只能有一个空格");
  }

  @Test
  void testgenerateNewText3(){
    String inputText = "  ";
    String expectedOutput = "";
    assert lab1 != null;
    String actualOutput = lab1.generateNewText(inputText);
    assertEquals(expectedOutput, actualOutput, "The generated text should include words");
  }

  @Test
  void testgenerateNewText4(){
    String inputText = "";
    String expectedOutput = "";
    assert lab1 != null;
    String actualOutput = lab1.generateNewText(inputText);
    assertEquals(expectedOutput, actualOutput, "The generated text should include words");
  }


}