package com.beehivestudio.mylittleforrest.DB;


import android.provider.BaseColumns;

public final class MissionDB {

    public static final class CreateDB implements BaseColumns {
        public static final String DATE = "date";
        public static final String _TABLENAME0 = "mission";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement Default 0, "
                +DATE+" text not null );";
    }
}
