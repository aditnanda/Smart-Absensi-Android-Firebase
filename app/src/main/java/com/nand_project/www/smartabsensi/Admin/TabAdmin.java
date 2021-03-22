package com.nand_project.www.smartabsensi.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nand_project.www.smartabsensi.Admin.Data.DatabaseAdmin;
import com.nand_project.www.smartabsensi.R;


/**
 * Created by USER on 08-Dec-17.
 */

public class TabAdmin extends Fragment {
    private EditText email, password;
    private Button btnLogin;
    private TextView tv_about;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_admin, container, false);

        email = rootView.findViewById(R.id.login_email);
        password = rootView.findViewById(R.id.login_password);
        tv_about = rootView.findViewById(R.id.about);

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.aditnanda.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btnLogin = rootView.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("1")&&password.getText().toString().equals("1")){
                    Intent intent = new Intent(getActivity(),DatabaseAdmin.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Email atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }



}
