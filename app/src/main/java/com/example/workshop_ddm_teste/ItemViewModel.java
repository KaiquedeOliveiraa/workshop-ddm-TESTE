package com.example.workshop_ddm_teste;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

//Faz a ponte entre a tela (ListActivity) e os dados (ItemRepository).
public class ItemViewModel extends ViewModel {

    private final ItemRepository repository = new ItemRepository();
    public MutableLiveData<List<String>>lista = new MutableLiveData<>();
    public MutableLiveData<String>mensagem = new MutableLiveData<>();
    public MutableLiveData<Boolean>carregando = new MutableLiveData<>();

    //Solicita ao repositório a lista de tarefas do Firestore.

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

    //valida e envia um novo item para o repositório salvar no Firestore.

    public void adicionarItem(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            mensagem.setValue("Digite um nome para o item.");
            return;
        }

        repository.adicionar(nome.trim(), new ItemRepository.OnItemListener() {
            @Override
            public void onSuccess() {
                carregarLista();
            }

            @Override
            public void onError(String erro) {
                mensagem.setValue(erro);
            }
        });
    }
}