package com.example.datavizar;

/**
NÃO UTILIZADA no app

Esta classe manipula dados através da api Tablesaw, mas esta não funciona no Android.
 */

import java.io.IOException;
import java.util.List;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReadOptions.Builder;

public class DataTable {


    private Table dataSet; // Dataset completo com os dados carregados para o app
    private Table dataSlice; // Slice do dataset que será visualizado em AR, com 2 colunas (categora x valor).

    //talvez retirar esse atributo e descatar a classe DataModel. Trabalhar com o método filtraSlice.
    private List<DataModel> dados;
    
    public static final int FILTER_MAIORES = 1;
    public static final int FILTER_MENORES = 2;
    public static final int FILTER_MAIORES_VS_MENORES = 3;

    public DataTable() {
        this.dataSet = null;
        this.dataSlice = null;
    }
    public DataTable(String filePath) throws IOException, IllegalArgumentException {
        this.setDataSet(filePath);
        this.dataSlice = null;
        this.dados = null;
    }
    public DataTable(Table dataSet) {
        this.dataSet = dataSet;
        this.dataSlice = null;
        this.dados = null;
    }

    public Table getDataSet() {
        return dataSet;
    }

    public Table getDataSlice() {
        return dataSlice;
    }

    public List<DataModel> getDados() {
        return dados;
    } // verificar se vai ser necessário

    /**
     * Recebe a String com o path do arquivo CSV a ser carregado e seta o atributo dataSet.
     * @param filePath  path do arquivo CSV.
     * @throws IOException  Erros ao tentar abrir o arquivo CSV.
     * @throws IllegalArgumentException CSV com menos de 2 colunas.
     */
    public void setDataSet(String filePath) throws IllegalArgumentException {
        // se não conseguir abrir o CSV, joga IOException
        Table tabela = Table.read().csv(filePath); // usa o separador padrão: ','

        //verificar se o arquivo carregou com apenas 1 coluna
        // isso acontece se o arquivo CSV estiver com outro separador diferente do utilizado no read().csv()
        if (tabela.columnCount() == 1) {
            // tentar ler o arquivo CSV com o separador ';'
            Builder builder = CsvReadOptions.builder(filePath).separator(';'); // separador ';'
            CsvReadOptions options = builder.build();
            tabela = Table.read().usingOptions(options);
        }

        if (tabela.columnCount() < 2)
            throw new IllegalArgumentException("A planilha precisa ter pelo menos duas colunas: uma coluna de 'categoria' e uma coluna de 'valores'.");
        else
            this.dataSet = tabela;

    }

    /**
     * Copia 2 colunas do dataSet como uma nova tabela, ordenada descendentemente, e seta como
     * o atributo this.dataSlice.
     * Parte das linhas do dataSlice serão visualizados em AR.
     * @param colCategoria  Coluna com os nomes dos itens da Categoria/Eixo.
     * @param colValores    Coluna com os valores dos itens.
     */
    public void setDataSlice(int colCategoria, int colValores) throws IllegalArgumentException {

        // checar se colCategoria e colValores estão no range de colunas do dataSet
        int qtdCol = this.dataSet.columnCount();
        if (colCategoria < 0 || colCategoria >= qtdCol ||
                colValores < 0 || colValores >= qtdCol) {
            throw new IllegalArgumentException("Algum dos índices das colunas indicadas (" + colCategoria +
                    " e " + colValores + ") não está na Tabela dataSet.");
            // checar se foram indicadas 2 colunas distintas (Categoria x Valor)
        } else if (colCategoria == colValores) {
            throw new IllegalArgumentException("As colunas de Categoria e de Valores não podem ter o mesmo índice");
        } else {
            Table slice = this.dataSet.selectColumns(colCategoria, colValores);

            // checar se a coluna de Valores é numérica
            Column valores = slice.column(1);
            boolean isNumeric = valores instanceof NumericColumn;

            if (!isNumeric)
                throw new IllegalArgumentException("A coluna de valores precisa ter dados numéricos.");

            // todo checar se colCategoria é de String - se não for, converter em String ?

            // filtrar linhas em branco
            slice = slice.dropWhere(valores.isMissing());

            // ordenar a slice e setar como this.dataSlice
            this.dataSlice = slice.sortDescendingOn(valores.name());
        }
    }

    /**
     * Com base nos filtros indicados, copia parte dos dados da tabela this.dataSlice e retorna
     * em uma nova Table.
     * @param modoFilter   Modo de seleção dos dados para a tabela de resposta, conforme atributos
     *                     FILTER da classe: MAIORES, MENORES ou MAIORES VS MENORES.
     * @param qtdLinhas Quantidade que a tabela de resposta deve ter
     * @param valorMin  Valor mínimo considerado para filtrar os dados
     * @param valorMax  Valor máximo considerado para filtrar os dados
     * @return  Tabela com os dados filtrados a partir da dataSlice.
     */
    public Table filtrarSlice(int modoFilter, int qtdLinhas, double valorMin, double valorMax) {
        //lembrete: this.dataSlice está sempre em ordem descendente
        Table tabFiltrada = null;

        //verifica se o dataSlice não é nulo. Se for, o método retorna null.
        if (this.dataSlice != null) {

            //filtrar pelos valores mínimos e máximos indicados
            NumberColumn valores = (NumberColumn) this.dataSlice.column(1);
            tabFiltrada = this.dataSlice.where(valores.isBetweenInclusive(valorMin, valorMax));

            // verifica regularidade do parâmetro qtdLinhas e adequa se necessário
            int totalLinhas = tabFiltrada.rowCount();

            if (qtdLinhas > totalLinhas) qtdLinhas = totalLinhas;
            else if (qtdLinhas < 2 ) qtdLinhas = 2;

            //verifica modo de seleção (maiodes, menores ou maiores vs menores)
            switch (modoFilter) {
                case FILTER_MAIORES:
                    tabFiltrada = this.dataSlice.first(qtdLinhas);
                    break;
                case FILTER_MENORES:
                    tabFiltrada = this.dataSlice.last(qtdLinhas);
                    break;
                case FILTER_MAIORES_VS_MENORES:
                    int topIndex = (qtdLinhas / 2) + (qtdLinhas % 2);
                    int bottomIndex = totalLinhas - (qtdLinhas - topIndex);
                    tabFiltrada = this.dataSlice.dropRange(topIndex, bottomIndex);
                    break;
                default: // aplicar o caso FILTER_MAIORES como padrão.
                    tabFiltrada = this.dataSlice.first(qtdLinhas);
            }
        }
        return tabFiltrada;
    }

    /**
     * Overload do método filtrarSlice, para quando os dados não precisarem ser filtrados com um
     * intervalo numérico de valores específico.
     * @param modoFilter Modo de seleção dos dados para a tabela de resposta, conforme atributos
     *                   FILTER da classe: MAIORES, MENORES ou MAIORES VS MENORES.
     * @param qtdLinhas Quantidade que a tabela de resposta deve ter
     * @return Tabela com os dados filtrados a partir da dataSlice.
     */
    public Table filtrarSlice(int modoFilter, int qtdLinhas) {
        Table tabFiltrada = null;
        if (this.dataSlice != null) {
            double valorMax = (Double) this.dataSlice.get(0, 1);
            double valorMin = (Double) this.dataSlice.last(1).get(0, 1);

            tabFiltrada = filtrarSlice(modoFilter, qtdLinhas, valorMin, valorMax);
        }
        return tabFiltrada;
    }

}
