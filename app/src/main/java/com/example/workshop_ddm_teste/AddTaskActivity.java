package com.example.workshop_ddm_teste;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class AddTaskActivity extends AppCompatActivity {

    private static final String CHANNEL_ID     = "tarefas_channel";
    private static final int    NOTIFICATION_ID = 1;

    private ActivityResultLauncher<String> pedirPermissaoLauncher;
    private String tarefaPendente = null;
    private boolean salvouComSucesso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        criarCanalDeNotificacao();

        EditText    edTarefa  = findViewById(R.id.edTarefa);
        Button      btnSalvar = findViewById(R.id.btnSalvar);
        ProgressBar progress  = findViewById(R.id.progressBar);

        ItemViewModel vm = new ViewModelProvider(this).get(ItemViewModel.class);

        // Registra o pedido de permissão de notificação
        pedirPermissaoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedida -> {
                    if (concedida) {
                        dispararNotificacao(tarefaPendente);
                    } else {
                        Toast.makeText(this,
                                "Permissão negada. Tarefa salva sem notificação.",
                                Toast.LENGTH_SHORT).show();
                    }
                    finish(); // volta para ListActivity em qualquer caso
                }
        );

        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        vm.carregando.observe(this, isCarregando ->
                progress.setVisibility(isCarregando ? View.VISIBLE : View.GONE));

        // Quando a lista atualiza, a tarefa foi salva com sucesso no Firestore
        vm.lista.observe(this, itens -> {
            if (tarefaPendente != null && !salvouComSucesso) {
                salvouComSucesso = true;
                verificarPermissaoENotificar(tarefaPendente);
            }
        });

        btnSalvar.setOnClickListener(v -> {
            String nome = edTarefa.getText().toString().trim();
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite o nome da tarefa!", Toast.LENGTH_SHORT).show();
                return;
            }
            tarefaPendente   = nome;
            salvouComSucesso = false;
            vm.adicionarItem(nome);
        });
    }

    private void verificarPermissaoENotificar(String nomeTarefa) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean temPermissao = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;

            if (temPermissao) {
                dispararNotificacao(nomeTarefa);
                finish();
            } else {
                // O launcher vai chamar finish() após o usuário responder
                pedirPermissaoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            dispararNotificacao(nomeTarefa);
            finish();
        }
    }

    private void criarCanalDeNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Tarefas",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            canal.setDescription("Notificações de tarefas adicionadas");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    private void dispararNotificacao(String nomeTarefa) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("✅ Tarefa adicionada!")
                .setContentText("\"" + nomeTarefa + "\" foi salva com sucesso.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}