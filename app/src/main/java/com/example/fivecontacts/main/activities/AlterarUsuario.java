package com.example.fivecontacts.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AlterarUsuario extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    //FUNCIONANDO PERFEITAMENTE ATÉ INSERIR A LINHA DO getSerializable DO PICK_CONTACTS - geralmente é user null
    boolean primeiraVezNome = true;
    boolean primeiraVezUser = true;
    boolean primeiraVezSenha = true;
    boolean primeiraVezEmail = true;
    EditText edUser, edPass, edNome, edEmail;
    Button btAlterar;
    Switch swLogado;

    BottomNavigationView bnv;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_usuario);

        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvPerfil);//deixa selecionado o ícone de Perfil
        Log.v("PDMv2", "alterar");
        btAlterar = (Button) findViewById(R.id.btCriar);
        edUser = (EditText) findViewById(R.id.edT_Login2);
        edPass = (EditText) findViewById(R.id.edt_Pass2);
        edNome = (EditText) findViewById(R.id.edtNome);
        edEmail = (EditText) findViewById(R.id.edEmail);
        swLogado = (Switch) findViewById(R.id.swLogado);

        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String nomeSalvo = temUser.getString("nome", "");
        String loginSalvo = temUser.getString("login", "");
        String senhaSalva = temUser.getString("senha", "");
        String emailSalvo = temUser.getString("email", "");
        boolean manterLogadoSalvo = temUser.getBoolean("manterLogado", false);
        //Tudo preenchido para ser alterado
        edNome.setText(nomeSalvo);
        edUser.setText(loginSalvo);
        edPass.setText(senhaSalva);
        edEmail.setText(emailSalvo);
        swLogado.setChecked(manterLogadoSalvo);

        btAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Evento do Botão Alterar
                String nome, login, senha, email;
                boolean manterLogado;
                nome = edNome.getText().toString();
                login = edUser.getText().toString();
                senha = edPass.getText().toString();
                email = edEmail.getText().toString();
                manterLogado = swLogado.isChecked();

                SharedPreferences salvaUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                SharedPreferences.Editor escritor = salvaUser.edit();

                escritor.putString("nome", nome);
                escritor.putString("senha", senha);
                escritor.putString("login", login);
                escritor.putString("email", email);
                escritor.putBoolean("manterLogado", manterLogado);

                escritor.commit(); //Salva em Disco

                Toast.makeText(AlterarUsuario.this, "Usuário alterado com sucesso", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AlterarUsuario.this, ListaDeContatos_ListView.class);
                startActivity(intent);//se comentasse esta linha de código, o usuário deslogaria se alterasse seu cadastro para não se manter logado
                //assim, ao alterar essa opção, a aplicação só requererá login quando for aberta da próxima vez

                Log.v("PDMv2", "Passei do StartActivity");//Matando a Activity atual ao passar para a Pick_Contacts
                finish();//mata a tela de alterar usuário
            }
        });

        //Dados da Intent anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuário
                user = (User) params.getSerializable("usuario");
                if (user != null) {
                    Log.v("pdm", user.getNome());
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //checa se o Item selecionado é o de Ligar
        if(item.getItemId() == R.id.anvLigar){
            //Intent intent = new Intent(this, ListaDeContatos_ListView.class);
            Toast.makeText(this, "Lista de contatos de emergência", Toast.LENGTH_LONG).show();
            Log.v("PDMv2", "Matou Alterar Usuário");
            finish();
        }
        //checa se o Item selecionado é o de Mudar Contatos
        if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Mudar Contatos
            Intent intent = new Intent(this, Pick_Contacts.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            startActivityForResult(intent, 1112);//abre com código 1112 (Mudar Contatos) uma Activity filha da qual se espera dados
            Toast.makeText(this, "Modificar os 5 contatos de emergência", Toast.LENGTH_LONG).show();
            Log.v("PDMv2", "Matou Alterar Usuário");
            finish();
        }
        //Log.v("PDMv2", "Em direção à morte de Alterar Usuário");
        //finish();
        return true;//TROCADO DE FALSE
    }
}