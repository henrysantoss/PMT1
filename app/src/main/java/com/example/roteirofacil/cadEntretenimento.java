package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class cadEntretenimento extends AppCompatActivity {
    ArrayList<HashMap<String, String>> lista;
    EditText edtDescricao;
    EditText edtCusto;
    EditText edtCalcTotal;

    private static final String DB_PRINCIPAL = "principal";
    private static final String DB_DADOS = "dados";
    private static final String DB_GASOLINA = "gasolina";
    private static final String DB_TARIFA = "tarifa";
    private static final String DB_REFEICAO = "refeicao";
    private static final String DB_HOSPEDAGEM = "hospedagem";
    private static final String DB_ENTRETENIMENTO = "entretenimento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_entretenimento);
        lista = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.lista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remova o item da lista com base na posição do item clicado
                lista.remove(position);
                ListAdapter adapter = new SimpleAdapter(cadEntretenimento.this, lista, R.layout.registro_lista,new String[]{"descricao","custo"}, new int[]{R.id.descricao, R.id.valor});
                lv.setAdapter(adapter);

                Double calcTotal = 0.0;
                for (HashMap<String, String> hashMap : lista) {
                    String valor = hashMap.get("custo");
                    if (valor != null) {
                        calcTotal += Integer.parseInt(valor);
                    }
                }
                edtCalcTotal = findViewById(R.id.edtCalcTotal);
                edtCalcTotal.setText(String.valueOf(calcTotal));
            }
        });
    }

    public void funcAdd(View view) {
        edtDescricao = findViewById(R.id.edtDescricao);
        edtCusto = findViewById(R.id.edtCusto);

        String desc = String.valueOf(edtDescricao.getText());
        String custo = String.valueOf(edtCusto.getText());

        if (desc.isEmpty()) {
            Toast.makeText(this, "Descrição está vazia", Toast.LENGTH_LONG).show();
            return;
        }

        if (custo.isEmpty()) {
            Toast.makeText(this, "Custo está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String,String> item = new HashMap<>();
        item.put("descricao", desc);
        item.put("custo", custo);
        lista.add(item);

        edtDescricao.setText("");
        edtCusto.setText("");
        ListView lv = (ListView) findViewById(R.id.lista);
        ListAdapter adapter = new SimpleAdapter(this, lista, R.layout.registro_lista,new String[]{"descricao","custo"}, new int[]{R.id.descricao, R.id.valor});
        lv.setAdapter(adapter);

        Double calcTotal = 0.0;
        for (HashMap<String, String> hashMap : lista) {
            String valor = hashMap.get("custo");
            if (valor != null) {
                calcTotal += Integer.parseInt(valor);
            }
        }
        edtCalcTotal = findViewById(R.id.edtCalcTotal);
        edtCalcTotal.setText(String.valueOf(calcTotal));
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadHospedagem.class);
        startActivity(intent);
    }

    public void funcFinalizar(View view) {
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_ENTRETENIMENTO + " where CH_PROVISORIO = 'T'");
            for (HashMap<String, String> hashMap : lista) {
                String descricao = hashMap.get("descricao");
                String valor = hashMap.get("custo");
                if ((valor != null) && (descricao != null)) {
                    banco.execSQL("insert into " + DB_ENTRETENIMENTO + " (descricao, custo, ch_provisorio) values ('"+ descricao +"', '"+ valor +"', 'T')");
                }
            }
            Date dataAtual = new Date();
            String formato = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
            String dataAtualString = sdf.format(dataAtual);

            ContentValues principal = new ContentValues();
            principal.put("data", dataAtualString);
            long id = banco.insert("principal", null, principal);
            banco.execSQL("update " + DB_DADOS + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_GASOLINA + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_REFEICAO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_TARIFA + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_REFEICAO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_HOSPEDAGEM + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_ENTRETENIMENTO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");

            Intent intent = new Intent(view.getContext(), relatorio.class);
            int intId = (int) id;
            intent.putExtra("id", intId);
            startActivityForResult(intent, 0);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}