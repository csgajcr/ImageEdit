package com.jcrspace.mojidemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jcrspace.imageeditor.view.ImageEditView;

public class MainActivity extends AppCompatActivity {

    private ImageEditView imageEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageEditView = (ImageEditView) findViewById(R.id.img_view);
        findViewById(R.id.btn_add_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEditView.setLineMode(Color.RED, 10);
            }
        });
        findViewById(R.id.btn_add_rect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEditView.setRectSelectionMode(Color.RED, 10);
            }
        });
        findViewById(R.id.btn_add_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEditView.setTextEditMode(Color.GREEN, 20);
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
