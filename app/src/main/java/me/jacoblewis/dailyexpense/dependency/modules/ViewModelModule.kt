package me.jacoblewis.dailyexpense.dependency.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.jacoblewis.dailyexpense.dependency.utils.ViewModelFactory
import me.jacoblewis.dailyexpense.dependency.utils.ViewModelKey
import me.jacoblewis.dailyexpense.viewModels.CategoryViewModel
import me.jacoblewis.dailyexpense.viewModels.EnterPaymentViewModel
import me.jacoblewis.dailyexpense.viewModels.PaymentViewModel

/**
 * Created by LEJ4MTP on 3/3/2018.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindMainViewModel(viewModel: PaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(viewModel: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EnterPaymentViewModel::class)
    abstract fun bindEnterPaymentViewModel(viewModel: EnterPaymentViewModel): ViewModel
}