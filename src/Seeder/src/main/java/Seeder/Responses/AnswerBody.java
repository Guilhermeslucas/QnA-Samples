package Seeder.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerBody {
    public double score;
    public int id;
    public String answer;
    public String source;
    public ArrayList<String> questions;
    public ArrayList<Object> metadata;

    @Override
    public String toString() {
        return String.format("answer: %s, score: %.2f", answer, score);
    }
}
