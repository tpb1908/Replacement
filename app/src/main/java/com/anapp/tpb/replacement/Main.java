package com.anapp.tpb.replacement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.anapp.tpb.replacement.Setup.IntroActivity;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
//
//        TermStorageHelper tsh = new TermStorageHelper(getApplicationContext());
//        tsh.addTerm(new Term("Term one",1, 2));
//        tsh.addTerm(new Term("Term two", 3, 4));
//        tsh.addTerm(new Term("Term three", 5, 6));
//
//        ArrayList<Term> terms = tsh.getAllTerms();
//
//        tsh.delete(terms.get(0));
//
//        terms = tsh.getAllTerms();
//        for (Term t : terms) {
//            Log.d("Term", t.toString());
//        }


        if (pref.getBoolean("firststart", true) || true) { //TODO
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.apply(); //apply writes in background, whereas commit writes immediately in the foreground
            //Start the first start screen
            Intent i = new Intent(getApplicationContext(), IntroActivity.class);
            startActivity(i);

        }


        setContentView(R.layout.activity_main);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings ("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*TODO-
    * The setup activity allows setting up terms for the first time
    * The next arrow button expands into the next layer of setup
    * Lessons should be added during the same setup process
     */

}
