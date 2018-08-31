package me.jacoblewis.dailyexpense.dependency.modules

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import me.jacoblewis.dailyexpense.dependency.utils.ViewModelFactory

/**
 * Created by LEJ4MTP on 3/3/2018.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel::class)
//    abstract fun bindMainViewModel(ViewModel: MainViewModel): ViewModel
}