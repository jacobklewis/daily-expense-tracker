package me.jacoblewis.dailyexpense.dependency

import dagger.Component
import me.jacoblewis.dailyexpense.dependency.modules.ViewModelModule
import me.jacoblewis.dailyexpense.mainActivity.MainActivity
import me.jacoblewis.dailyexpense.dependency.modules.AppModule
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import javax.inject.Singleton

/**
 * Created by Jacob on 11/28/2017.
 */
@Singleton
@Component(modules = [ViewModelModule::class, AppModule::class])
interface AppComponent {
    fun inject(app: MyApp)
    fun inject(obj: MainActivity)
}