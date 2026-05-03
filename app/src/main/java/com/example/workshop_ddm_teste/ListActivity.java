package com.example.workshop_ddm_teste;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> dados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Views
        ListView listView     = findViewById(R.id.listView);
        EditText edNome       = findViewById(R.id.edNome);
        Button   btnAdicionar = findViewById(R.id.btnAdicionar);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Adapter conectado à lista local
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dados);
        listView.setAdapter(adapter);

        // ViewModel
        ItemViewModel vm = new ViewModelProvider(this).get(ItemViewModel.class);

        // Observa a lista de itens
        vm.lista.observe(this, itens -> {
            dados.clear();
            dados.addAll(itens);
            adapter.notifyDataSetChanged();
        });

        // Observa mensagens de erro / feedback
        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        // Observa o estado de carregamento
        vm.carregando.observe(this, isCarregando ->
                progressBar.setVisibility(isCarregando ? View.VISIBLE : View.GONE));

        // Botão adicionar
        btnAdicionar.setOnClickListener(v -> {
            vm.adicionarItem(edNome.getText().toString());
            edNome.setText("");
        });

        // Carrega a lista ao abrir a tela
        vm.carregarLista();
    }
}