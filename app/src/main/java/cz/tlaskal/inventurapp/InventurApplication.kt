package cz.tlaskal.inventurapp

import android.app.Application
import cz.tlaskal.inventurapp.util.AppContainer
import cz.tlaskal.inventurapp.util.AppDataContainer

class InventurApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}