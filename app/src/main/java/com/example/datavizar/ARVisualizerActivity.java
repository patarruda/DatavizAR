package com.example.datavizar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARVisualizerActivity extends AppCompatActivity implements
        BaseArFragment.OnTapArPlaneListener {

    private ArFragment arFragment;

    // modelo de cubo criado para renderização
    private Renderable model;

    //gambiarra para criar cubos de diferentes tamanhos - testes
    private float[] listaY = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.5f,
                              2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f};
    private int indiceY = 0;
    private float alturaMaxima = 2.5f;
    private float larguraMaxima = 0.5f;
    private float larguraMinima = 0.05f;

    private ArrayList<DataModel> dataSlice;
    private double valorMaximo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setar dados selecionados pelo usuário
        this.dataSlice = getIntent().getParcelableArrayListExtra("dadosSelecionados");
            // dataSlice está em ordem crescente de valores
        this.valorMaximo = Math.max(dataSlice.get(dataSlice.size() - 1).getValor(),
                            Math.abs(dataSlice.get(0).getValor()));

        setContentView(R.layout.activity_ar_visualizer);
        getSupportFragmentManager().addFragmentOnAttachListener((fragmentManager, fragment) -> {
            if (fragment.getId() == R.id.arFragment) {
                arFragment = (ArFragment) fragment;
                arFragment.setOnTapArPlaneListener(ARVisualizerActivity.this);
            }
        });

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }

        Toast t = Toast.makeText(this, "toque nos locais desejados para exibir os dados...", Toast.LENGTH_LONG);
        t.setGravity(Gravity.TOP, 0, 0);
        t.show();

    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

        //if (indiceY < listaY.length) {
        if (indiceY < dataSlice.size()) {
            DataModel item = dataSlice.get(indiceY);
            Vector3 vetorSize = calcularVetor(item.getValor());

            //criarCubo(0.1f, listaY[indiceY], 0.1f).thenAccept(model -> {
            //criarCubo(0.1f, calcularAlturaY(dataSlice.get(indiceY).getValor()), 0.1f).thenAccept(model -> {
            criarCubo(vetorSize).thenAccept(model -> {
                Anchor anchor = hitResult.createAnchor();

                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                // Create the transformable model and add it to the anchor.
                TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
                modelNode.setParent(anchorNode);
                RenderableInstance modelInstance = modelNode.setRenderable(this.model);

                modelNode.select();

                Node labelNode;
                try {
                    labelNode = criarLabel(item.getNome() + "\n" +
                                            item.getValor(), vetorSize);
                    labelNode.setParent(anchorNode);

                } catch (Exception e) {
                    Toast.makeText(this, "Ocorreu um erro...", Toast.LENGTH_SHORT).show();
                }
                indiceY++;
            });

        } else {
            Toast.makeText(this, "Não há mais dados...", Toast.LENGTH_SHORT).show();
        }


    }

    public Node criarLabel(String nome, Vector3 vetorCubo) {
        Node textViewNode = new Node();
        // posiciona label em frente ao cubo, próximo à base
        textViewNode.setLocalPosition(new Vector3(0.0f, 0.0f, vetorCubo.z + 0.2f));
        textViewNode.setLocalRotation(Quaternion.eulerAngles(new Vector3(-90f, 0f, 0f)));

        //Toast.makeText(this, "iniciando ViewRenderable.builder()", Toast.LENGTH_LONG).show();

        ViewRenderable.builder()
                .setView(this, R.layout.ar_label_layout)
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.setShadowCaster(false);
                    TextView textView = viewRenderable.getView().findViewById(R.id.arLabel);
                    textView.setText(nome);
                    textViewNode.setRenderable(viewRenderable);
                });

        //Toast.makeText(this, "finalizado ViewRenderable.builder()", Toast.LENGTH_LONG).show();

        return textViewNode;
    }

    public CompletableFuture<Void> criarCubo(Vector3 vSize) {
        Color cor;
        if (vSize.y < 0) {
            // vermelho
            cor = new Color(1, 0, 0, 1);
            vSize.y = - (vSize.y);
        } else {
            // azul
            cor = new Color(0, 0, 1, 1);
        }

        return MaterialFactory.makeOpaqueWithColor(this, cor)
                .thenAccept(
                        material -> {
                            model = ShapeFactory.makeCube(
                                    vSize,
                                    new Vector3(0, (vSize.y / 2), 0),
                                    material
                            );
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Erro ao criar cubo...", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

    }

    private Vector3 calcularVetor(double valor) {
        float alturaY = (alturaMaxima * (float) valor) / (float) valorMaximo;
        float larguraX;
        if (alturaY == 0) {
            larguraX = 0;
        } else {
            larguraX = Math.max(larguraMinima,
                                (larguraMaxima * Math.abs(alturaY)) / alturaMaxima);
        }
        float profundidadeZ = larguraX;
        return new Vector3(larguraX, alturaY, profundidadeZ);

    }

    public void onBackPressed() {
        Intent intent = new Intent(ARVisualizerActivity.this, CarregarDadosActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


}
