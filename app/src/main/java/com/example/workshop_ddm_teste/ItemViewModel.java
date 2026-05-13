package com.example.workshop_ddm_teste;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ItemViewModel extends ViewModel {

    private final ItemRepository repository = new ItemRepository();

    public MutableLiveData<List<Tarefa>> lista      = new MutableLiveData<>();
    public MutableLiveData<String>       mensagem   = new MutableLiveData<>();
    public MutableLiveData<Boolean>      carregando = new MutableLiveData<>();

    public void carregarLista() {
        carregando.setValue(true);
        repository.buscarTodos(new ItemRepository.OnListListener() {
            @Override
            public void onSuccess(List<Tarefa> itens) {
                carregando.setValue(false);
                lista.setValue(itens);
            }
            @Override
            public void onError(String erro) {
                carregando.setValue(false);
                mensagem.setValue(erro);
            }
        });
    }

    public void adicionarItem(String nome, String dataHora) {
        if (nome == null || nome.trim().isEmpty()) {
            mensagem.setValue("Digite um nome para o item.");
            return;
        }
        repository.adicionar(nome.trim(), dataHora, new ItemRepository.OnItemListener() {
            @Override public void onSuccess() { carregarLista(); }
            @Override public void onError(String erro) { mensagem.setValue(erro); }
        });
    }

    public void removerItem(String id) {
        repository.remover(id, new ItemRepository.OnItemListener() {
            @Override public void onSuccess() { carregarLista(); }
            @Override public void onError(String erro) { mensagem.setValue(erro); }
        });
    }

    public void alternarConcluida(String id, boolean novoEstado) {
        repository.alternarConcluida(id, novoEstado, new ItemRepository.OnItemListener() {
            @Override public void onSuccess() { carregarLista(); }
            @Override public void onError(String erro) { mensagem.setValue(erro); }
        });
    }
}