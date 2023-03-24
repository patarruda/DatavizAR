package com.example.datavizar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.icu.impl.locale.LocaleDistance;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;


public class DataModel {

    //atributos estáticos
    private static List<Map<String, String>> dataSet;
    private static String[] nomesColunas;
    private static double escala;



    private String nome;
    private double valor;
    private boolean isSelected;

    public DataModel(String nome, double valor) {
        this.nome = nome;
        this.valor = valor;
        this.isSelected = false;
    }

    public String getNome() {
        return nome;
    }

    public double getValor() {
        return valor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static List<Map<String, String>> getDataSet() {
        return dataSet;
    }

    public static void createDataSet(String pathCsv) throws FileNotFoundException {
        File myFile = new File(pathCsv);
        BufferedReader bufReader = new BufferedReader(new FileReader(myFile));
        DataModel.createDataSet(bufReader);
    }

    public static void createDataSet(BufferedReader bufReader) throws FileNotFoundException {
        CSVReader reader = new CSVReader(bufReader);

        try {
            List<String[]> csv = reader.readAll();

            nomesColunas = csv.get(0);
            List<Map<String, String>> linhas = new ArrayList<>();
            int qtdColunas = nomesColunas.length;

            for (int i = 1; i < csv.size(); i++) {
                String [] csvLinha = csv.get(i);
                Map<String, String> linha = new HashMap<>(qtdColunas);

                for (int j = 0; j < qtdColunas; j++) {
                    linha.put(nomesColunas[j], csvLinha[j]);
                }
                linhas.add(linha);
            }
            dataSet = linhas;

        } catch (CsvException e) {
            // todo Tratar exceção da chamada: List<String[]> csv = reader.readAll();
        } catch (IOException e) {
            // todo Tratar exceção da chamada: List<String[]> csv = reader.readAll();
        }

    }

    public static String[] getNomesColunas() {
        return nomesColunas;
    }

    //

    public static List<DataModel> createDataSlice(String colunaNomes, String colunaValores) throws NumberFormatException {
        // verificar se a coluna de valores e numérica
            String nome;
            double valor;
            DataModel item;
            List<DataModel> dataSlice = new ArrayList<>();

            for (Map<String, String> linha : dataSet) {
                nome = linha.get(colunaNomes);
                valor = Double.parseDouble(linha.get(colunaValores)); // NumberFormatException
                item = new DataModel(nome, valor);

                dataSlice.add(item);

            }
            return dataSlice;
    }



    //FILTERING METHODS

}
