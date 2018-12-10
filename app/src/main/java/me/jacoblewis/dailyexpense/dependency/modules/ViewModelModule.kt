package me.jacoblewis.dailyexpense.dependency.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.jacoblewis.dailyexpense.dependency.utils.ViewModelFactory
import me.jacoblewis.dailyexpense.dependency.utils.ViewModelKey
import me.jacoblewis.dailyexpense.fragments.categories.CategoryViewModel
import me.jacoblewis.dailyexpense.fragments.categories.SettingsViewModel
import me.jacoblewis.dailyexpense.fragments.main.MainViewModel

/**
 * Created by LEJ4MTP on 3/3/2018.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(viewModel: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}