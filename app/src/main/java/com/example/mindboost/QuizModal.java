package com.example.mindboost;

public class QuizModal {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int option1Points;
    private int option2Points;
    private int option3Points;
    private int option4Points;

    public QuizModal(String question, String option1, String option2, String option3, String option4, int option1Points, int option2Points, int option3Points, int option4Points) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.option1Points = option1Points;
        this.option2Points = option2Points;
        this.option3Points = option3Points;
        this.option4Points = option4Points;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getOption1Points() {
        return option1Points;
    }

    public void setOption1Points(int option1Points) {
        this.option1Points = option1Points;
    }

    public int getOption2Points() {
        return option2Points;
    }

    public void setOption2Points(int option2Points) {
        this.option2Points = option2Points;
    }

    public int getOption3Points() {
        return option3Points;
    }

    public void setOption3Points(int option3Points) {
        this.option3Points = option3Points;
    }

    public int getOption4Points() {
        return option4Points;
    }

    public void setOption4Points(int option4Points) {
        this.option4Points = option4Points;
    }
}
