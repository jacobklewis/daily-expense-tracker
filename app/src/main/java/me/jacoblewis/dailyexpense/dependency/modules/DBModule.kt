package me.jacoblewis.dailyexpense.dependency.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.jacoblewis.dailyexpense.data.BalancesDB
import me.jacoblewis.dailyexpense.data.daos.BudgetsDao
import me.jacoblewis.dailyexpense.data.daos.CategoriesDao
import me.jacoblewis.dailyexpense.data.daos.PaymentsDao
import javax.inject.Singleton

@Module
class DBModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun providesDB(context: Context): BalancesDB {
        return BalancesDB.getInstance(context)
    }

    @Provides
    internal fun providesPaymentsDao(db: BalancesDB): PaymentsDao {
        return db.paymentsDao()
    }

    @Provides
    internal fun providesCategoriesDao(db: BalancesDB): CategoriesDao {
        return db.categoriesDao()
    }

    @Provides
    internal fun providesBudgetsDao(db: BalancesDB): BudgetsDao {
        return db.budgetsDao()
    }
}