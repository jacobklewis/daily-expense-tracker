package me.jacoblewis.dailyexpense.mainActivity.interfaces

import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavException

interface ExceptionController : ActivityController {

    fun handleException(exception: NavException) {
        when (exception) {
        }
    }

}