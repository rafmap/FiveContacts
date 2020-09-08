package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ListaDeContatos_ListView extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener, BottomNavigationView.OnNavigationItemSelectedListener {

    ListView lv;
    BottomNavigationView bnv;
    User user;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);
        bnv = (BottomNavigationView) findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);

        lv = (ListView) findViewById(R.id.listView1);
        preencherListaDeContatos(); //Montagem do ListView

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

    protected void preencherListaDeContatos() {//Preenche a lista dos 5 contatos de emergência
        SharedPreferences recuperarContatos = getSharedPreferences("contatos2", Activity.MODE_PRIVATE);
        int num = recuperarContatos.getInt("numContatos", 0);
        final ArrayList<Contato> contatos = new ArrayList<Contato>();//final = variável constante, para ser vista nas inner classes
        Contato contato;
        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");//pega cada contato do array de contatos salvos no editor
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    contato = (Contato) ois.readObject();//contato recuperado
                    if (contato != null) {
                        contatos.add(contato);
                        Log.v("PDM", contato.getNome());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (contatos != null) {
            final String[] nomesSP;
            nomesSP = new String[contatos.size()];
            for (int i = 0; i < contatos.size(); i++) {
                nomesSP[i] = contatos.get(i).getNome();
            }

            lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesSP){
                @Override//muda o estilo do texto da ListView
                public View getView(int position, View convertView, ViewGroup parent){
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextSize(24);
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setPadding(30,90,30,90);
                    return textView;
                }
            });
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//O DENIED VOCÊ JÁ TEM POR PADRÃO
                    uri = Uri.parse(contatos.get(i).getNumero());
                    if (checarPermissaoPhone_SMD()) {//se a permissão foi concedida, a ligação será feita
                        //Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);//só disca
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);//realiza a ligação -- esta linha não pode ser chamada sem a permissão
                        startActivity(itLigar);
                    }

                }
            });
        }
    }

    //Método da Interface UIEducacionalPermissao.NoticeDialogListener
    //Método responsável por abrir a janela de conceder permissão de ligar para contato
    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo == 1) {
            String[] permissions = {Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);
        }
        if (codigo == 2) {
            Intent itDiscar = new Intent(Intent.ACTION_DIAL, uri);//só disca
            startActivity(itDiscar);
        }
        Log.v("SMD", "Clicou no OK");
    }

    //Método chamado quando a janela de conceder permissão de ligar para um contato é respondida
    //Serve para mostrar o feedback da resposta na tela
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2222://CÓDIGO DE RETORNO DA JANELA DE PERMISSÃO DE LIGAR PARA CONTATO
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão concedida", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show();
                    //Mensagem que será mostrada na UIEducacional quando a permissão para ligar para um contato é negada
                    String mensagem = "A ligação só pode ser feita automaticamente se a permissão para ligar for concedida.";
                    String titulo = "Por que precisamos desta permissão?";//Título da UIEducacional em questão
                    //Monta uma UIEducacional após a janela de permissão ser fechada com resposta negativa
                    UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, 2);
                    mensagemPermissao.onAttach((Context) this);
                    mensagemPermissao.show(getSupportFragmentManager(), "segundavez");// contém onDialogPositiveClick
                }
                break;
        }
    }

    //Método que checa se a permissão para ligar para um contato já foi concedida
    //Retorna true se sim e false se não
    protected boolean checarPermissaoPhone_SMD() {
        //MÉTODO QUE CHECA SE PERMISSÃO JÁ ESTÁ CONCEDIDA
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("SMD", "Tenho permissão CALL_PHONE");
            return true;//PERMISSÃO JÁ CONCEDIDA
        } else {//PERMISSÃO NÃO ESTÁ CONCEDIDA
            Log.v("SMD", "Não tenho permissão CALL_PHONE");
            String mensagem = "Este aplicativo precisa acessar o telefone para discagem automática. Uma janela de permissão aparecerá.";
            String titulo = "Permissão de acesso a chamadas";
            int codigo = 1;//CÓDIGO DO DIÁLOGO DE CONCEDER PERMISSÃO
            //Monta uma UIEducacional antes de abrir a janela de permissão
            UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
            mensagemPermissao.onAttach((Context) this);//passa uma referência para essa classe; essa classe pede uma instância de Context
            mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");
        }
        return false;
    }

    //Abre uma nova Activity dependendo do Item selecionado na BottomNavigationBar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //checa se o Item selecionado é o de Alterar Usuário
        if (item.getItemId() == R.id.anvPerfil) {
            //abre a tela de Alterar Usuário
            Intent intent = new Intent(this, AlterarUsuario.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            startActivityForResult(intent, 1111);//abre com código 1111 (Alterar Usuário) uma Activity filha da qual se espera dados
            Toast.makeText(this, "Alterar seus dados", Toast.LENGTH_LONG).show();
        }
        //checa se o Item selecionado é o de Mudar Contatos
        if (item.getItemId() == R.id.anvMudar) {
            //abre a tela de Mudar Contatos
            Intent intent = new Intent(this, Pick_Contacts.class);
            intent.putExtra("usuario", user);//envia instância do user logado para a Activity filha chamada
            startActivityForResult(intent, 1112);//abre com código 1112 (Mudar Contatos) uma Activity filha da qual se espera dados
            Toast.makeText(this, "Modificar os 5 contatos de emergência", Toast.LENGTH_LONG).show();
        }
        return true;//TROCADO DE FALSE
    }

    //Método callback que lida com os dados recebidos da Activity filha quando ela se fecha
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //caso seja um Voltar ou Sucesso, selecionar o item Ligar
        if (requestCode == 1111) {//código de retorno de Alterar Usuário
            bnv.setSelectedItemId(R.id.anvLigar);//deixa o botãozinho de Ligar selecionado
        }
        if (requestCode == 1112) {//código de retorno de Mudar Contatos
            bnv.setSelectedItemId(R.id.anvLigar);//deixa o botãozinho de Ligar selecionado
            preencherListaDeContatos();//atualiza a Lista de Contatos
        }
    }

}