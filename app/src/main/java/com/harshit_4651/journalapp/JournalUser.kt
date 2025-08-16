package com.harshit_4651.journalapp

import android.app.Application

class JournalUser: Application() {

    var userName: String? = null
    var userId: String? = null

    companion object {
        var instance: JournalUser? = null
            get() {

               if(field == null) {
                   // create a new instance from journal User
                   field = JournalUser()
               }
               return field
            }
        private set

    }

}