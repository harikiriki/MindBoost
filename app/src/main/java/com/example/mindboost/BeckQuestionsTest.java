package com.example.mindboost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Random;

public class BeckQuestionsTest extends AppCompatActivity {

    private TextView questionNumber, question;
    private Button btn1, btn2, btn3, btn4;
    Random random;
    private ArrayList<QuizModal> quizModalArrayList;
    int currentScore = 0, questionAttempted = 1, currentPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beck_questions_test);

        questionNumber = findViewById(R.id.questionNumber);
        question = findViewById(R.id.questionContent);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        quizModalArrayList = new ArrayList<>();
        random = new Random();
        
        getQuizQuestions(quizModalArrayList);
        currentPos = 0;
        setDataToViews(currentPos);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScore += 0;
                questionAttempted++;
                currentPos++;
                setDataToViews(currentPos);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScore += 1;
                questionAttempted++;
                currentPos++;
                setDataToViews(currentPos);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScore += 2;
                questionAttempted++;
                currentPos++;
                setDataToViews(currentPos);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScore += 3;
                questionAttempted++;
                currentPos++;
                setDataToViews(currentPos);
            }
        });
    }

    private void setDataToViews(int currentPos) {
        questionNumber.setText("Pytanie " + String.valueOf(questionAttempted));
        if(currentPos >= quizModalArrayList.size()) {
            showResultScreen();
            return;
        }
        questionNumber.setText(String.valueOf(currentPos + 1)); // Aktualizacja numeru pytania
        question.setText(quizModalArrayList.get(currentPos).getQuestion());
        btn1.setText(quizModalArrayList.get(currentPos).getOption1());
        btn2.setText(quizModalArrayList.get(currentPos).getOption2());
        btn3.setText(quizModalArrayList.get(currentPos).getOption3());
        btn4.setText(quizModalArrayList.get(currentPos).getOption4());
    }

    private void showResultScreen() {
        Intent intent = new Intent(BeckQuestionsTest.this, TestResultActivity.class);
        intent.putExtra("SCORE", currentScore);
        startActivity(intent);
        finish(); // Zakończ obecną aktywność
    }

    private void getQuizQuestions(ArrayList<QuizModal> quizModalArrayList) {
// Pytanie 1
        quizModalArrayList.add(new QuizModal(
                "Odczuwanie smutku i przygnębienia",
                "Nie jestem smutny ani przygnębiony",
                "Często odczuwam smutek i przygnębienie",
                "Przeżywam stale smutek, nie mogę się uwolnić od niego",
                "Jestem stale smutny i nieszczęśliwy, jest to nie do wytrzymania",
                0, 1, 2, 3
        ));

// Pytanie 2
        quizModalArrayList.add(new QuizModal(
                "Martwienie się o przyszłość",
                "Nie przejmuję się zbytnio swoją przyszłością",
                "Często martwię się o swoją przyszłość",
                "Obawiam się, że w przyszłości nic dobrego mnie nie czeka",
                "Czuję, że przyszłość jest beznadziejna i nic tego nie zmieni",
                0, 1, 2, 3
        ));

// Pytanie 3
        quizModalArrayList.add(new QuizModal(
                "Uważasz, że zaniedbujesz swoje obowiązki?",
                "Sądzę, że nie popełniam większych zaniedbań",
                "Sądzę, że czynię więcej zaniedbań niż inni",
                "Kiedy zastanawiam się nad sobą, widzę mnóstwo błędów i zaniedbań",
                "Jestem zupełnie niewydolny i wszystko robię źle.",
                0, 1, 2, 3
        ));

// Pytanie 4
        quizModalArrayList.add(new QuizModal(
                "Jesteś zadowolony z siebie?",
                "To co robię sprawia mi przyjemność",
                "Nie cieszy mnie to co robię",
                "Nic mi teraz nie daje prawdziwego zadowolenia",
                "Nie potrafię przeżywać zadowolenia i przyjemności. Wszystko mnie nuży",
                0, 1, 2, 3
        ));

// Pytanie 5
        quizModalArrayList.add(new QuizModal(
                "Czy często masz poczucie winy?",
                "Nie czuję się winnym ani wobec siebie, ani wobec innych",
                "Dosyć często mam wyrzuty sumienia",
                "Bardzo często czuję, że zawiniłem",
                "Stale mam poczucie winy",
                0, 1, 2, 3
        ));

// Pytanie 6
        quizModalArrayList.add(new QuizModal(
                "Czy zasługujesz na karę?",
                "Sądzę, że nie zasługuję na karę",
                "Sądzę, że zasługuję na karę",
                "Spodziewam się ukarania",
                "Wiem, że jestem karany",
                0, 1, 2, 3
        ));

// Pytanie 7
        quizModalArrayList.add(new QuizModal(
                "Zadowolenie z siebie",
                "Jestem z siebie zadowolony",
                "Nie jestem z siebie zadowolony",
                "Czuję do siebie niechęć",
                "Nienawidzę siebie",
                0, 1, 2, 3
        ));

// Pytanie 8
        quizModalArrayList.add(new QuizModal(
                "Czy czujesz się gorszy od innych?",
                "Nie czuję się gorszy od innych ludzi",
                "Zarzucam sobie, że jestem nieudolny i popełniam błędy",
                "Stale potępiam siebie za popełnione błędy",
                "Winię siebie za wszystko zło, które istnieje",
                0, 1, 2, 3
        ));

// Pytanie 9
        quizModalArrayList.add(new QuizModal(
                "Czy masz myśli samobójcze?",
                "Nie myślę o odebraniu sobie życia",
                "Myślę o samobójstwie - ale nie mógłbym tego dokonać",
                "Pragnę odebrać sobie życie",
                "Popełnię samobójstwo, jak będzie odpowiednia sposobność",
                0, 1, 2, 3
        ));

// Pytanie 10
        quizModalArrayList.add(new QuizModal(
                "Często chce Ci się płakać?",
                "Nie płaczę częściej niż zwykle",
                "Płaczę częściej niż dawniej",
                "Ciągle chce mi się płakać",
                "Chciałbym płakać, lecz nie jestem w stanie",
                0, 1, 2, 3
        ));

// Pytanie 11
        quizModalArrayList.add(new QuizModal(
                "Jesteś ostatnio bardziej nerwowy i rozdrażniony?",
                "Nie jestem bardziej podenerwowany niż dawniej",
                "Jestem bardziej nerwowy i przykry niż dawniej",
                "Stale jestem zdenerwowany lub rozdrażniony",
                "Wszystko co dawniej mnie drażniło, stało się obojętne",
                0, 1, 2, 3
        ));

// Pytanie 12
        quizModalArrayList.add(new QuizModal(
                "Czy zmieniło się coś w Twoim zainteresowaniu innymi ludźmi?",
                "Ludzie interesują mnie jak dawniej",
                "Interesuję się ludźmi mniej niż dawniej",
                "Utraciłem większość zainteresowań innymi ludźmi",
                "Utraciłem wszelkie zainteresowania innymi ludźmi",
                0, 1, 2, 3
        ));

// Pytanie 13
        quizModalArrayList.add(new QuizModal(
                "Czy ostatnio bardziej masz problemy z podejmowaniem różnych decyzji?",
                "Decyzję podejmuję łatwo, tak jak dawniej",
                "Częściej niż kiedyś odwlekam podjęcie decyzji",
                "Mam dużo trudności z podjęciem decyzji",
                "Nie jestem w stanie podjąć żadnej decyzji",
                0, 1, 2, 3
        ));

// Pytanie 14
        quizModalArrayList.add(new QuizModal(
                "Uważasz, że wyglądasz gorzej i mniej atrakcyjnie niż kiedyś?",
                "Sądzę, że wyglądam nie gorzej niż dawniej",
                "Martwię się tym, że wyglądam staro i nie atrakcyjnie",
                "Czuję, że wyglądam coraz gorzej",
                "Jestem przekonany, że wyglądam okropnie i odpychająco",
                0, 1, 2, 3
        ));

// Pytanie 15
        quizModalArrayList.add(new QuizModal(
                "Czy masz większe trudności z wykonywaniem różnych prac i zadań?",
                "Mogę pracować jak dawniej",
                "Z trudem rozpoczynam każdą czynność",
                "Z wielkim wysiłkiem zmujszam się do zrobienia czegokolwiek",
                "Nie jestem w stanie nic robić",
                0, 1, 2, 3
        ));

// Pytanie 16
        quizModalArrayList.add(new QuizModal(
                "Masz kłopoty ze snem?",
                "Sypiam dobrze, jak zwykle",
                "Sypiam gorzej niż dawniej",
                "Rano budzę się 1-2 godziny za wcześnie i trudno jest mi ponownie zasnąć",
                "Budzę się kilka godzin za wcześnie i nie mogę zasnąć ponownie",
                0, 1, 2, 3
        ));

// Pytanie 17
        quizModalArrayList.add(new QuizModal(
                "Czy męczysz się bardziej, niż zwykle?",
                "Nie męczę się bardziej niż dawniej",
                "Męczę się znacznie łatwiej niż kiedyś",
                "Męczę się wszystkim, co robię",
                "Jestem zbyt zmęczony, aby cokolwiek robić",
                0, 1, 2, 3
        ));

// Pytanie 18
        quizModalArrayList.add(new QuizModal(
                "Czy masz kłopoty z apetytem?",
                "Mam apetyt nie gorszy niż dawniej",
                "Mam trochę gorszy apetyt",
                "Apetyt mam wyraźnie gorszy",
                "Nie mam w ogóle apetytu",
                0, 1, 2, 3
        ));

// Pytanie 19
        quizModalArrayList.add(new QuizModal(
                "W ciągu ostatniego miesiąca nie stosowałem diety, aby schudnąć, lecz straciłem na wadze:",
                "Nie tracę na wadze ciała (w okresie ostatniego miesiąca)",
                "Straciłem na wadze więcej niż 2 kg",
                "Straciłem na wadze więcej niż 4 kg",
                "Straciłem na wadze więcej niż 6 kg",
                0, 1, 2, 3
        ));

// Pytanie 20
        quizModalArrayList.add(new QuizModal(
                "Czy ostatnio bardziej martwisz się swoim stanem zdrowia?",
                "Nie martwię się o swoje zdrowie bardziej niż zawsze",
                "Martwię się swoimi dolegliwościami, mam rozstrój żołądka, zaparcie, bóle",
                "Stan mego zdrowia bardzo mnie martwi, często o tym myślę",
                "Tak bardzo martwię się o swoje zdrowie, że nie mogę o niczym innym myśleć",
                0, 1, 2, 3
        ));

// Pytanie 21
        quizModalArrayList.add(new QuizModal(
                "Czy masz kłopoty z potencją?",
                "Moje zainteresowania seksualne nie uległy zmianom",
                "Jestem mniej zainteresowany sprawami płci (seksu)",
                "Seks wyraźnie mniej mnie interesuje",
                "Zupełnie straciłem zainteresowanie sprawami seksu",
                0, 1, 2, 3
        ));

    }
}