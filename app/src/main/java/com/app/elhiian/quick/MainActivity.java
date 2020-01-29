package com.app.elhiian.quick;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.app.elhiian.quick.clases.SQLiteConection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,InicioFragment.OnFragmentInteractionListener{


    TextView nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View viewNavigation=navigationView.getHeaderView(0);

        nombre=viewNavigation.findViewById(R.id.headerName);
        consultarUsuario();
        stopService(new Intent(MainActivity.this,SolicitudesActivity.class));
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorPrincipal,new InicioFragment()).commit();

    }

    private void consultarUsuario() {
        try{
            SQLiteConection conection=new SQLiteConection(this);
            SQLiteDatabase db=conection.getReadableDatabase();
            Cursor cursor=db.rawQuery("select * from usuario",null);
            cursor.moveToNext();
            nombre.setText(cursor.getString(3));
        }catch (Exception e ){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the camera action
            Uri uri=Uri.parse("http://movipp.com");
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this,HistoryActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(MainActivity.this,AccountActivity.class).putExtra("user","U"));
            finish();

        } else if (id == R.id.nav_exit) {
            SQLiteConection conection=new SQLiteConection(this);
            SQLiteDatabase db=conection.getReadableDatabase();
            db.execSQL("delete from usuario");
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
