package com.example.workshop_ddm_teste;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ItemViewModel extends ViewModel {

    private final ItemRepository repository = new ItemRepository();

    public MutableLiveData<List<String>> lista   = new MutableLiveData<>();
    public MutableLiveData<String>       mensagem = new MutableLiveData<>();
    public MutableLiveData<Boolean>      carregando = new MutableLiveData<>();

    /** Carrega os itens do Firestore e publica no LiveData. */
    public void carregarLista() {
        carregando.setValue(true);
        repository.buscarTodos(new ItemRepository.OnListListener() {
            @Override
            public void onSuccess(List<String> itens) {
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

    /** Adiciona um item e recarrega a lista em seguida. */
    public void adicionarItem(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            mensagem.setValue("Digite um nome para o item.");
            return;
        }
        repository.adicionar(nome.trim(), new ItemRepository.OnItemListener() {
            @Override
            public void onSuccess() {
                carregarLista(); // atualiza a lista automaticamente
            }

            @Override
            public void onError(String erro) {
                mensagem.setValue(erro);
            }
        });
    }
}