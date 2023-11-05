package com.example.roteirofacil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editUser;
    private EditText editSenha;
    private Button btnEntrar;
    private Button btnRegistro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", 0); // 0 - for private mode

        editUser = findViewById(R.id.username);

        String login = pref.getString("KEY_LEGENDA", "");
        if (!login.isEmpty()) {
            editUser.setText(login);
        }

        editSenha = findViewById(R.id.password);
        btnEntrar = findViewById(R.id.login_button);
        btnRegistro = findViewById(R.id.cadastro_button);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editUser.getText().toString().isEmpty()) {
                    editUser.setError("Campo usuário obrigatório!!");
                    editUser.setEnabled(true);
                }
                else if(editSenha.getText().toString().isEmpty()) {
                    editSenha.setError("Campo senha obrigatórío!!");
                    editSenha.setEnabled(true);
                }
                else {
                    if (verificarUsuario(editUser.getText().toString(),editSenha.getText().toString())) {
                        SharedPreferences.Editor editor = pref.edit();
                        Intent it = new Intent(LoginActivity.this, listaViagens.class);
                        it.putExtra("KEY_LEGENDA", editUser.getText().toString());
                        it.putExtra("KEY_SENHA", editSenha.getText().toString());
                        editor.putString("KEY_LEGENDA", editUser.getText().toString());
                        editor.commit();
                        startActivity(it);
                    }
                    else {
                        alertaIncorreto();
                    }
                }
            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, CriaActivity.class);
                startActivity(it);
            }
        });


    }
    public boolean verificarUsuario(String username, String senha) {
        SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);

        String query = "SELECT * FROM usuarios WHERE username = ? AND senha = ? ";
        Cursor cursor = banco.rawQuery(query, new String[]{username, senha});

        if (cursor.moveToFirst()) {
            cursor.close();
            banco.close();
            return true;
        } else {
            cursor.close();
            banco.close();
            return false;
        }
    }
    private void alertaIncorreto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acesso Negado");
        builder.setMessage("Usuário ou senha incorretos. Por favor, tente novamente.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Fecha o diálogo e permite ao usuário tentar novamente
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
