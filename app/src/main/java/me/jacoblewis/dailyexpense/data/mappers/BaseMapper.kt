package me.jacoblewis.dailyexpense.data.mappers

interface BaseMapper<ROOM_MODEL, FIREBASE_MODEL> {
    fun toFirebase(roomModel: ROOM_MODEL): FIREBASE_MODEL
    fun toRoom(firebaseModel: FIREBASE_MODEL): ROOM_MODEL?
}