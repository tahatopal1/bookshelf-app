package com.example.bookshelf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookshelf.R;
import com.example.bookshelf.model.AppUser;
import com.example.bookshelf.service.AppService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminSessionActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView = null;
    private AppService appService;
    private AppUser appUser;

    private TextView tv_username;
    private TextView tv_name_surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_session);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        setTextAreas();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main_page, R.id.nav_shelf, R.id.nav_book, R.id.nav_users, R.id.nav_users_not_approved)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_session, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), getString(R.string.cannot_go_main_page), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(AdminSessionActivity.this, SignUpActivity.class);
                Bundle bundle = getIntent().getExtras();
                bundle.putInt(getString(R.string.signSession), 1);
                intent.putExtras(bundle);
                startActivity(intent);
                return super.onOptionsItemSelected(item);
            case R.id.action_logout:
                startActivity(new Intent(AdminSessionActivity.this, MainActivity.class));
                return super.onOptionsItemSelected(item);
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setTextAreas() {

        appService = new AppService(getApplicationContext());
        View header = navigationView.getHeaderView(0);

        tv_username = header.findViewById(R.id.tv_username);
        tv_name_surname = header.findViewById(R.id.tv_name_surname);

        appUser = appService.getCurrentUser(getIntent().getExtras().getString(getString(R.string.userSession)));

        tv_username.setText(appUser.getUsername());
        tv_name_surname.setText(appUser.getName().concat(" ").concat(appUser.getSurname()));
    }


}
