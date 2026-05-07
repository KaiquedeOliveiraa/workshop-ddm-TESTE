package com.example.workshop_ddm_teste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

/**
 * Fluxo:
 *   ListActivity - solicita ao ViewModel
 *   ViewModel - chama o Repository
 *   Repository - busca no Firestore e responde via callback
 *   ViewModel - atualiza o LiveData
 *   ListActivity - é notificada automaticamente e atualiza a tela
 */
public class ListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> dados = new ArrayList<>();
    private ItemViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.listView);
        Button btnNova = findViewById(R.id.btnNovaTarefa);
        ProgressBar progressBar2 = findViewById(R.id.progressBar);

        // Adapter padrão do Android: vincula a lista "dados" ao ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dados);
        listView.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(ItemViewModel.class);

        // Observa a lista de tarefas sempre que o ViewModel publicar novos dados
        vm.lista.observe(this, itens -> {
            dados.clear();
            dados.addAll(itens);
            adapter.notifyDataSetChanged(); //
        });

        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        vm.carregando.observe(this, estaCarregando -> {
            progressBar2.setVisibility(estaCarregando ? View.VISIBLE : View.GONE);
        });

        // abre a tela de adicionar nova tarefa
        btnNova.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));

        vm.carregarLista();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vm != null) vm.carregarLista();
    }
}