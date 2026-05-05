package com.example.workshop_ddm_teste;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private AuthRepository repository = new AuthRepository();
    public MutableLiveData<String> mensagem = new MutableLiveData<>();
    public MutableLiveData<Boolean> logado = new MutableLiveData<>();

    public void executar(String e, String s, boolean n) {
        repository.autenticar(e, s, n, new AuthRepository.OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                logado.setValue(true);
            }

            @Override
            public void onError(String erro) {
                mensagem.setValue(erro);
            }
        });
    }
}