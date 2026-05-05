package com.example.workshop_ddm_teste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        ListView listView       = findViewById(R.id.listView);
        Button   btnNovaTarefa  = findViewById(R.id.btnNovaTarefa);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dados);
        listView.setAdapter(adapter);

        ItemViewModel vm = new ViewModelProvider(this).get(ItemViewModel.class);

        vm.lista.observe(this, itens -> {
            dados.clear();
            dados.addAll(itens);
            adapter.notifyDataSetChanged();
        });

        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        vm.carregando.observe(this, isCarregando ->
                progressBar.setVisibility(isCarregando ? View.VISIBLE : View.GONE));

        // Navega para a tela de adicionar tarefa
        btnNovaTarefa.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));

        vm.carregarLista();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega a lista ao voltar da AddTaskActivity
        ItemViewModel vm = new ViewModelProvider(this).get(ItemViewModel.class);
        vm.carregarLista();
    }
}