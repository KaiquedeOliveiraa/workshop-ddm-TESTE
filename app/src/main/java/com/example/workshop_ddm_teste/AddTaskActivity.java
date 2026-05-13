package com.example.workshop_ddm_teste;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private static final String CHANNEL_ID     = "tarefas_channel";
    private static final int    NOTIFICATION_ID = 1;

    private ActivityResultLauncher<String> pedirPermissaoLauncher;
    private String  tarefaPendente   = null;
    private boolean salvouComSucesso = false;

    private int  diaSelecionado, mesSelecionado, anoSelecionado;
    private int  horaSelecionada, minutoSelecionado;
    private boolean dataSelecionada     = false;
    private boolean horaSelecionadaFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        criarCanalDeNotificacao();

        EditText       edTarefa  = findViewById(R.id.edTarefa);
        EditText       edData    = findViewById(R.id.edData);
        EditText       edHora    = findViewById(R.id.edHora);
        MaterialButton btnSalvar = findViewById(R.id.btnSalvar);
        ProgressBar    progress  = findViewById(R.id.progressBar);

        ItemViewModel vm = new ViewModelProvider(this).get(ItemViewModel.class);

        // Abre o DatePickerDialog ao clicar no campo data
        edData.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, ano, mes, dia) -> {
                diaSelecionado  = dia;
                mesSelecionado  = mes + 1;
                anoSelecionado  = ano;
                dataSelecionada = true;
                edData.setText(String.format("%02d/%02d/%04d", dia, mes + 1, ano));
            },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Abre o TimePickerDialog ao clicar no campo hora
        edHora.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hora, minuto) -> {
                horaSelecionada     = hora;
                minutoSelecionado   = minuto;
                horaSelecionadaFlag = true;
                edHora.setText(String.format("%02d:%02d", hora, minuto));
            },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true).show();
        });

        pedirPermissaoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedida -> {
                    if (concedida) dispararNotificacao(tarefaPendente);
                    else Toast.makeText(this,
                            "Permissão negada. Tarefa salva sem notificação.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });

        vm.mensagem.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        vm.carregando.observe(this, isCarregando ->
                progress.setVisibility(isCarregando ? View.VISIBLE : View.GONE));

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

            // Monta dataHora conforme o que foi preenchido
            String dataHora = "";
            if (dataSelecionada && horaSelecionadaFlag) {
                dataHora = String.format("%02d/%02d/%04d %02d:%02d",
                        diaSelecionado, mesSelecionado, anoSelecionado,
                        horaSelecionada, minutoSelecionado);
            } else if (dataSelecionada) {
                dataHora = String.format("%02d/%02d/%04d",
                        diaSelecionado, mesSelecionado, anoSelecionado);
            } else if (horaSelecionadaFlag) {
                dataHora = String.format("%02d:%02d",
                        horaSelecionada, minutoSelecionado);
            }

            tarefaPendente   = nome;
            salvouComSucesso = false;
            vm.adicionarItem(nome, dataHora);
        });
    }

    private void verificarPermissaoENotificar(String nomeTarefa) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean temPermissao = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
            if (temPermissao) {
                dispararNotificacao(nomeTarefa);
                finish();
            } else {
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
                    CHANNEL_ID, "Tarefas", NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription("Notificações de tarefas adicionadas");
            getSystemService(NotificationManager.class).createNotificationChannel(canal);
        }
    }

    private void dispararNotificacao(String nomeTarefa) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("✅ Tarefa adicionada!")
                .setContentText("\"" + nomeTarefa + "\" foi salva com sucesso.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, builder.build());
    }
}