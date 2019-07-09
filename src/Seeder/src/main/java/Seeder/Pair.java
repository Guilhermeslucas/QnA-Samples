package Seeder;

public class Pair {
    private String _question;
    private String _answer;

    public Pair(String question, String answer) {
        _question = question;
        _answer = answer;
    }

    public String getQuestion() {
        return this._question;
    }

    public String getAnswer() {
        return this._answer;
    }

    @Override
    public String toString() {
        return String.format("Question: %s Answer: %s", this._question, this._answer);
    }
}