package com.example.roteirofacil;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CriaActivity extends AppCompatActivity {

    private EditText editUser;
    private EditText editSenha;
    private EditText editConfSenha;
    private Button cria_usuario;

    public static final String DB_USUARIOS = "usuarios";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criarusuario);

        editUser = findViewById(R.id.usuario);
        editSenha = findViewById(R.id.senha);
        editConfSenha = findViewById(R.id.confirma_senha);
        cria_usuario = findViewById(R.id.cria_usuario);
        cria_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editUser.getText().toString().isEmpty()) {
                    editUser.setError("Campo usuário obrigatório!!");
                    editUser.setEnabled(true);
                }
                else if(editSenha.getText().toString().isEmpty()) {
                    editSenha.setError("Campo senha obrigatórío!!");
                    editSenha.setEnabled(true);
                }
                else if(!editSenha.getText().toString().equals(editConfSenha.getText().toString())) {
                    editConfSenha.setError("Senhas são diferentes!!");
                    editConfSenha.setEnabled(true);
                }
                else {
                    if (inserirNovoUsuario(editUser.getText().toString(), editSenha.getText().toString())) {
                        Toast.makeText(CriaActivity.this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CriaActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CriaActivity.this, "Falha ao criar usuário. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private boolean inserirNovoUsuario(String novoUsuario, String novaSenha) {
        SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);

        ContentValues values = new ContentValues();
        values.put("username", novoUsuario);
        values.put("senha", novaSenha);

        long newRowId = banco.insert(DB_USUARIOS, null, values);
        banco.close();

        return newRowId != -1; // Retorna true se a inserção foi bem-sucedida
    }
}
