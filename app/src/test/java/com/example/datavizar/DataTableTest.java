package com.example.datavizar;

import static org.junit.Assert.*;

import android.provider.ContactsContract;

import org.junit.Test;

import tech.tablesaw.api.Table;

public class DataTableTest {

    @Test
    public void getDataSet() {

    }

    @Test
    public void getDataView() {
    }

    @Test
    public void getDados() {
    }

    @Test
    public void setDataView() {
    }

    @Test
    public void testGetDataSet() {
    }

    @Test
    public void testGetDataView() {
    }

    @Test
    public void testGetDados() {
    }

    @Test
    public void setDataSet() {
        try {
            DataTable tabelaTeste = new DataTable("C:\\Users\\patar\\AndroidStudioProjects\\DatavizAR\\app\\data\\WorldGDP.csv");
            System.out.println(tabelaTeste.getDataSet().first(10));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testSetDataSlice() {
        try {
            DataTable tabelaTeste = new DataTable("C:\\Users\\patar\\AndroidStudioProjects\\DatavizAR\\app\\data\\WorldGDP.csv");

            System.out.println(tabelaTeste.getDataSet().first(10));

            tabelaTeste.setDataSlice(0, 43);

            System.out.println(tabelaTeste.getDataSlice().first(10));
            System.out.println(tabelaTeste.getDataSlice().last(10));

            Table tabFiltrada1 = tabelaTeste.filtrarSlice(DataTable.FILTER_MAIORES, 15, 1000, 10000);
            Table tabFiltrada2 = tabelaTeste.filtrarSlice(DataTable.FILTER_MENORES, 15);
            Table tabFiltrada3 = tabelaTeste.filtrarSlice(DataTable.FILTER_MAIORES_VS_MENORES, 15);
            Table tabFiltrada4 = tabelaTeste.filtrarSlice(0, 15);

            System.out.println(tabFiltrada1.printAll());
            System.out.println(tabFiltrada2.printAll());
            System.out.println(tabFiltrada3.printAll());
            System.out.println(tabFiltrada4.printAll());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}