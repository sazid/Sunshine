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

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static Bundle bundleForFragments = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        shouldDisplayHomeUp();
                    }
                }
        );

    }

    @Override
    public void onBackPressed() {
//        Log.v(LOG_TAG, String.valueOf(getFragmentManager().getBackStackEntryCount()));
        if (getFragmentManager().getBackStackEntryCount() < 1) {
            super.onBackPressed();
        }

        getFragmentManager().popBackStack();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(
                            R.animator.enter_anim,
                            R.animator.exit_anim,
                            R.animator.enter_anim_reverse,
                            R.animator.exit_anim_reverse)
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
            return true;
        } else if (id == R.id.action_map) {
            showMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shouldDisplayHomeUp() {
        boolean canGoBack = getFragmentManager().getBackStackEntryCount() > 0;
        changeActionBarTitle(canGoBack);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage().toString(), e);
            e.printStackTrace();
        }
    }

    private void changeActionBarTitle(boolean canGoBack) {
        if (!canGoBack) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This method is called when the Up button is pressed.
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        getFragmentManager().popBackStack();
        return true;
    }

    private void showMap() {
        String preferredLocation = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(
                        getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default)
                );

        String queryParam = "q";
        Uri mapUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter(queryParam, preferredLocation).build();

        Intent mapIntent = new Intent();
        mapIntent.setAction(Intent.ACTION_VIEW);
        mapIntent.setData(mapUri);

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(
                    this,
                    "Sorry, no suiteable application found on your device to handle this operation",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}
