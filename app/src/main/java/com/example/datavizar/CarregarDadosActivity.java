package com.example.datavizar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import tech.tablesaw.api.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;



public class CarregarDadosActivity extends AppCompatActivity {

    private int requestCode = 1;
    private String filePath;
    private Table table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregar_dados);
    }

    public void openFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, requestCode);
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
                File file = new File(uri.getPath());
                filePath = file.getAbsolutePath();
                //this.filePath = uri.getPath();

                //Table tabela = Table.read().csv(file);
                //loadCsvFile(filePath);
                Toast.makeText(context, this.filePath, Toast.LENGTH_SHORT).show();
                loadCsvFile(uri);

            }

            // use filePath to access the selected file


        }
    }

    private void loadCsvFile(String filePath) {
        try {
            File inputFile = new File(filePath);
            CsvReadOptions options = CsvReadOptions.builder(inputFile)
                    .header(true)
                    .build();
            table = Table.read().csv(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCsvFile(Uri fileUri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read the CSV data into a Tablesaw Table
            //AndroidTable table1 = (AndroidTable) AndroidTable.create("TabelaTeste");
            Table table = Table.read().csv(reader);
            System.out.println(table.first(10));
            // Do something with the table data
            // ...

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
