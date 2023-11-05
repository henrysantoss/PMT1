package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class relatorio extends AppCompatActivity {

    private static final String DB_PRINCIPAL = "principal";
    private static final String DB_DADOS = "dados";
    private static final String DB_GASOLINA = "gasolina";
    private static final String DB_TARIFA = "tarifa";
    private static final String DB_REFEICAO = "refeicao";
    private static final String DB_HOSPEDAGEM = "hospedagem";
    private static final String DB_ENTRETENIMENTO = "entretenimento";

    ArrayList<HashMap<String, String>> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
        lista = new ArrayList<>();

        String id = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int idInt = bundle.getInt("id");
            id = String.valueOf(idInt); // Converter para String
        }

        Cursor dados = buscaDados(id);
        buscaGasolina(id);
        buscaTarifa(id, dados);
        buscaRefeicao(id, dados);
        buscaHospedagem(id);
        buscaEntretenimento(id);
    }

    public Cursor buscaDados(String id) {
        EditText edtDadosTotViaj = findViewById(R.id.edtDadosTotViaj);
        EditText edtDadosDurViagem = findViewById(R.id.edtDadosDurViagem);
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select TOTPESSOAS, DIASVIAGEM from " + DB_DADOS + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToLast()) {
                edtDadosTotViaj.setText(tabela.getString(0));
                edtDadosDurViagem.setText(tabela.getString(1));
                return tabela;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public void buscaGasolina(String id) {
        EditText edtGasolinaTotQuil = findViewById(R.id.edtGasolinaTotQuil);
        EditText edtGasolinaMediaLitro = findViewById(R.id.edtGasolinaMediaLitro);
        EditText edtGasolinaCustoMedioLitro = findViewById(R.id.edtGasolinaCustoMedioLitro);
        EditText edtGasolinaTotVaic = findViewById(R.id.edtGasolinaTotVaic);
        EditText edtGasolinaCalcTotal = findViewById(R.id.edtGasolinaCalcTotal);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select QUILOMETRO, MEDIA, CUSTOMEDIO, TOTVEIC, CH_ADD, CH_PROVISORIO from " + DB_GASOLINA + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToLast()) {
                if (String.valueOf(tabela.getString(4)).equals("T")) {
                    edtGasolinaTotQuil.setText(tabela.getString(0));
                    edtGasolinaMediaLitro.setText(tabela.getString(1));
                    edtGasolinaCustoMedioLitro.setText(tabela.getString(2));
                    edtGasolinaTotVaic.setText(tabela.getString(3));

                    edtGasolinaCalcTotal.setText(String.valueOf(((tabela.getInt(0) / tabela.getInt(1)) * tabela.getInt(2)) / tabela.getInt(3)));
                }
            }
        } catch (Exception e) {

        }
    }

    public void buscaTarifa(String id, Cursor dados) {
        EditText edtTarifaCustoPessoa = findViewById(R.id.edtTarifaCustoPessoa);
        EditText edtTarifaAluguelVeic = findViewById(R.id.edtTarifaAluguelVeic);
        EditText edtTarifaCalcTotal = findViewById(R.id.edtTarifaCalcTotal);
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTOPESSOA, ALUGUELVEIC, CH_ADD, CH_PROVISORIO from " + DB_TARIFA + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToLast()) {
                if (String.valueOf(tabela.getString(2)).equals("T")) {
                    edtTarifaCustoPessoa.setText(tabela.getString(0));
                    edtTarifaAluguelVeic.setText(tabela.getString(1));

                    edtTarifaCalcTotal.setText(String.valueOf((tabela.getInt(0) * dados.getInt(0)) + tabela.getInt(1)));
                }
            }
        } catch (Exception e) {

        }
    }

    public void buscaRefeicao(String id, Cursor dados) {
        EditText edtRefeicaoCustoEst = findViewById(R.id.edtRefeicaoCustoEst);
        EditText edtRefeicaoDia = findViewById(R.id.edtRefeicaoDia);
        EditText edtRefeicaoCalcTotal = findViewById(R.id.edtRefeicaoCalcTotal);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTOREFEICAO, REFEICAODIA, CH_ADD, CH_PROVISORIO from " + DB_REFEICAO + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToLast()) {
                if (String.valueOf(tabela.getString(2)).equals("T")) {
                    edtRefeicaoCustoEst.setText(tabela.getString(0));
                    edtRefeicaoDia.setText(tabela.getString(1));

                    edtRefeicaoCalcTotal.setText(String.valueOf( ((tabela.getInt(1) * dados.getInt(0)) * tabela.getInt(0)) * dados.getInt(1) ));
                }
            }
        } catch (Exception e) {

        }
    }

    public void buscaHospedagem(String id) {
        EditText edtHospedagemCusto = findViewById(R.id.edtHospedagemCusto);
        EditText edtHospedagemNoites = findViewById(R.id.edtHospedagemNoites);
        EditText edtHospedagemQuartos = findViewById(R.id.edtHospedagemQuartos);
        EditText edtHospedagemCalcTotal = findViewById(R.id.edtHospedagemCalcTotal);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTONOITE, TOTNOITE, TOTQUARTO, CH_ADD, CH_PROVISORIO from " + DB_HOSPEDAGEM + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToLast()) {
                if (String.valueOf(tabela.getString(3)).equals("T")) {
                    edtHospedagemCusto.setText(tabela.getString(0));
                    edtHospedagemNoites.setText(tabela.getString(1));
                    edtHospedagemQuartos.setText(tabela.getString(2));

                    edtHospedagemCalcTotal.setText(String.valueOf( (tabela.getInt(0) * tabela.getInt(1)) * tabela.getInt(2) ));
                }
            }
        } catch (Exception e) {

        }
    }

    public void buscaEntretenimento(String id) {
        Double calcTotal = 0.0;
        EditText edtEntretenimentoCalcTotal = findViewById(R.id.edtEntretenimentoCalcTotal);
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select DESCRICAO, CUSTO from " + DB_ENTRETENIMENTO + " where ID_PRINCIPAL = '"+ id +"'", null);
            if (tabela.moveToFirst()) {
                do {
                    HashMap<String,String> item = new HashMap<>();
                    item.put("descricao", tabela.getString(0));
                    item.put("custo", tabela.getString(1));
                    lista.add(item);
                    calcTotal += tabela.getInt(1);
                } while (tabela.moveToNext());

                ListView lv = (ListView) findViewById(R.id.lista);
                ListAdapter adapter = new SimpleAdapter(this, lista, R.layout.registro_lista,new String[]{"descricao","custo"}, new int[]{R.id.descricao, R.id.valor});
                lv.setAdapter(adapter);
                edtEntretenimentoCalcTotal.setText(String.valueOf(calcTotal));
            }
        } catch (Exception e) {

        }
    }


    public void funcFechar(View view) {
        Intent intent = new Intent(this, listaViagens.class);
        startActivity(intent);
    }
}