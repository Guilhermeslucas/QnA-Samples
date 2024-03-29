/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Seeder;


import Seeder.Responses.AnswerBody;
import Seeder.Utils.SheetReader;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testCreateKb() {
        KnowledgeBase kb = new KnowledgeBase("qnaconfig");
        try {
            kb.addQnaPairs("test.xlsx");
        } catch (Exception e) {
            fail("Not able to execute the test");
        }
    }

    @Test public void testSheetToList() {
        try {
            SheetReader.parseFileToList("test.xlsx");
        } catch (Exception e) {
            fail("Failed on return a list from sheet");
        }
    }

    @Test public  void testPublishKb() {
        KnowledgeBase kb = new KnowledgeBase("qnaconfig");
        try {
            kb.publish();
        } catch (Exception e) {
            fail("Not able to execute the test");
        }
    }

    @Test public void testGetAnswerKb() {
        KnowledgeBase kb = new KnowledgeBase("qnaconfig");
        try {
            AnswerBody[] answers = kb.getAnswer("whats your age", 1, 20);
            if (answers.length == 0) {
                fail("Not able to execute the test");
            }
        } catch (Exception e) {
            fail("Not able to execute the test");
        }
    }

    @Test public void testGetQuestion() {
        Pair p = new Pair("question", "answer");
        Assert.assertEquals("question", p.getQuestion());
    }

    @Test public void testGetAnswer() {
        Pair p = new Pair("question", "answer");
        Assert.assertEquals("answer", p.getAnswer());
    }
}
