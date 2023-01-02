package tn.esprit.front.Views.Activities.PlayList


import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.front.R
import tn.esprit.front.Views.Activities.Signin.TOKEN
import tn.esprit.front.models.Tracks
import tn.esprit.front.viewmodels.musicApi
import kotlin.concurrent.thread



class song_detail : AppCompatActivity() {
    lateinit var songName: TextView
    lateinit var songArtist: TextView
    lateinit var songCover: ImageView
    lateinit var play: ImageView
    lateinit var duration: TextView
    lateinit var progress: TextView
    lateinit var progressbar: Slider
    lateinit var mediaPlayer: MediaPlayer
    lateinit var sharedPreferences: SharedPreferences
    var isPlaying :  Boolean = false
    lateinit var playNext : ImageView
    lateinit var playPrevious : ImageView
    lateinit var back : ImageView
    lateinit var fav : ImageView
    lateinit var add : ImageView
    var isfavorite : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.played_now)
        supportActionBar?.hide()
        songName = findViewById(R.id.played_now_audio_title)
        songArtist = findViewById(R.id.played_now_audio_author)
        songCover = findViewById(R.id.music_cover)
        play = findViewById(R.id.imageView7)
        duration = findViewById(R.id.end_time)
        progress = findViewById(R.id.progress_time)
        progressbar = findViewById(R.id.slider)
        back = findViewById(R.id.back_to_music_home)
        add = findViewById(R.id.imageView)
        fav = findViewById(R.id.imageView4)
        fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)


        playNext = findViewById(R.id.imageView8)
        playPrevious = findViewById(R.id.imageView9)

        val map: HashMap<String, String> = HashMap()
        sharedPreferences = this.getSharedPreferences("PREF_NAME", 0)
        val token=sharedPreferences.getString(TOKEN,"").toString()
        map["token"]=token
        var favorite: MutableList<String>?
        favorite = sharedPreferences.getStringSet("favoriteTracks", mutableSetOf())?.toMutableList()
        takeData()
        if (favorite == null) {
            favorite = mutableListOf()
        }
        if (favorite.contains(songName.text.toString()))
        {
            fav.setImageResource(R.drawable.ic_baseline_favorite_24)
            isfavorite = true
        }
        else
        {
            fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            isfavorite = false
        }

        mediaPlayer = MediaPlayer()
        var url = intent.getStringExtra("songUrl")
        var id = intent.getStringExtra("songId")

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()



        val services = musicApi.create()

        fav.setOnClickListener() {
            map["songname"] = songName.text.toString()
            if (isfavorite == false) {
                isfavorite = true
                services.addFavoritesTrack(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            fav.setImageResource(R.drawable.ic_baseline_favorite_24)
                            Toast.makeText(
                                this@song_detail,
                                "Added to your favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else
            {
                isfavorite = false
                services.removeFavoritesTrack(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                            Toast.makeText(
                                this@song_detail,
                                "Removed from your favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                    }
                })

            }

        }

        back.setOnClickListener {
            //break all threads
            mediaPlayer.stop()
            mediaPlayer.release()
            Thread.currentThread().interrupt()

            finish()

        }

        //updae the progress text

        thread(start = true) {
            while (true) {
                if (mediaPlayer.isPlaying) {
                    val current = mediaPlayer.currentPosition
                    val total = mediaPlayer.duration
                    val currentMin = current / 1000 / 60
                    val currentSec = current / 1000 % 60
                    val totalMin = total / 1000 / 60
                    val totalSec = total / 1000 % 60
                    val currentText = String.format("%02d:%02d", currentMin, currentSec)
                    runOnUiThread {
                        progress.text = currentText

                    }
                    Thread.sleep(1000)
                }

            }
        }


        playNext.setOnClickListener {

            map["currentTrack"] = id.toString()
            // get the next song
            Log.e("id", id.toString())
            val tag = intent.getStringExtra("tag")
            if (tag == "fav") {
                services.getNextFavoritesTracks(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            val track = response.body()
                            id = track?._id
                            songName.text = track?.name
                            songArtist.text = track?.artist
                            Glide.with(this@song_detail).load(track?.cover).into(songCover)
                            //update the progress bar
                            progressbar.value = 0f
                            progress.text = "00:00"
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(track?.url)
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                            playAudio()
                        } else {
                            Toast.makeText(this@song_detail, "no response", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else if (tag == "top") {
                services.getNextSongtop(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            val track = response.body()
                            id = track?._id
                            songName.text = track?.name
                            songArtist.text = track?.artist
                            Glide.with(this@song_detail).load(track?.cover).into(songCover)
                            //update the progress bar
                            progressbar.value = 0f
                            progress.text = "00:00"
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(track?.url)
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                            playAudio()
                        } else {
                            Toast.makeText(this@song_detail, "no response", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else if (tag == "new") {
                services.getNextSongnew(map).enqueue(object : Callback<Tracks> {
                    override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                        if (response.isSuccessful) {
                            val track = response.body()
                            id = track?._id
                            songName.text = track?.name
                            songArtist.text = track?.artist
                            Glide.with(this@song_detail).load(track?.cover).into(songCover)
                            //update the progress bar
                            progressbar.value = 0f
                            progress.text = "00:00"
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(track?.url)
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                            playAudio()
                        } else {
                            Toast.makeText(this@song_detail, "no response", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Tracks>, t: Throwable) {
                        Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        playPrevious.setOnClickListener {
            map["currentTrack"] = id.toString()
            services.getPreviousFavoritesTracks(map).enqueue(object : Callback<Tracks> {
                override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                    if (response.isSuccessful) {
                        val track = response.body()
                        id = track?._id
                        songName.text = track?.name
                        songArtist.text = track?.artist
                        Glide.with(this@song_detail).load(track?.cover).into(songCover)
                        //update the progress bar
                        progressbar.value = 0f
                        progress.text = "00:00"
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(track?.url)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        playAudio()
                    }
                    else{
                        Toast.makeText(this@song_detail, "no response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Tracks>, t: Throwable) {
                    Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        mediaPlayer.setOnCompletionListener {
            //go to the next song
            map["currentTrack"] = id.toString()
            services.getNextFavoritesTracks(map).enqueue(object : Callback<Tracks> {
                override fun onResponse(call: Call<Tracks>, response: Response<Tracks>) {
                    if (response.isSuccessful) {
                        val track = response.body()
                        id = track?._id
                        songName.text = track?.name
                        songArtist.text = track?.artist
                        Glide.with(this@song_detail).load(track?.cover).into(songCover)
                        //update the progress bar
                        progressbar.value = 0f
                        progress.text = "00:00"
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(track?.url)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        playAudio()
                    }
                    else{
                        Toast.makeText(this@song_detail, "no response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Tracks>, t: Throwable) {
                    Toast.makeText(this@song_detail, "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }


        play.setOnClickListener { playAudio() }


    }





    private fun takeData()
    {
        val name = intent.getStringExtra("songName")
        val artist = intent.getStringExtra("songArtist")
        val cover = intent.getStringExtra("songCover")
        val url = intent.getStringExtra("songUrl")
        val id = intent.getStringExtra("songId")


        songName.text = name
        songArtist.text = artist
        Glide.with(this).load(cover).into(songCover)
        // mediaPlayer.setDataSource(url)
    }
    // refresh the activity
    override fun onResume() {
        super.onResume()
        if (mediaPlayer.isPlaying) {
            play.setImageResource(R.drawable.ic_baseline_pause_24)
            play.tag = "pause"
        } else {
            play.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            play.tag = "play"
        }
    }

    // createTimeLabel function to convert the time in milliseconds to minutes and seconds
    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    override fun onDestroy() {
        super.onDestroy()
        //stop all the threads when the activity is destroyed
        Thread.currentThread().interrupt()
        mediaPlayer.release()
    }

    fun playAudio() {
        val dur = mediaPlayer.duration
        // set the duration of the audio in the text view in minutes and seconds
        duration.text = createTimeLabel(dur)
        if (isPlaying)
        {
            mediaPlayer.pause()
            play.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            isPlaying = false

        }
        else
        {
            mediaPlayer.start()
            play.setImageResource(R.drawable.ic_baseline_pause_24)
            isPlaying = true

            progressbar.valueTo = mediaPlayer.duration.toFloat()

            progressbar.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    mediaPlayer.pause()

                }

                override fun onStopTrackingTouch(slider: Slider) {
                    mediaPlayer.seekTo(slider.value.toInt())
                    mediaPlayer.start()

                }})
        }
        Thread(Runnable {
            while (mediaPlayer != null) {
                try {
                    progressbar.value = mediaPlayer.currentPosition.toFloat()
                    //create a thred to update the progress text view
                    var msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    val handlerThread = HandlerThread("handlerThread")
                    handlerThread.start()
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
        // update the progress text view in minutes and seconds

    }



}