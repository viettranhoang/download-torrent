package com.vit.demotorrent

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.proninyaroslav.libretorrent.core.model.AddTorrentParams
import org.proninyaroslav.libretorrent.core.model.TorrentEngine
import org.proninyaroslav.libretorrent.core.model.data.Priority

class MainActivity : AppCompatActivity() {

    private var streamUrl = "https://rarbg.to/download.php?id=6kjo2f1&h=7e7&f=The.Boys.S02E08.What.I.Know.1080p.AMZN.WEBRip.DDP5.1.x264-NTb%5Brartv%5D-[rarbg.to].torrent"

    private lateinit var engine: TorrentEngine
    private lateinit var params: AddTorrentParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        engine = TorrentEngine.getInstance(this)

        val priorities = arrayOfNulls<Priority>(1)
        params = AddTorrentParams(streamUrl, false, "", "The boys", priorities, Uri.fromFile(filesDir), true, true)

        addTorrent()
    }

    private fun addTorrent() {
        try {
            val t = Thread {
                try {
                    engine.addTorrentSync(params, false)
                } catch (e: Exception) {
                    Log.e("MainActivity", "addTorrentSync: $e")
                }
            }
            t.start()
            t.join()
        } catch (e: InterruptedException) {
            Log.e("MainActivity", "addTorrent: $e")
        }
    }
}