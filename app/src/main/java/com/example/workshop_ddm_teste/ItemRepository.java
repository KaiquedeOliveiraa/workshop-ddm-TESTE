package com.example.workshop_ddm_teste;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String COLECAO = "tarefas";

    public interface OnListListener {
        void onSuccess(List<String> itens);
        void onError(String erro);
    }

    public interface OnItemListener {
        void onSuccess();
        void onError(String erro);
    }

     //Busca os documentos da coleção no Firestore.
    public void buscarTodos(OnListListener listener) {
        db.collection(COLECAO)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> lista = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String nome = doc.getString("nome"); //
                            if (nome != null) lista.add(nome);
                        }

                        listener.onSuccess(lista);
                    } else {
                        listener.onError("Erro ao carregar a lista.");
                    }
                });
    }

     //Adiciona um novo documento na coleção "tarefas".
    public void adicionar(String nome, OnItemListener listener) {
        Map<String, Object> dados = new java.util.HashMap<>();
        dados.put("nome", nome);

        db.collection(COLECAO)
                .add(dados)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onError("Não foi possível adicionar o item.");
                    }
                });
    }
}