package com.printurth.database;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String ID = "id";
        public static final String FILENAME = "fileName";
        public static final String TITLE = "title";
        public static final String THUMBIMGNAME = "thumbImgName";
        public static final String MAKERNAME = "makerName";
        public static final String UPLOADAT = "uploadAt";
        public static final String LEVEL = "level";
        public static final String TIME = "time";
        public static final String FILAMENT = "filament";
        public static final String DESCRIPTION = "description";
        public static final String LIKE = "like";
        public static final String ISFIN = "isfin";
        public static final String LASTDATE = "lastdate";
        public static final String _TABLENAME0 = "stldata_table";


        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +ID+" text not null , "
                +FILENAME+" text not null , "
                +TITLE+" text not null , "
                +THUMBIMGNAME+" text not null , "
                +MAKERNAME+" text not null , "
                +UPLOADAT+" text not null , "
                +LEVEL+" text not null , "
                +TIME+" text not null , "
                +FILAMENT+" text not null , "
               +DESCRIPTION+" text not null , "
                +LIKE+" integer not null , "
                +ISFIN+" integer  not null, "
                +LASTDATE+" text  not null );";
    }


}