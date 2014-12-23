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

package com.mohammedsazidalrashid.android.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.mohammedsazidalrashid.android.sunshine.data.WeatherContract.LocationEntry;
import com.mohammedsazidalrashid.android.sunshine.data.WeatherContract.WeatherEntry;
import com.mohammedsazidalrashid.android.sunshine.data.WeatherDbHelper;

import java.util.Set;

/**
 * Created by sazid on 12/23/2014.
 */
public class TestDb extends AndroidTestCase {

    private final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext)
                .getWritableDatabase();
        assertEquals(db.isOpen(), true);
        db.close();
    }

    public void testInsertReadDb() throws Throwable {

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        locationValues.put(LocationEntry.COLUMN_COORD_LAT, 64.772);
        locationValues.put(LocationEntry.COLUMN_COORD_LONG, -147.355);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        // Verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // A cursor is the primary interface to the query results
        Cursor cursor = queryDbForTable(db, LocationEntry.TABLE_NAME, locationValues);

        assertMoveToFirst(cursor, locationValues);

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        assert(weatherRowId != -1);
        Log.d(LOG_TAG, "Weather row id: " + weatherRowId);

        cursor = queryDbForTable(db, WeatherEntry.TABLE_NAME, weatherValues);

        assertMoveToFirst(cursor, weatherValues);

        db.close();

    }

    private void assertColumnsWithValues(Cursor cursor, ContentValues contentValues) {
        for (String key: contentValues.keySet()) {
            int idx = cursor.getColumnIndex(key);
            String valueInDb = cursor.getString(idx);
            assertEquals(valueInDb, contentValues.getAsString(key));
        }
    }

    private String[] valuesFromContents(ContentValues contentValues) {
        if (contentValues == null) {
            return null;
        }

        Set<String> set = contentValues.keySet();
        String[] values = new String[set.size()];
        values = set.toArray(values);
        return values;
    }

    private Cursor queryDbForTable
            (SQLiteDatabase db, String tableName, ContentValues contentValues) {
        String[] projection = valuesFromContents(contentValues);
        Cursor cursor = db.query(
                tableName,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        return cursor;
    }

    private void assertMoveToFirst(Cursor cursor, ContentValues contentValues) {
        if (cursor.moveToFirst()) {
            assertColumnsWithValues(cursor, contentValues);
        } else {
            fail("No data returned :(");
        }
    }

}
