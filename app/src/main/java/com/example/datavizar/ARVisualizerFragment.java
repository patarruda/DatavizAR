package com.example.datavizar;

/**
NÃO UTILIZADA no app

Após testes, vimos que o app fica lento e trava o celular quando
 utilizamos o Fragment ARVisualizerFragment

 Como solução, estamos utilizando a Activity ARVisualizerActivity
 */

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ARVisualizerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ARVisualizerFragment extends Fragment implements
        BaseArFragment.OnTapArPlaneListener {

    private ArFragment arFragment;

    // modelo de cubo criado para renderização
    private Renderable model;

    //gambiarra para criar cubos de diferentes tamanhos
    //private float[] listaY = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.5f};
    private float[] listaY = {0.1f, 0.5f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
    private int indiceY = 0;

    // Dados reais
    private List<DataModel> dataSlice;
    private int selectedModelIndex;

    public ARVisualizerFragment() {
        // Required empty public constructor
    }

    public static ARVisualizerFragment newInstance(String param1, String param2) {
        ARVisualizerFragment fragment = new ARVisualizerFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar_visualizer, container, false);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener(this);

        if (Sceneform.isSupported(requireContext())) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.arFragment, ArFragment.class, null)
                    .commit();
        }

        //criarCubo(0.1f, 0.05f, 0.1f);

        return view;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        //gambiarra para riar cubos de diferentes tamanhos
        float tapPositionY = hitResult.getHitPose().ty();
        //Toast.makeText(getContext(), "tapPositionY = " + tapPositionY, Toast.LENGTH_LONG).show();

        String nome = "teste";
        if (indiceY < listaY.length) {
            float altura = listaY[indiceY];
            nome = String.valueOf(altura);
            criarCubo(0.1f, listaY[indiceY],0.1f);
            indiceY++;

        }

        if (model == null) { //|| texture == null) {
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
            return;
        }



        // Create the Anchor.

        Anchor anchor = hitResult.createAnchor();

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable model and add it to the anchor.
        TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
        modelNode.setParent(anchorNode);
        RenderableInstance modelInstance = modelNode.setRenderable(this.model);

        //    modelInstance.getMaterial().setInt("baseColorIndex", 0);
        //    modelInstance.getMaterial().setTexture("baseColorMap", texture);

        modelNode.select();
        Node labelNode;
        try {
            labelNode = criarLabel(nome);
            labelNode.setParent(anchorNode);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro", Toast.LENGTH_LONG).show();
        }

    }

    public Node criarLabel(String nome) throws ExecutionException, InterruptedException {
        Node textViewNode = new Node();
        textViewNode.setLocalPosition(new Vector3(0.0f, 0.1f, 0.1f)); // Offset the text view in front of the cube

        Toast.makeText(getContext(), "iniciando ViewRenderable.builder()", Toast.LENGTH_LONG).show();

        ViewRenderable.builder()
                .setView(getContext(), R.layout.ar_label_layout)
                .build()
                .thenAccept(viewRenderable -> {
                    viewRenderable.setShadowCaster(false);
                    TextView textView = viewRenderable.getView().findViewById(R.id.arLabel);
                    textView.setText(nome);
                    textViewNode.setRenderable(viewRenderable);
                });

        Toast.makeText(getContext(), "finalizado ViewRenderable.builder()", Toast.LENGTH_LONG).show();

        return textViewNode;
    }
    public Renderable criarCubo(float x, float y, float z) {
        //WeakReference<ARVisualizerActivity> weakActivity = new WeakReference<>(this);
        //ARVisualizerActivity activity = weakActivity.get();
        Vector3 v = new Vector3(x, y, z);
        //if (activity != null) {

        MaterialFactory.makeOpaqueWithColor(getContext(), new Color(0, 0, 1, 1))
                .thenAccept(
                        material -> {
                            model = ShapeFactory.makeCube(
                                    v,
                                    new Vector3(0, (y / 2), 0),
                                    material
                            );
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(getContext(), "Unable to make cube. material", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        // sempre retorna null.. em que momento o Renderable model é criado e setado??
        return model;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


}