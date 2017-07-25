package com.ck_telecom.d22434.criminal_note.db;

/**
 * Created by D22434 on 2017/7/24.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String NUMBER = "number";
        }

    }
}
