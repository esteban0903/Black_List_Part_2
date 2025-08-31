package co.eci.blacklist.labs.part1;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CountThreadTest {
    @Test
    void testCountThreadPrintsExpectedRange() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        int from = 1;
        int to = 3;
        CountThread thread = new CountThread(from, to);
        thread.run(); 

        System.setOut(originalOut); 

        String[] lines = outContent.toString().split("\\r?\\n");
        assertEquals(to - from + 1, lines.length);
        for (int i = from; i <= to; i++) {
            assertTrue(lines[i - from].contains("[CountThread-" + from + "-" + to + "] " + i));
        }
    }
}
