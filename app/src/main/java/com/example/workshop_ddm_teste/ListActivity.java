package com.example.workshop_ddm_teste;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {

    private TarefaAdapter adapterPendentes;
    private TarefaAdapter adapterConcluidas;
    private ArrayList<Tarefa> dadosPendentes  = new ArrayList<>();
    private ArrayList<Tarefa> dadosConcluidas = new ArrayList<>();
    private ItemViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Views
        ProgressBar   progressBar    = findViewById(R.id.progressBar);
        ListView      listPendentes  = findViewById(R.id.listViewPendentes);
        ListView      listConcluidas = findViewById(R.id.listViewConcluidas);
        MaterialButton btnNova       = findViewById(R.id.btnNovaTarefa);

        // Data de hoje no header
        android.widget.TextView txtDataHoje = findViewById(R.id.txtDataHoje);
        Calendar cal = Calendar.getInstance();
        String[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        String[] dias  = {"Domingo","Segunda","Terça","Quarta",
                "Quinta","Sexta","Sábado"};
        txtDataHoje.setText(dias[cal.get(Calendar.DAY_OF_WEEK) - 1] + ", " +
                cal.get(Calendar.DAY_OF_MONTH) + " de " +
                meses[cal.get(Calendar.MONTH)]);

        // Listener compartilhado
        TarefaAdapter.OnAcaoListener acaoListener = new TarefaAdapter.OnAcaoListener() {
            @Override
            public void onRemover(Tarefa tarefa) {
                new AlertDialog.Builder(ListActivity.this)
                        .setTitle("Remover tarefa")
                        .setMessage("Deseja remover \"" + tarefa.nome + "\"?")
                        .setPositiveButton("Remover", (d, w) -> vm.removerItem(tarefa.id))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }

            @Override
            public void onConcluir(Tarefa tarefa, boolean concluida) {
                vm.alternarConcluida(tarefa.id, concluida);
            }
        };

        adapterPendentes  = new TarefaAdapter(this, dadosPendentes,  acaoListener);
        adapterConcluidas = new TarefaAdapter(this, dadosConcluidas, acaoListener);
        listPendentes.setAdapter(adapterPendentes);
        listConcluidas.setAdapter(adapterConcluidas);

        vm = new ViewModelProvider(this).get(ItemViewModel.class);

        vm.lista.observe(this, itens -> {
            dadosPendentes.clear();
            dadosConcluidas.clear();
            for (Tarefa t : itens) {
                if (t.concluida) dadosConcluidas.add(t);
                else             dadosPendentes.add(t);
            }
            adapterPendentes.notifyDataSetChanged();
            adapterConcluidas.notifyDataSetChanged();
        });

        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        vm.carregando.observe(this, carregando ->
                progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE));

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