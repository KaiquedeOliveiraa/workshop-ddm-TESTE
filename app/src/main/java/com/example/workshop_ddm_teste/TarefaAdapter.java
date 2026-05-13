package com.example.workshop_ddm_teste;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class TarefaAdapter extends ArrayAdapter<Tarefa> {

    public interface OnAcaoListener {
        void onRemover(Tarefa tarefa);
        void onConcluir(Tarefa tarefa, boolean concluida);
    }

    private final OnAcaoListener listener;

    public TarefaAdapter(Context context, List<Tarefa> tarefas, OnAcaoListener listener) {
        super(context, 0, tarefas);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_tarefa, parent, false);
        }

        Tarefa      tarefa     = getItem(position);
        TextView    txtNome    = convertView.findViewById(R.id.txtNomeTarefa);
        TextView    txtData    = convertView.findViewById(R.id.txtDataHora);
        ImageButton btnConcluir = convertView.findViewById(R.id.btnConcluir);
        ImageButton btnRemover  = convertView.findViewById(R.id.btnRemover);

        txtNome.setText(tarefa.nome);

        // Exibe data/hora se existir
        if (tarefa.dataHora != null && !tarefa.dataHora.isEmpty()) {
            txtData.setText("🕐 " + tarefa.dataHora);
            txtData.setVisibility(View.VISIBLE);
        } else {
            txtData.setVisibility(View.GONE);
        }

        // Visual de concluída: risco no texto + círculo preenchido
        if (tarefa.concluida) {
            txtNome.setPaintFlags(txtNome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtNome.setTextColor(0xFF9CA3AF);
            btnConcluir.setImageResource(R.drawable.ic_circle_checked);
        } else {
            txtNome.setPaintFlags(txtNome.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            txtNome.setTextColor(0xFF1A1A2E);
            btnConcluir.setImageResource(R.drawable.ic_circle_outline);
        }

        btnConcluir.setOnClickListener(v ->
                listener.onConcluir(tarefa, !tarefa.concluida));

        btnRemover.setOnClickListener(v ->
                listener.onRemover(tarefa));

        return convertView;
    }
}