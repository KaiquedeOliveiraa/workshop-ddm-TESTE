package com.example.workshop_ddm_teste;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String COLECAO = "tarefas";

    public interface OnListListener {
        void onSuccess(List<Tarefa> itens);
        void onError(String erro);
    }

    public interface OnItemListener {
        void onSuccess();
        void onError(String erro);
    }

    public void buscarTodos(OnListListener listener) {
        db.collection(COLECAO)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Tarefa> lista = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String  id        = doc.getId();
                            String  nome      = doc.getString("nome");
                            Boolean concluida = doc.getBoolean("concluida");
                            String  dataHora  = doc.getString("dataHora");
                            if (nome != null) {
                                lista.add(new Tarefa(
                                        id,
                                        nome,
                                        Boolean.TRUE.equals(concluida),
                                        dataHora != null ? dataHora : ""
                                ));
                            }
                        }
                        listener.onSuccess(lista);
                    } else {
                        listener.onError("Erro ao carregar a lista.");
                    }
                });
    }

    public void adicionar(String nome, String dataHora, OnItemListener listener) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome",      nome);
        dados.put("concluida", false);
        dados.put("dataHora",  dataHora != null ? dataHora : "");

        db.collection(COLECAO)
                .add(dados)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) listener.onSuccess();
                    else listener.onError("Não foi possível adicionar o item.");
                });
    }

    public void remover(String id, OnItemListener listener) {
        db.collection(COLECAO).document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) listener.onSuccess();
                    else listener.onError("Não foi possível remover o item.");
                });
    }

    public void alternarConcluida(String id, boolean novoEstado, OnItemListener listener) {
        db.collection(COLECAO).document(id)
                .update("concluida", novoEstado)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) listener.onSuccess();
                    else listener.onError("Não foi possível atualizar o item.");
                });
    }
}