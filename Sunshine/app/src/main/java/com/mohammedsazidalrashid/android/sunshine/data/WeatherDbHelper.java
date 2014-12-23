/**********************************************************************************
 * Bismillahir Rahmanir Rahim, ALLAHU AKBAR                                       *
 * The MIT License (MIT)                                                          *
 *                                                                                *
 * Copyright (c) 2014 Mohammed Sazid-Al-Rashid                                    *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

package com.mohammedsazidalrashid.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mohammedsazidalrashid.android.sunshine.data.WeatherContract.LocationEntry;
import com.mohammedsazidalrashid.android.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by sazid on 12/23/2014.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    // If the db schema is changed, the version number must be incremented
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherEntry.TABLE_NAME
                        + " ("
                        + WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, "
                        + WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL, "
                        + WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, "
                        + WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "

                        + WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, "
                        + WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, "

                        + WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "
                        + WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "
                        + WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "
                        + WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, "

                        + "FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES "
                        + WeatherEntry.TABLE_NAME + " (" + LocationEntry._ID + "), "

                        + "UNIQUE (" + WeatherEntry.COLUMN_DATETEXT + ", "
                        + WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE"
                        + ");";

        // Create a table to hold locations. A location consists of postal code or city name
        // and a human readable name (e.g "Mountain View")
        final String SQL_CREATE_LOCATION_TABLE =
                "CREATE TABLE " + LocationEntry.TABLE_NAME
                        + " ("
                        + LocationEntry._ID + " INTEGER PRIMARY KEY, "
                        + LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, "
                        + LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, "
                        + LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, "
                        + LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, "

                        + "UNIQUE (" + LocationEntry.COLUMN_LOCATION_SETTING + ") ON CONFLICT IGNORE"

                        + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int newVersion, int oldVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME + ";");
        onCreate(sqLiteDatabase);
    }

}
