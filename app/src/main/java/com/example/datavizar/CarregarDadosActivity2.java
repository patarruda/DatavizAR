package com.example.datavizar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarregarDadosActivity2 extends AppCompatActivity {

    private Spinner spnColNomes;
    private Spinner spnColValores;
    private ArrayAdapter<String> adapterSpnNomes;
    private ArrayAdapter<String> adapterSpnValores;
    private RecyclerView recyclerTabela;
    private DataAdapter adapterRecycler;
    //private Button okButton;
    public List<DataModel> dataSlice;
    private int ordem = 0;

    public static final int ORD_CATEGORIA_ASC = 1;
    public static final int ORD_CATEGORIA_DES = 2;
    public static final int ORD_VALORES_ASC = 2;
    public static final int ORD_VALORES_DES = 4;

    /*
    // para Sessão AR
    private boolean mUserRequestedInstall = true;
    private Session arSession;
    private ArFragment arFragment;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregar_dados2);

        //Variáveis contendo dados para spinners e recyclerView
        String[] cols = DataModel.getNomesColunas();
        List<Map<String, String>> dataSet = DataModel.getDataSet();
        List<Map<String, String>> dataset2 = DataModel.getDataSet();

        //Configurar spinners com lista dos nomes das colunas ("cols")
        spnColNomes = findViewById(R.id.spinColunaNomes);
        spnColValores = findViewById(R.id.spinColunaValores);

          //adapters dos spinners: setados com dados de "cols" e layouts default para "spinner item" e para "dropdown".
        adapterSpnNomes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,cols);
        adapterSpnValores = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,cols);
        adapterSpnNomes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpnValores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

          // Aplicar adapter aos spinners e seleciona item selecionado (primeira exibição)
        spnColNomes.setAdapter(adapterSpnNomes);
        spnColValores.setAdapter(adapterSpnValores);
        spnColValores.setSelection(1);

          //configurar eventListeners para os spinners
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                atualizarDados((String) spnColNomes.getSelectedItem(),
                               (String) spnColValores.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        };
        spnColNomes.setOnItemSelectedListener(spinnerListener);
        spnColValores.setOnItemSelectedListener(spinnerListener);

        //Configurar event listeners para os headers das colunas "CATEGORIA" e "VALORES"
        //Ao clicar no header, os dados serão ordenados em ordem crescente ou decrescente
        TextView tvCategoria = findViewById(R.id.headerCategoria);
        TextView tvValores = findViewById(R.id.headerValores);

        tvCategoria.setOnClickListener(v -> {
            if (ordem == ORD_CATEGORIA_ASC) {
                ordenarCategoriaDes();
            } else {
                ordenarCategoriaAsc();
            }
        });

        tvValores.setOnClickListener(v -> {
            if (ordem == ORD_VALORES_ASC) {
                ordenarValorDes();
            } else {
                ordenarValorAsc();
            }
        });

        recyclerTabela = findViewById(R.id.recycler);
        setRecyclerView();

        //Configurar okButton
        //okButton = findViewById(R.id.btOk);
        //okButton.oncl

    }

    private void ordenarValorAsc() {
        dataSlice.sort(DataModel.compareValorAsc());
        ordem = ORD_VALORES_ASC;
        //adapterRecycler.setDataSlice(dataSlice);
        adapterRecycler.notifyDataSetChanged();
    }

    private void ordenarValorDes() {
        dataSlice.sort(DataModel.compareValorDes());
        ordem = ORD_VALORES_DES;
    //  adapterRecycler.setDataSlice(dataSlice);
        adapterRecycler.notifyDataSetChanged();
    }

    private void ordenarCategoriaAsc() {
        dataSlice.sort(DataModel.compareNomeAsc());
        ordem = ORD_CATEGORIA_ASC;
        //  adapterRecycler.setDataSlice(dataSlice);
        adapterRecycler.notifyDataSetChanged();
    }

    private void ordenarCategoriaDes() {
        dataSlice.sort(DataModel.compareNomeDes());
        ordem = ORD_CATEGORIA_DES;
        //  adapterRecycler.setDataSlice(dataSlice);
        adapterRecycler.notifyDataSetChanged();
    }

    private void atualizarDados(String colNomes, String colValores) {
        try {
            dataSlice = DataModel.createDataSlice(colNomes, colValores);
        } catch (Exception e) { //NumberFormat
            dataSlice = null;
            showAlerta("Selecione uma coluna contendo nomes para \"Categoria\" e uma coluna numérica para \"Valores\"!");
        } finally {
            adapterRecycler.setDataSlice(dataSlice);
            adapterRecycler.notifyDataSetChanged();
        }
    }

    private void setRecyclerView() {
        recyclerTabela.setHasFixedSize(true);
        recyclerTabela.setLayoutManager(new LinearLayoutManager(this));
        adapterRecycler = new DataAdapter(this, dataSlice);
        recyclerTabela.setAdapter(adapterRecycler);
    }

    private void showAlerta(String msg) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setMessage(msg);
        alerta.show();
    }

    public void openAR(View v) {
        // todo: pegar selected items de dataSlice, passar para próxima Activity (Visualização AR)
        List<DataModel> dadosSelecionados = this.adapterRecycler.dataSlice.stream()
                .filter(DataModel::isSelected).sorted(DataModel.compareValorAsc()).collect(Collectors.toList());

        //Ordenar os dados selecionados por ordem crescente de valores

        Intent intent = new Intent(CarregarDadosActivity2.this, ARVisualizerActivity.class);
        intent.putParcelableArrayListExtra("dadosSelecionados", new ArrayList<DataModel>(dadosSelecionados));

        startActivity(intent);



        /*
        // ARVisualizerFragment - app fica muito lento e trava o celular

        ARVisualizerFragment arVisualizerFragment = new ARVisualizerFragment();
        //arVisualizerFragment.setDataSlice();


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, arVisualizerFragment);
        fragmentTransaction.commit();
        */
    }


}
