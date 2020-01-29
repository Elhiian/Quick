package com.app.elhiian.quick;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InicioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    View view;
    LinearLayout linearTaxis,linearMotos,linearAcarreos,linearMudanzas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_inicio, container, false);
        linearTaxis=view.findViewById(R.id.linearTaxi);
        linearMotos=view.findViewById(R.id.linearMoto);
        linearAcarreos=view.findViewById(R.id.linearAcarreo);
        linearMudanzas=view.findViewById(R.id.linearMudanza);

        linearTaxis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "buscar taxis", Toast.LENGTH_SHORT).show();
                verificarDestino("taxi");
            }
        });
        linearMotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "buscar motos", Toast.LENGTH_SHORT).show();
                verificarDestino("moto");
            }
        });

        linearAcarreos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "buscar piaggio", Toast.LENGTH_SHORT).show();
                verificarDestino("acarreo");
            }
        });

        linearMudanzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "buscar Mudanza", Toast.LENGTH_SHORT).show();
                verificarDestino("mudanza");
            }
        });


        return  view;
    }



    private void verificarDestino(final String action) {
        AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.modal_destino_desing,null);
        alBuilder.setView(view);
        final AlertDialog ventana=alBuilder.create();
        final EditText txtdestino=view.findViewById(R.id.edittextdestino);
        Button btnEnviarDestino=view.findViewById(R.id.btnEnviarDestino);
        Button btnSinDestino=view.findViewById(R.id.btnSinDestino);
        btnEnviarDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtdestino.getText().toString().length()>0){
                    Intent intent=new Intent(getActivity(),UbicationActivity.class);
                    intent.putExtra("action",action);
                    intent.putExtra("tipoubicacion","actual");
                    intent.putExtra("destino",txtdestino.getText().toString());
                    startActivity(intent);
                    ventana.dismiss();
                }else{
                    Toast.makeText(getActivity(), "Introdusca el destino", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSinDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),UbicationActivity.class);
                intent.putExtra("action",action);
                intent.putExtra("tipoubicacion","actual");
                intent.putExtra("destino","none");
                startActivity(intent);
                ventana.dismiss();
            }
        });
        ventana.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
