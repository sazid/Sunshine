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

/**
 * Created by sazid on 12/23/2014.
 */
public class TestProvider extends AndroidTestCase {

    private final String LOG_TAG = TestProvider.class.getSimpleName();

    static public String TEST_CITY_NAME = "North Pole";
    static public String TEST_LOCATION = "99705";
    static public String TEST_DATE = "20141205";

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testGetType() {
        // content://com.mohammedsazid.android.sunshine/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // content://vnd.android.cursor.dir/com.mohammedsazid.android.sunshine/weather/
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.mohammedsazid.android.sunshine/weather/94074
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));
        // content://vnd.android.cursor.dir/com.mohammedsazid.android.sunshine/weather/94074
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.mohammedsazid.android.sunshine/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // content://vnd.android.cursor.item/com.mohammedsazid.android.sunshine/weather/94074/20140612
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.mohammedsazid.android.sunshine/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // content://vnd.android.cursor.dir/com.mohammedsazid.android.sunshine/weather/
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        long id = 12;
        // content://com.mohammedsazid.android.sunshine/weather/
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(id));
        // content://vnd.android.cursor.item/com.mohammedsazid.android.sunshine/weather/12
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertReadProvider() throws Throwable {

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues locationValues = createNorthPoleLocationValues();

        //------------------- LocationEntry test --------------------//

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        // Verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New location row id: " + locationRowId);

        // A cursor is the primary interface to the query results
        String[] projection = TestDb.valuesFromContents(locationValues);
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, locationValues);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, locationValues);
        cursor.close();

        //------------------- WeatherEntry test --------------------//

        ContentValues weatherValues = createWeatherValues(locationRowId);

        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        assert(weatherRowId != -1);
        Log.d(LOG_TAG, "Weather row id: " + weatherRowId);

        projection = TestDb.valuesFromContents(weatherValues);
        cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, weatherValues);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                null,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, weatherValues);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, weatherValues);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null
        );
        TestDb.assertMoveToFirstAndValidate(cursor, weatherValues);
        cursor.close();

        db.close();

    }

}