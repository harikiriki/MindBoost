package com.example.mindboost.meditationPage

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mindboost.R

class MeditationActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var songNameTextView: TextView
    private lateinit var meditationPicture: ImageView
    private lateinit var description: TextView
    private lateinit var seekBar: SeekBar
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    seekBar.progress = it.currentPosition
                    seekBar.max = it.duration
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        songNameTextView = findViewById(R.id.songName)
        meditationPicture = findViewById(R.id.meditationPicture)
        description = findViewById(R.id.description)


        val audioUrl = intent.getStringExtra("AUDIO_URL") ?: return
        val meditationType = intent.getStringExtra("MEDITATION_TYPE") ?: return
        val songName = intent.getStringExtra("SONG_NAME") ?: return

        songNameTextView.text = songName
        setMeditationPicture(meditationType)
        setDescriptionText(meditationType)


        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepare() // This could take time for streams, consider using prepareAsync().
            setOnPreparedListener {
                playButton.isEnabled = true
                handler.post(updateSeekBar)
            }
        }

        playButton.setOnClickListener {
            mediaPlayer?.start()
            updateButtonsVisibility(isPlaying = true)
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
            updateButtonsVisibility(isPlaying = false)
        }

        // Ustawienie początkowego stanu widoczności przycisków
        updateButtonsVisibility(isPlaying = false)


        seekBar = findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Opcjonalnie: zaimplementuj, jeśli potrzebujesz
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Opcjonalnie: zaimplementuj, jeśli potrzebujesz
            }
        })
    }

    private fun updateButtonsVisibility(isPlaying: Boolean) {
        if (isPlaying) {
            playButton.visibility = View.INVISIBLE
            pauseButton.visibility = View.VISIBLE
        } else {
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.INVISIBLE
        }
    }

    private fun setMeditationPicture(meditationType: String) {
        val imageResId = when (meditationType) {
            "mindfulness_meditation" -> R.drawable.mindfulness_meditation
            "breath_meditation" -> R.drawable.breath_meditation
            "transdental_meditation" -> R.drawable.transdental_meditation
            "metta_meditation" -> R.drawable.metta_meditation
            else -> R.drawable.meditation
        }
        meditationPicture.setImageResource(imageResId)
    }

    private fun setDescriptionText(meditationType: String) {
        val descriptionText = when (meditationType) {
            "mindfulness_meditation" -> "W czasie tej medytacji zwróć uwagę na swój oddech, nie oceniając go, następnie skup się na odbieraniu bodźców z otoczenia, obserwując \"tu i teraz\". \nZaangażuj wszystkie zmysły, aby w pełni być obecnym w danej chwili."
            "breath_meditation" -> "W czasie tej medytacji zwróć szczególną uwagę na swój oddech, oddychaj w naturalnym tempie nie oceniając go, po prostu obserwuj. \nJeśli skupisz się na czymś innym, np.: myśleniu o przeszłości, nie przejmuj się, tylko spróbuj od nowa."
            "transdental_meditation" -> "W trakcie tej medytacji powtarzaj dźwięk mantry głośno, lub w myslach, jak wolisz."
            "metta_meditation" -> "W czasie tej medytacji chodzi o skupienie się na swojej osobie, oraz docenienie jej. Zrelaksuj się oraz powtarzaj poniższe sformułowania, aby wywołać meettę:\n\n \"Obym był/a zdrowy/a\"\n\"Obym był/a szcześliwy/a\"\n\"Obym był/a spokojny/a\"\n\nMożesz również wymyślić swoje!"
            else -> ""
        }
        description.text = descriptionText
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
