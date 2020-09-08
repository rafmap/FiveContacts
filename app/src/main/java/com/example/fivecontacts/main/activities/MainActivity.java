package com.example.fivecontacts.main.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.User;

public class MainActivity extends AppCompatActivity {

    boolean primeiraVezUser = true;
    boolean primeiraVezSenha = true;
    EditText edUser;
    EditText edPass;
    Button btLogar;
    Button btNovo;
    //TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //se true, é porque o user ao cadastrar marcou em manter usuário logado, sem tela inicial
        if (montarObjetoSemUsuarioLogar()) {
            User user = montarObjetoUser();

            //Abrir a Tela de Contatos

            Intent intent = new Intent(this, ListaDeContatos_ListView.class);
            intent.putExtra("usuario", user);//passar esse parâmetro
            startActivity(intent);
            finish();
            //se o user escolheu se manter logado, esta tela vai checar isso, passar para a próxima rapidamente e se destruir
            //os componentes abaixo nem serão criados
        }

        btLogar = (Button) findViewById(R.id.btLogar);
        btNovo = (Button) findViewById(R.id.btNovo);
        edUser = (EditText) findViewById(R.id.edT_Login);
        edPass = (EditText) findViewById(R.id.edt_Pass);

        //Colocando Underline (Vamos usar esse campo mais na frente com o FireBase)
        //mTextView = (TextView) findViewById(R.id.tvEsqueceuSenha);
        //mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Evento de limpar Componente
        edUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezUser) {
                    edUser.setText("");
                    primeiraVezUser = false;
                }
                return false;
            }
        });

        //Evento de limpar Componente
        edPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezSenha) {
                    primeiraVezSenha = false;
                    edPass.setText("");
                    edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }
                return false;
            }
        });

        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Ao clicar deve-se:
                //1- Checar se existe um SharedPreferences
                //2- Comparar login e senha salvos
                //3- Se tudo der certo, resgatar lista de contatos
                //4- Abrir a Atividade lista de Contatos passando como parametro o objeto User e seus 5 Contatos

                SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                String loginSalvo = temUser.getString("login", "");
                String senhaSalva = temUser.getString("senha", "");
                Log.v("DEBUG", loginSalvo + " " + senhaSalva);
                if ((loginSalvo != null) && (senhaSalva != null)) {
                    //Recuperando da tela
                    String senha = edPass.getText().toString();
                    String login = edUser.getText().toString();

                    //Comparando
                    if ((loginSalvo.compareTo(login) == 0) && (senhaSalva.compareTo(senha) == 0)) {
                        User user = montarObjetoUser();
                        //Abrindo a Lista de Contatos
                        Intent intent = new Intent(MainActivity.this, ListaDeContatos_ListView.class);
                        intent.putExtra("usuario", user);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Login ou Senha Incorretos", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Login ou Senha nulos", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Novo Usuário
        btNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences cadastro = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                boolean temCadastro = cadastro.getBoolean("temCadastro", false);//retorna false como padrão se não achar a variável temCadastro
                if (temCadastro == false) {
                    Intent intent = new Intent(MainActivity.this, NovoUsuario.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Você já tem cadastro", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private User montarObjetoUser() {
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String nomeSalvo = temUser.getString("nome","");
        String loginSalvo = temUser.getString("login","");
        String senhaSalva = temUser.getString("senha","");
        String emailSalvo = temUser.getString("email","");

        User user = new User(nomeSalvo, loginSalvo, senhaSalva, emailSalvo);
        //montando um usuário padrão
        //User user = new User("Windson", "", "", "");
        return user;
    }

    private boolean montarObjetoSemUsuarioLogar() {
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        return temUser.getBoolean("manterLogado", false); //retorna false como padrão se não achar a variável manterLogado
    }

}