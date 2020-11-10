package com.vit.demotorrent

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.proninyaroslav.libretorrent.core.model.AddTorrentParams
import org.proninyaroslav.libretorrent.core.model.TorrentEngine
import org.proninyaroslav.libretorrent.core.model.TorrentInfoProvider
import org.proninyaroslav.libretorrent.core.model.data.Priority
import java.util.*

class MainActivity : AppCompatActivity() {

    private var streamUrl =
        "magnet:?xt=urn:btih:549936E2602D3D646F84982F53356EA2D0C94313&dn=Mulan.2020.HDRip.XviD.AC3-EVO%5BTGx%5D&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce&tr=udp%3A%2F%2F9.rarbg.to%3A2920%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.internetwarriors.net%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.cyberia.is%3A6969%2Fannounce"

    private lateinit var engine: TorrentEngine
    private lateinit var params: AddTorrentParams
    private val disposables = CompositeDisposable()


    private lateinit var stateProvider: TorrentInfoProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionX.init(this)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    engine = TorrentEngine.getInstance(applicationContext)
                    stateProvider = TorrentInfoProvider.getInstance(applicationContext)
                    engine.start()

                    addTorrent()

                    getTorrent()
                } else {
                    finish()
                }
            }


    }

    private fun getTorrent() {
        stateProvider.observeInfoList()
            .subscribeOn(Schedulers.io())
            .subscribe {
                it.forEach {
                    Log.e("MainActivity", "getTorrent: $it")
                }
            }
            .let { disposables.add(it) }
    }

    private fun addTorrent() {

        val priorities = arrayOfNulls<Priority>(3)
        Arrays.fill(priorities, Priority.DEFAULT)
        params = AddTorrentParams(
            streamUrl,
            true,
            "549936e2602d3d646f84982f53356ea2d0c94313",
            "Mulan.2020.HDRip.XviD.AC3-EVO[TGx]-vit",
            priorities,
            Uri.fromFile(filesDir),
            false,
            false
        )

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