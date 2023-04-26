package com.example.datavizar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.opencsv.exceptions.CsvException;

import tech.tablesaw.api.Table;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CarregarDadosActivity1 extends AppCompatActivity {

    private final int requestCode = 1;
    private String filePath;
    private Table table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregar_dados1);
    }

    public void openFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        startActivityForResult(intent, requestCode);
        // verificar ActivityResultContracts.GetContent
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Context context = getApplicationContext();
        if (this.requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (intent == null) {
                return;
            }
            Uri uri = intent.getData();
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();

                try {
                    InputStream inputStream = resolver.openInputStream(uri);
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));

                    //throws IOException, CsvException
                    DataModel.createDataSet(bufReader);

                    //Toast.makeText(context, this.filePath, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(CarregarDadosActivity1.this, CarregarDadosActivity2.class));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Não foi possível abrir o arquivo.", Toast.LENGTH_LONG).show();
                } catch (CsvException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "O arquivo não parece ser um csv válido.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ops.. Algo deu errado. Tente outro arquivo.", Toast.LENGTH_LONG).show();
                }


            }

            // use filePath to access the selected file


        }
    }



}
