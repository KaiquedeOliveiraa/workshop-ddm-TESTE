package com.example.workshop_ddm_teste;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthViewModel vm = new ViewModelProvider(this).get(AuthViewModel.class);

        EditText edEmail = findViewById(R.id.edEmail);
        EditText edSenha = findViewById(R.id.edSenha);

        // Se o ViewModel enviar mensagem, mostramos o Pop-up
        vm.mensagem.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        // Se logar, avisa no log (ou muda de tela na Parte 2)
        vm.logado.observe(this, ok -> {
            if (ok) Toast.makeText(this, "Logado!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnEntrar).setOnClickListener(v ->
                vm.executar(edEmail.getText().toString(), edSenha.getText().toString(), false));

        findViewById(R.id.btnCadastrar).setOnClickListener(v ->
                vm.executar(edEmail.getText().toString(), edSenha.getText().toString(), true));
    }
}