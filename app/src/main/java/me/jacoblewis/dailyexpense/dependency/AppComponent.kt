package me.jacoblewis.dailyexpense.dependency

import dagger.Component
import me.jacoblewis.dailyexpense.dependency.modules.AppModule
import me.jacoblewis.dailyexpense.dependency.modules.DBModule
import me.jacoblewis.dailyexpense.dependency.modules.ViewModelModule
import me.jacoblewis.dailyexpense.dependency.utils.MyApp
import me.jacoblewis.dailyexpense.fragments.categories.CategoryFragment
import me.jacoblewis.dailyexpense.fragments.categories.ChooseCategoryFragment
import me.jacoblewis.dailyexpense.fragments.settings.SettingsFragment
import me.jacoblewis.dailyexpense.fragments.enterCategory.EnterCategoryDialogFragment
import me.jacoblewis.dailyexpense.fragments.enterPayment.EnterPaymentFragment
import me.jacoblewis.dailyexpense.fragments.main.MainFragment
import me.jacoblewis.dailyexpense.mainActivity.MainActivity
import javax.inject.Singleton

/**
 * Created by Jacob on 11/28/2017.
 */
@Singleton
@Component(modules = [ViewModelModule::class, AppModule::class, DBModule::class])
interface AppComponent {
    fun inject(app: MyApp)
    fun inject(obj: MainActivity)
    fun inject(obj: MainFragment)
    fun inject(obj: EnterPaymentFragment)
    fun inject(obj: CategoryFragment)
    fun inject(obj: ChooseCategoryFragment)
    fun inject(obj: EnterCategoryDialogFragment)
    fun inject(obj: SettingsFragment)
}