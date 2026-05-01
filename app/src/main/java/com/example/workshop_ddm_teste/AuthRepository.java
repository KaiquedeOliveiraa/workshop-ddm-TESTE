package com.example.workshop_ddm_teste;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public interface OnAuthListener {
        void onSuccess(FirebaseUser user);
        void onError(String erro);
    }

    public void autenticar(String email, String senha, boolean novo, OnAuthListener listener) {
        if (novo) {
            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onSuccess(mAuth.getCurrentUser());
                } else {
                    // Pega a mensagem do Firebase
                    String msg = task.getException().getMessage();
                    // Se for erro de formato, avisa. Se não, diz que o cadastro falhou.
                    if (msg.contains("badly formatted")) {
                        listener.onError("E-mail com formato incorreto!");
                    } else {
                        listener.onError("Não foi possível cadastrar.");
                    }
                }
            });
        } else {
            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onSuccess(mAuth.getCurrentUser());
                } else {
                    listener.onError("E-mail ou senha incorretos.");
                }
            });
        }
    }
}