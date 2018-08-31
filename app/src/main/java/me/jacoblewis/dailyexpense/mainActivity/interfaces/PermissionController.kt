package me.jacoblewis.dailyexpense.mainActivity.interfaces

import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavPermission

interface PermissionController : ActivityController {
    fun requestPermission(navPermission: NavPermission) {

    }
}